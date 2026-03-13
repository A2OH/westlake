#!/usr/bin/env python3
"""Create GitHub issues for Tier A (pure Java) shim implementation tasks.

Each issue contains:
- Class name, file path, current stub analysis
- API methods from api_compat.db
- Self-validation test specification
- Labels: tier-a, todo, non-ui

Usage:
    python3 scripts/create_issues.py                # create all Tier A issues
    python3 scripts/create_issues.py --dry-run      # preview without creating
    python3 scripts/create_issues.py --tier b       # create Tier B issues
"""

import os
import re
import sqlite3
import subprocess
import sys

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DB_PATH = os.path.join(PROJECT_ROOT, "database", "api_compat.db")
SHIM_ROOT = os.path.join(PROJECT_ROOT, "shim", "java")
REPO = "A2OH/harmony-android-guide"

# Tier A: Pure Java data classes — no native/OHOS dependency
TIER_A_CLASSES = [
    # android.os
    ("android.os", "Bundle", "HashMap-backed key-value store"),
    ("android.os", "BaseBundle", "Base class for Bundle"),
    ("android.os", "PersistableBundle", "Bundle that survives process death"),
    ("android.os", "Message", "Handler message with what/arg1/arg2/obj"),
    ("android.os", "Parcel", "Serialization container (pure Java subset)"),
    ("android.os", "PatternMatcher", "String pattern matching (LITERAL, PREFIX, GLOB)"),
    ("android.os", "ResultReceiver", "Callback wrapper"),
    # android.content
    ("android.content", "Intent", "Action/data/extras/component/categories"),
    ("android.content", "IntentFilter", "Action/category/data matching"),
    ("android.content", "ContentValues", "Map<String,Object> for DB inserts"),
    ("android.content", "ComponentName", "Package + class name pair"),
    ("android.content", "ContentUris", "Uri helper: withAppendedId, parseId"),
    ("android.content", "UriMatcher", "Tree-based URI routing"),
    # android.net
    ("android.net", "Uri", "Immutable URI with parse/build/query"),
    ("android.net", "UrlQuerySanitizer", "Query parameter sanitization"),
    # android.util
    ("android.util", "SparseArray", "int→Object map (no boxing)"),
    ("android.util", "SparseBooleanArray", "int→boolean map"),
    ("android.util", "SparseIntArray", "int→int map"),
    ("android.util", "SparseLongArray", "int→long map"),
    ("android.util", "ArrayMap", "Memory-efficient Map<K,V>"),
    ("android.util", "ArraySet", "Memory-efficient Set<E>"),
    ("android.util", "Base64", "Base64 encode/decode"),
    ("android.util", "Pair", "Immutable pair (first, second)"),
    ("android.util", "Size", "Immutable width x height (int)"),
    ("android.util", "SizeF", "Immutable width x height (float)"),
    ("android.util", "TypedValue", "Resource value with type/data/density"),
    ("android.util", "Log", "Logging (println to stdout)"),
    ("android.util", "Patterns", "Common regex patterns (EMAIL, URL, IP)"),
    ("android.util", "JsonReader", "Streaming JSON parser"),
    ("android.util", "JsonWriter", "Streaming JSON writer"),
    ("android.util", "Xml", "XML pull parser factory"),
    ("android.util", "LruCache", "Least-recently-used cache"),
    # android.text
    ("android.text", "TextUtils", "isEmpty, join, split, htmlEncode, etc."),
    ("android.text", "SpannableString", "String with span markup"),
    ("android.text", "SpannableStringBuilder", "Mutable spannable string"),
    ("android.text", "Html", "HTML to/from Spanned"),
    # android.graphics
    ("android.graphics", "Color", "ARGB color int utilities"),
    ("android.graphics", "Point", "Immutable int x,y"),
    ("android.graphics", "PointF", "Immutable float x,y"),
    ("android.graphics", "Rect", "int left,top,right,bottom rectangle"),
    ("android.graphics", "RectF", "float left,top,right,bottom rectangle"),
    ("android.graphics", "Matrix", "3x3 transformation matrix"),
    # android.database
    ("android.database", "MatrixCursor", "In-memory Cursor from arrays"),
    ("android.database", "MergeCursor", "Concatenates multiple Cursors"),
    ("android.database", "DatabaseUtils", "SQL utility methods"),
]

