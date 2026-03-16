#!/usr/bin/env python3
"""Analyze Android APK files and map their API usage against the api_compat.db.

Extracts method references from DEX files, filters to Android framework APIs,
and cross-references with the mapping database to produce a migration readiness report.
"""

import struct
import zipfile
import sqlite3
import json
import os
import sys
from collections import Counter, defaultdict

DB_FILE = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")

# Android framework package prefixes (filter app-internal classes)
FRAMEWORK_PREFIXES = (
    'Landroid/', 'Ljava/', 'Ljavax/', 'Ldalvik/',
    'Landroidx/', 'Lcom/google/android/',
    'Lcom/android/', 'Ljunit/',
)

# Map DEX type descriptors to DB package names
# DEX uses Landroid/app/Activity; format, DB uses android.app
def dex_type_to_package(dex_type):
    """Convert DEX type descriptor to Java package name."""
    # Landroid/app/Activity; -> android.app
    if dex_type.startswith('L') and dex_type.endswith(';'):
        full = dex_type[1:-1].replace('/', '.')
        parts = full.rsplit('.', 1)
        return parts[0] if len(parts) > 1 else full
    return dex_type

def dex_type_to_classname(dex_type):
    """Convert DEX type descriptor to simple class name."""
    if dex_type.startswith('L') and dex_type.endswith(';'):
        full = dex_type[1:-1].replace('/', '.')
        parts = full.rsplit('.', 1)
        return parts[1] if len(parts) > 1 else full
    return dex_type


def parse_dex_methods(dex_data):
    """Parse all method references from a DEX file.

    Returns list of (class_descriptor, method_name) tuples.
    """
    # Verify magic
    if dex_data[:4] != b'dex\n':
        return []

    # Header fields
    string_ids_size = struct.unpack_from('<I', dex_data, 56)[0]
    string_ids_off = struct.unpack_from('<I', dex_data, 60)[0]
    type_ids_size = struct.unpack_from('<I', dex_data, 64)[0]
    type_ids_off = struct.unpack_from('<I', dex_data, 68)[0]
    method_ids_size = struct.unpack_from('<I', dex_data, 88)[0]
    method_ids_off = struct.unpack_from('<I', dex_data, 92)[0]

    # Cache for string reads
    string_cache = {}

    def read_string(idx):
        if idx in string_cache:
            return string_cache[idx]
        str_off = struct.unpack_from('<I', dex_data, string_ids_off + idx * 4)[0]
        # Skip ULEB128 length prefix
        i = str_off
        while i < len(dex_data) and dex_data[i] & 0x80:
            i += 1
        i += 1
        # Find null terminator
        end = dex_data.index(0, i) if i < len(dex_data) else i
        try:
            s = dex_data[i:end].decode('utf-8', errors='replace')
        except Exception:
            s = ''
        string_cache[idx] = s
        return s

    # Cache for type reads
    type_cache = {}

    def read_type(idx):
        if idx in type_cache:
            return type_cache[idx]
        if idx >= type_ids_size:
            return '?'
        str_idx = struct.unpack_from('<I', dex_data, type_ids_off + idx * 4)[0]
        t = read_string(str_idx)
        type_cache[idx] = t
        return t

    methods = []
    for i in range(method_ids_size):
        off = method_ids_off + i * 8
        if off + 8 > len(dex_data):
            break
        class_idx = struct.unpack_from('<H', dex_data, off)[0]
        name_idx = struct.unpack_from('<I', dex_data, off + 4)[0]

        class_name = read_type(class_idx)
        method_name = read_string(name_idx)
        methods.append((class_name, method_name))

    return methods