# Tier B: I/O with Java fallback
TIER_B_CLASSES = [
    ("android.content", "SharedPreferences", "Key-value prefs (HashMap + file)"),
    ("android.database.sqlite", "SQLiteDatabase", "SQL database (needs JDBC or stub)"),
    ("android.database.sqlite", "SQLiteOpenHelper", "DB lifecycle manager"),
    ("android.os", "Environment", "External storage dirs (File-based)"),
    ("android.os", "StatFs", "Filesystem stats"),
    ("android.os", "Handler", "Message queue + runnable dispatch"),
    ("android.os", "Looper", "Thread message loop"),
    ("android.os", "HandlerThread", "Thread with Looper"),
    ("android.os", "AsyncTask", "Background thread + UI callback"),
    ("android.os", "CountDownTimer", "Periodic tick timer"),
    ("android.os", "SystemClock", "uptimeMillis, elapsedRealtime"),
    ("android.content", "ContentProvider", "CRUD interface"),
    ("android.content", "ContentResolver", "Client to ContentProvider"),
    ("android.util", "AtomicFile", "Atomic file write with backup"),
    ("android.app", "Application", "App singleton"),
]


def get_shim_path(pkg, cls):
    """Convert package.Class to file path."""
    parts = pkg.replace(".", "/")
    return os.path.join(SHIM_ROOT, parts, cls + ".java")


def analyze_stub(filepath):
    """Count return null/0/false stubs in a file."""
    if not os.path.exists(filepath):
        return {"exists": False, "lines": 0, "stubs": 0, "methods": 0}
    with open(filepath) as f:
        content = f.read()
    lines = content.count("\n") + 1
    stubs = content.count("return null;") + content.count("return 0;") + content.count("return false;")
    methods = len(re.findall(r'(public|protected)\s+\w+\s+\w+\s*\(', content))
    return {"exists": True, "lines": lines, "stubs": stubs, "methods": methods}


def get_api_details(pkg, cls):
    """Get API methods from api_compat.db."""
    conn = sqlite3.connect(DB_PATH)
    rows = conn.execute("""
        SELECT a.name, a.signature, a.kind, m.score, m.mapping_type,
               COALESCE(oa.name, '') as oh_name
        FROM api_mappings m
        JOIN android_apis a ON m.android_api_id = a.id
        JOIN android_types t ON a.type_id = t.id
        JOIN android_packages p ON t.package_id = p.id
        LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
        WHERE p.name = ? AND t.full_name = ?
        ORDER BY a.kind, m.score DESC
    """, (pkg, cls)).fetchall()
    conn.close()
    return rows


def build_test_spec(pkg, cls, desc):
    """Generate a self-validation test specification."""
    fqn = f"{pkg}.{cls}"
    tests = {
        "Bundle": [
            "putString/getString round-trip",
            "putInt/getInt round-trip",
            "putBoolean/getBoolean round-trip",
            "containsKey returns true after put",
            "size() tracks entries",
            "remove() works",
            "clear() empties bundle",
        ],
        "Intent": [
            "setAction/getAction round-trip",
            "putExtra/getStringExtra round-trip",
            "putExtra/getIntExtra round-trip",
            "setData/getData (Uri) round-trip",
            "setComponent/getComponent round-trip",
            "addCategory/getCategories",
            "hasExtra returns true after put",
        ],
        "ContentValues": [
            "put/getAsString round-trip",
            "put/getAsInteger round-trip",
            "containsKey returns true after put",
            "size() tracks entries",
            "remove() works",
            "keySet() returns all keys",
        ],
        "Uri": [
            'parse("http://example.com/path?q=1#frag")',
            "getScheme() == http",
            "getHost() == example.com",
            "getPath() == /path",
            "getQuery() == q=1",
            "getFragment() == frag",
            "getQueryParameter('q') == 1",
            "Builder creates valid URI",
        ],
        "SparseArray": [
            "put/get round-trip",
            "size() tracks entries",
            "indexOfKey finds key",
            "removeAt removes entry",
            "keyAt/valueAt work",
        ],
        "Base64": [
            "encode then decode returns original bytes",
            'encode("Hello") matches known Base64',
            "decode known Base64 returns expected bytes",
            "URL_SAFE flag works",
        ],
        "Log": [
            "Log.d() doesn't crash",
            "Log.e() doesn't crash",
            "Log.i() doesn't crash",
            "Log.isLoggable() returns true",
        ],
        "TextUtils": [
            'isEmpty("") == true',
            'isEmpty(null) == true',
            'isEmpty("x") == false',
            'join(",", ["a","b","c"]) == "a,b,c"',
            "htmlEncode escapes < > & \"",
        ],
        "Color": [
            "rgb(255,0,0) == 0xFFFF0000",
            "red(0xFFFF0000) == 255",
            "green(0xFF00FF00) == 255",
            "blue(0xFF0000FF) == 255",
            "alpha(0x80000000) == 128",
            'parseColor("#FF0000") == red',
        ],
        "Rect": [
            "constructor sets left,top,right,bottom",
            "width() == right - left",
            "height() == bottom - top",
            "contains(x,y) works",
            "intersect modifies rect",
            "isEmpty() for zero rect",
        ],
        "Pair": [
            "create(a,b).first == a",
            "create(a,b).second == b",
            "equals works for same values",
        ],
        "LruCache": [
            "put/get round-trip",
            "evicts oldest when over maxSize",
            "size() tracks entries",
            "remove() works",
        ],
        "ComponentName": [
            "constructor(pkg, cls) stores both",
            "getPackageName() returns pkg",
            "getClassName() returns cls",
            "flattenToString() format",
            "unflattenFromString() round-trip",
        ],
    }
    default_tests = [
        "instantiation doesn't crash",
        "key methods return non-null/sensible defaults",
        "round-trip for any get/set pairs",
    ]
    return tests.get(cls, default_tests)