def extract_apk_apis(apk_path):
    """Extract all Android framework API references from an APK.

    Returns dict with:
        - api_calls: Counter of (package, class, method) -> count
        - total_methods: total method refs in DEX
        - framework_methods: framework-only method refs
        - dex_count: number of DEX files
    """
    api_calls = Counter()
    total_methods = 0
    framework_methods = 0
    dex_count = 0

    try:
        with zipfile.ZipFile(apk_path) as z:
            dex_files = sorted([n for n in z.namelist() if n.endswith('.dex')])
            dex_count = len(dex_files)

            for dex_name in dex_files:
                dex_data = z.read(dex_name)
                methods = parse_dex_methods(dex_data)
                total_methods += len(methods)

                for class_desc, method_name in methods:
                    if any(class_desc.startswith(p) for p in FRAMEWORK_PREFIXES):
                        framework_methods += 1
                        pkg = dex_type_to_package(class_desc)
                        cls = dex_type_to_classname(class_desc)
                        api_calls[(pkg, cls, method_name)] += 1
    except Exception as e:
        print(f"  Error reading {apk_path}: {e}")

    return {
        'api_calls': api_calls,
        'total_methods': total_methods,
        'framework_methods': framework_methods,
        'dex_count': dex_count,
    }


# Subsystem classification for Android packages (mirrors import_android.py)
PACKAGE_SUBSYSTEM = {
    'android.app': 'App Framework', 'android.app.admin': 'App Framework',
    'android.app.job': 'App Framework', 'android.app.usage': 'App Framework',
    'android.app.slice': 'App Framework', 'android.app.role': 'App Framework',
    'android.appwidget': 'App Framework',
    'android.content': 'Content', 'android.content.pm': 'Package Manager',
    'android.content.res': 'Resources',
    'android.view': 'View', 'android.view.animation': 'Animation',
    'android.view.accessibility': 'Accessibility',
    'android.view.inputmethod': 'Input Method',
    'android.widget': 'Widget',
    'android.os': 'OS', 'android.system': 'OS',
    'android.net': 'Networking', 'android.net.http': 'Networking',
    'android.net.wifi': 'WiFi',
    'android.media': 'Media', 'android.media.audiofx': 'Media',
    'android.media.session': 'Media',
    'android.telephony': 'Telephony', 'android.telecom': 'Telephony',
    'android.bluetooth': 'Bluetooth', 'android.bluetooth.le': 'Bluetooth',
    'android.nfc': 'NFC',
    'android.location': 'Location',
    'android.hardware': 'Hardware', 'android.hardware.camera2': 'Camera',
    'android.hardware.biometrics': 'Biometrics',
    'android.hardware.fingerprint': 'Biometrics',
    'android.sensor': 'Sensors',
    'android.database': 'Database', 'android.database.sqlite': 'Database',
    'android.security': 'Security', 'android.security.keystore': 'Security',
    'android.graphics': 'Graphics', 'android.graphics.drawable': 'Graphics',
    'android.opengl': 'Graphics', 'android.renderscript': 'Graphics',
    'android.webkit': 'WebView',
    'android.animation': 'Animation', 'android.transition': 'Animation',
    'android.text': 'Text', 'android.text.style': 'Text',
    'android.provider': 'Provider',
    'android.util': 'Util',
    'android.accounts': 'Accounts',
    'android.print': 'Print',
    'android.speech': 'Voice', 'android.speech.tts': 'Voice',
    'android.service.notification': 'Notifications',
    'android.drm': 'DRM',
    'android.inputmethodservice': 'Input Method',
    'android.accessibilityservice': 'Accessibility',
    'java.net': 'Java Standard', 'java.io': 'Java Standard',
    'java.lang': 'Java Standard', 'java.util': 'Java Standard',
    'java.security': 'Java Standard', 'java.math': 'Java Standard',
    'java.text': 'Java Standard', 'java.nio': 'Java Standard',
    'java.sql': 'Java Standard', 'javax.net': 'Java Standard',
    'javax.crypto': 'Java Standard', 'javax.security': 'Java Standard',
    'dalvik': 'Dalvik',
    'androidx.core': 'AndroidX', 'androidx.appcompat': 'AndroidX',
    'androidx.fragment': 'AndroidX', 'androidx.recyclerview': 'AndroidX',
    'androidx.lifecycle': 'AndroidX', 'androidx.activity': 'AndroidX',
    'androidx.annotation': 'AndroidX',
}

def classify_subsystem(package):
    """Classify a package into a subsystem."""
    # Try exact match first
    if package in PACKAGE_SUBSYSTEM:
        return PACKAGE_SUBSYSTEM[package]
    # Try prefix match (longest first)
    best_match = None
    best_len = 0
    for prefix, sub in PACKAGE_SUBSYSTEM.items():
        if package.startswith(prefix) and len(prefix) > best_len:
            best_match = sub
            best_len = len(prefix)
    return best_match or 'Other'


def lookup_db_mappings(api_calls, db_path):
    """Look up each API call in the database.

    Returns dict mapping (pkg, cls, method) -> {
        'found': bool,
        'score': float,
        'mapping_type': str,
        'effort': str,
        'oh_api': str or None,
        'subsystem': str,
    }
    """
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    results = {}

    # Build lookup: preload relevant Android APIs
    # Get all unique class names from the APK
    class_names = set()
    for pkg, cls, method in api_calls:
        class_names.add(cls)

    # Query DB for matching APIs
    db_apis = {}
    for cls in class_names:
        rows = c.execute("""
            SELECT a.name, a.signature, a.subsystem, a.compat_score, a.effort_level,
                   t.name as type_name, p.name as package_name,
                   m.mapping_type, m.score as mapping_score,
                   COALESCE(oa.name, '') as oh_name,
                   COALESCE(om.name, '') as oh_module
            FROM android_apis a
            JOIN android_types t ON a.type_id = t.id
            JOIN android_packages p ON t.package_id = p.id
            LEFT JOIN api_mappings m ON m.android_api_id = a.id
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            LEFT JOIN oh_modules om ON oa.module_id = om.id
            WHERE t.name = ?
        """, (cls,)).fetchall()

        for row in rows:
            key = (row[6], row[5], row[0])  # (package, type, method)
            db_apis[key] = {
                'subsystem': row[2] or classify_subsystem(row[6]),
                'compat_score': row[3],
                'effort': row[4],
                'mapping_type': row[7] or 'none',
                'mapping_score': row[8] or 0,
                'oh_api': row[9] if row[9] else None,
                'oh_module': row[10] if row[10] else None,
            }

    conn.close()

    for (pkg, cls, method), count in api_calls.items():
        key = (pkg, cls, method)
        if key in db_apis:
            results[key] = db_apis[key]
            results[key]['count'] = count
            results[key]['in_db'] = True
        else:
            results[key] = {
                'subsystem': classify_subsystem(pkg),
                'compat_score': None,
                'effort': 'unknown',
                'mapping_type': 'none',
                'mapping_score': 0,
                'oh_api': None,
                'oh_module': None,
                'count': count,
                'in_db': False,
            }

    return results