def create_issue(pkg, cls, desc, tier, dry_run=False):
    """Create a GitHub issue for one class."""
    fqn = f"{pkg}.{cls}"
    filepath = get_shim_path(pkg, cls)
    relpath = os.path.relpath(filepath, PROJECT_ROOT)
    stub_info = analyze_stub(filepath)
    apis = get_api_details(pkg, cls)
    tests = build_test_spec(pkg, cls, desc)

    # Build API table
    api_table = "| Method | Signature | Score | Mapping |\n|--------|-----------|-------|---------|\n"
    for name, sig, kind, score, mtype, oh_name in apis[:20]:
        api_table += f"| `{name}` | `{sig}` | {score:.1f} | {mtype} |\n"
    if len(apis) > 20:
        api_table += f"\n_... and {len(apis) - 20} more_\n"
    if not apis:
        api_table = "_No API mappings in database — implement based on AOSP behavior._\n"

    # Build test checklist
    test_list = "\n".join(f"- [ ] {t}" for t in tests)

    body = f"""## Class: `{fqn}`

**Tier:** {tier.upper()} (pure Java, self-validating)
**Description:** {desc}
**File:** `{relpath}`
**Status:** {'Stub exists' if stub_info['exists'] else 'File missing'} — {stub_info['lines']} lines, {stub_info['stubs']} null/0 stubs, {stub_info['methods']} methods

## Task

Replace stub implementations (`return null`, `return 0`, `return false`) with **real Java logic**.

For this class, use standard Java collections (`HashMap`, `ArrayList`, etc.) — no JNI or OHBridge needed.

## API Methods (from api_compat.db)

{api_table}

## Self-Validation Tests

Add to `test-apps/02-headless-cli/src/HeadlessTest.java`:

{test_list}

## Verification

```bash
cd test-apps && ./run-local-tests.sh headless
# Must compile cleanly and all tests pass
# Existing tests must not regress
```

## Completion

1. Implement real logic in `{relpath}`
2. Add test method `test{cls}()` to HeadlessTest.java
3. Verify with `./run-local-tests.sh headless`
4. Commit to branch `shim/{cls.lower()}`
5. Close this issue with test results
"""

    title = f"[SHIM] Implement {fqn}"
    labels = f"todo,tier-{tier},non-ui,shim"

    if dry_run:
        print(f"  Would create: {title}")
        print(f"    File: {relpath} ({stub_info['lines']} lines, {stub_info['stubs']} stubs)")
        print(f"    APIs in DB: {len(apis)}")
        print(f"    Tests: {len(tests)}")
        return None

    # Check if issue already exists
    result = subprocess.run(
        ["gh", "issue", "list", "--repo", REPO, "--search", f"[SHIM] Implement {fqn}", "--json", "number", "--limit", "1"],
        capture_output=True, text=True
    )
    if result.stdout.strip() not in ("[]", ""):
        import json
        existing = json.loads(result.stdout)
        if existing:
            print(f"  SKIP: {fqn} — issue #{existing[0]['number']} already exists")
            return existing[0]["number"]

    result = subprocess.run(
        ["gh", "issue", "create", "--repo", REPO,
         "--title", title,
         "--label", labels,
         "--body", body],
        capture_output=True, text=True
    )
    if result.returncode == 0:
        url = result.stdout.strip()
        num = url.split("/")[-1]
        print(f"  Created: #{num} — {title}")
        return num
    else:
        print(f"  ERROR creating {fqn}: {result.stderr}")
        return None


def main():
    dry_run = "--dry-run" in sys.argv
    tier = "a"
    for arg in sys.argv[1:]:
        if arg.startswith("--tier"):
            tier = sys.argv[sys.argv.index(arg) + 1]

    classes = TIER_A_CLASSES if tier == "a" else TIER_B_CLASSES

    print(f"{'DRY RUN — ' if dry_run else ''}Creating Tier {tier.upper()} issues ({len(classes)} classes)")
    print(f"Repo: {REPO}")
    print()

    created = 0
    skipped = 0
    for pkg, cls, desc in classes:
        result = create_issue(pkg, cls, desc, tier, dry_run)
        if result:
            created += 1
        else:
            skipped += 1

    print(f"\nDone: {created} created, {skipped} skipped")


if __name__ == "__main__":
    main()