def analyze_single_apk(apk_path, db_path):
    """Full analysis of a single APK."""
    app_name = os.path.basename(apk_path).replace('.apk', '')
    print(f"\n{'='*60}")
    print(f"Analyzing: {app_name}")
    print(f"{'='*60}")

    # Step 1: Extract APIs from DEX
    extraction = extract_apk_apis(apk_path)
    api_calls = extraction['api_calls']

    print(f"  DEX files: {extraction['dex_count']}")
    print(f"  Total method refs: {extraction['total_methods']}")
    print(f"  Framework API refs: {extraction['framework_methods']}")
    print(f"  Unique framework APIs: {len(api_calls)}")

    if not api_calls:
        return None

    # Step 2: Look up in DB
    mappings = lookup_db_mappings(api_calls, db_path)

    # Step 3: Compute statistics
    subsystem_stats = defaultdict(lambda: {
        'total': 0, 'unique_apis': 0,
        'direct': 0, 'near': 0, 'partial': 0, 'composite': 0, 'none': 0,
        'in_db': 0, 'not_in_db': 0,
        'avg_score': 0, 'scores': [],
    })

    tier_counts = {'tier1_direct': 0, 'tier2_similar': 0,
                   'tier3_capable': 0, 'tier4_gap': 0}

    top_unmapped = []

    for (pkg, cls, method), info in mappings.items():
        sub = info['subsystem']
        count = info['count']
        ss = subsystem_stats[sub]
        ss['total'] += count
        ss['unique_apis'] += 1

        mtype = info['mapping_type']
        if mtype in ('direct',):
            ss['direct'] += 1
            tier_counts['tier1_direct'] += 1
        elif mtype in ('near',):
            ss['near'] += 1
            tier_counts['tier1_direct'] += 1
        elif mtype in ('partial',):
            ss['partial'] += 1
            tier_counts['tier2_similar'] += 1
        elif mtype in ('composite',):
            ss['composite'] += 1
            tier_counts['tier2_similar'] += 1
        else:
            ss['none'] += 1
            if info['in_db']:
                tier_counts['tier3_capable'] += 1
            else:
                tier_counts['tier4_gap'] += 1
            top_unmapped.append((pkg, cls, method, count, sub))

        if info['in_db']:
            ss['in_db'] += 1
        else:
            ss['not_in_db'] += 1

        if info['mapping_score']:
            ss['scores'].append(info['mapping_score'])

    # Compute averages
    for sub, ss in subsystem_stats.items():
        if ss['scores']:
            ss['avg_score'] = round(sum(ss['scores']) / len(ss['scores']), 2)
        del ss['scores']

    # Sort unmapped by frequency
    top_unmapped.sort(key=lambda x: x[3], reverse=True)

    total_unique = len(mappings)
    total_mapped = sum(1 for v in mappings.values() if v['mapping_type'] != 'none')
    coverage_pct = round(100 * total_mapped / total_unique, 1) if total_unique > 0 else 0

    # Print summary
    print(f"\n  --- Mapping Coverage ---")
    print(f"  Total unique APIs used: {total_unique}")
    print(f"  Mapped (direct+near+partial+composite): {total_mapped} ({coverage_pct}%)")
    print(f"  Unmapped: {total_unique - total_mapped}")

    print(f"\n  --- Tier Distribution ---")
    for tier, cnt in tier_counts.items():
        pct = round(100 * cnt / total_unique, 1) if total_unique > 0 else 0
        print(f"  {tier}: {cnt} ({pct}%)")

    print(f"\n  --- Subsystem Breakdown ---")
    print(f"  {'Subsystem':<20} {'Unique':>6} {'Direct':>7} {'Near':>5} {'Part':>5} {'Comp':>5} {'None':>5} {'Score':>6}")
    print(f"  {'-'*65}")
    for sub in sorted(subsystem_stats, key=lambda s: subsystem_stats[s]['unique_apis'], reverse=True):
        ss = subsystem_stats[sub]
        if ss['unique_apis'] > 0:
            print(f"  {sub:<20} {ss['unique_apis']:>6} {ss['direct']:>7} {ss['near']:>5} "
                  f"{ss['partial']:>5} {ss['composite']:>5} {ss['none']:>5} {ss['avg_score']:>6.1f}")

    print(f"\n  --- Top 15 Unmapped APIs (by frequency) ---")
    for pkg, cls, method, count, sub in top_unmapped[:15]:
        print(f"  [{sub}] {cls}.{method} (x{count})")

    return {
        'app_name': app_name,
        'dex_count': extraction['dex_count'],
        'total_method_refs': extraction['total_methods'],
        'framework_method_refs': extraction['framework_methods'],
        'unique_apis': total_unique,
        'mapped_apis': total_mapped,
        'coverage_pct': coverage_pct,
        'tier_distribution': tier_counts,
        'subsystem_stats': dict(subsystem_stats),
        'top_unmapped': [(pkg, cls, method, count, sub) for pkg, cls, method, count, sub in top_unmapped[:50]],
    }


def cross_app_comparison(results):
    """Print cross-app comparison matrix."""
    print(f"\n{'='*80}")
    print(f"CROSS-APP COMPARISON MATRIX")
    print(f"{'='*80}")

    # Coverage comparison
    print(f"\n{'App':<30} {'APIs':>6} {'Mapped':>7} {'Cover%':>7} {'T1':>5} {'T2':>5} {'T3':>5} {'T4':>5}")
    print(f"{'-'*76}")
    for r in sorted(results, key=lambda x: x['coverage_pct'], reverse=True):
        t = r['tier_distribution']
        print(f"{r['app_name']:<30} {r['unique_apis']:>6} {r['mapped_apis']:>7} "
              f"{r['coverage_pct']:>6.1f}% {t['tier1_direct']:>5} {t['tier2_similar']:>5} "
              f"{t['tier3_capable']:>5} {t['tier4_gap']:>5}")

    # Subsystem heat map
    all_subsystems = set()
    for r in results:
        all_subsystems.update(r['subsystem_stats'].keys())

    # Filter to interesting subsystems
    key_subsystems = ['App Framework', 'View', 'Widget', 'Content', 'OS',
                      'Networking', 'Media', 'Camera', 'Graphics', 'Location',
                      'Bluetooth', 'Security', 'Database', 'WebView',
                      'Notifications', 'Animation', 'Text', 'Telephony',
                      'Java Standard', 'AndroidX']

    print(f"\n--- Subsystem API Count by App ---")
    header = f"{'Subsystem':<16}" + "".join(f"{r['app_name'][:8]:>9}" for r in results)
    print(header)
    print("-" * len(header))
    for sub in key_subsystems:
        row = f"{sub:<16}"
        for r in results:
            cnt = r['subsystem_stats'].get(sub, {}).get('unique_apis', 0)
            row += f"{cnt:>9}"
        print(row)

    # Common unmapped APIs across apps
    unmapped_freq = Counter()
    for r in results:
        for pkg, cls, method, count, sub in r['top_unmapped']:
            unmapped_freq[(cls, method, sub)] += 1

    print(f"\n--- Most Common Unmapped APIs (across all apps) ---")
    print(f"{'Class.Method':<50} {'Subsystem':<20} {'Apps':>5}")
    print("-" * 75)
    for (cls, method, sub), app_count in unmapped_freq.most_common(30):
        print(f"{cls}.{method}"[:49] + f" {sub:<20} {app_count:>5}")


def main():
    apk_paths = sys.argv[1:]
    if not apk_paths:
        # Default: all APKs in home dir
        apk_paths = sorted([
            os.path.join(os.path.expanduser('~'), f)
            for f in os.listdir(os.path.expanduser('~'))
            if f.endswith('.apk')
        ])

    if not apk_paths:
        print("No APK files found. Usage: python3 analyze_apk.py <apk1> [apk2] ...")
        sys.exit(1)

    print(f"Analyzing {len(apk_paths)} APK files against {DB_FILE}")

    results = []
    for apk_path in apk_paths:
        if not os.path.exists(apk_path):
            print(f"Skipping {apk_path}: file not found")
            continue
        result = analyze_single_apk(apk_path, DB_FILE)
        if result:
            results.append(result)

    if len(results) > 1:
        cross_app_comparison(results)

    # Save results to JSON
    output_path = os.path.expanduser(
        "~/android-to-openharmony-migration/database/apk_analysis_results.json"
    )
    # Make JSON serializable
    serializable = []
    for r in results:
        sr = dict(r)
        sr['top_unmapped'] = [
            {'package': pkg, 'class': cls, 'method': method, 'count': count, 'subsystem': sub}
            for pkg, cls, method, count, sub in r['top_unmapped']
        ]
        serializable.append(sr)

    with open(output_path, 'w') as f:
        json.dump(serializable, f, indent=2)
    print(f"\nResults saved to {output_path}")


if __name__ == '__main__':
    main()
