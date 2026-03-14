# Android-to-OpenHarmony Migration

Run **unmodified Android APKs on OpenHarmony** by porting the Android API surface through a Java shim layer, a Dalvik VM, and a JNI bridge to OHOS native APIs.

```
 Android APK
     |
 [Dalvik VM]        ← KitKat-era VM, ported to 64-bit (x86_64, OHOS aarch64, ARM32)
     |
 [Java Shim Layer]  ← 1,968 android.* stub classes (this repo)
     |
 [OHBridge / JNI]   ← Routes Android API calls to OHOS native APIs
     |
 OpenHarmony OS
```

## Project Status

| Component | Status |
|-----------|--------|
| Dalvik VM (x86_64 Linux) | Working — runs .dex files |
| Dalvik VM (OHOS ARM32) | Cross-compiles, needs device testing |
| Java Shim Layer | 1,968 stubs compile; ~50 implemented with real logic |
| OHBridge (JNI) | Skeleton — routes calls but most are no-ops |
| Test Harness | 497/502 headless tests pass |
| Orchestrator Dashboard | Live at GitHub Pages |

## Architecture

### Shim Tiers

Every Android API class is classified into one of four implementation tiers:

| Tier | What | Dependency | Count | Example Classes |
|------|------|------------|-------|-----------------|
| **A** | Pure Java data structures | None | 314 classes / 1,316 APIs | Bundle, Intent, Uri, SparseArray, LruCache |
| **B** | I/O with Java fallback | File system | 946 classes / 2,212 APIs | SharedPreferences, Handler, Looper, SQLiteDatabase |
| **C** | System service wrappers | OHBridge (JNI) | 3,445 classes / 43,254 APIs | LocationManager, BluetoothAdapter, Camera |
| **D** | UI components | ArkUI | 613 classes / 10,507 APIs | Activity, View, TextView, RecyclerView |

**Total: 5,318 classes, 57,289 APIs**

### Repository Layout

```
shim/java/android/          ← 1,968 Java stub files mirroring Android API
shim/java/com/ohos/shim/    ← OHBridge JNI bridge (real implementation)
test-apps/
  mock/                     ← Mock OHBridge for JVM testing (no device needed)
  02-headless-cli/          ← Headless test harness (497 tests)
  03-ui-mockup/             ← UI test harness (59 tests)
  run-local-tests.sh        ← Test runner script
database/
  api_compat.db             ← SQLite DB: 57K APIs, tier classifications, OH mappings
  generate_shims.py         ← Stub generator
scripts/
  create_issues.py          ← GitHub issue generator from DB
frontend/                   ← React dashboard (GitHub Pages)
  public/tier-classes.json  ← All 5,318 classes by tier (from DB)
skills/                     ← Conversion skill files for CC workers
dalvik-port/                ← Dalvik VM build artifacts
```

## Quick Start

### Prerequisites

```bash
# Java (JDK 8+, JDK 21 works)
javac -version
java -version

# GitHub CLI (authenticated)
gh auth status

# Claude Code (for automated workers)
claude --version
```

### Run Tests

```bash
# Clone
git clone https://github.com/A2OH/harmony-android-guide.git
cd harmony-android-guide

# Run headless tests (compiles all shims + mock bridge + tests)
cd test-apps && ./run-local-tests.sh headless
# Expected: 497/502 pass

# Run UI tests
./run-local-tests.sh ui
# Expected: 59 pass, 2 known failures (click events)
```

### Implement a Shim (Manual)

```bash
# 1. Find a class to implement
gh issue list --repo A2OH/harmony-android-guide --label todo --label tier-a --limit 10

# 2. Claim it
gh issue edit <NUMBER> --repo A2OH/harmony-android-guide \
  --remove-label todo --add-label in-progress

# 3. Read the stub
cat shim/java/android/os/Bundle.java

# 4. Look up the conversion skill
sqlite3 database/api_compat.db \
  "SELECT ap.skill FROM android_types at
   JOIN android_packages ap ON at.package_id = ap.id
   WHERE at.full_name = 'Bundle'"
# → A2OH-LIFECYCLE
cat skills/A2OH-LIFECYCLE.md

# 5. Implement real logic (replace return null/0/false)
vim shim/java/android/os/Bundle.java

# 6. Verify tests still pass
cd test-apps && ./run-local-tests.sh headless

# 7. Commit and close
git add shim/java/android/os/Bundle.java
git commit -m "Implement Bundle shim"
git push origin main
gh issue close <NUMBER> --repo A2OH/harmony-android-guide \
  --comment "Implemented and tested"
```

---

## Automated Workers (Claude Code)

The project uses a distributed task queue via GitHub Issues. Multiple Claude Code sessions work in parallel, each claiming issues, implementing shims, and closing them when done.

Worker scripts and orchestration tools are in a separate repo: **[A2OH/a2oh-orchestrator](https://github.com/A2OH/a2oh-orchestrator)**

```bash
# Quick start — launch a worker
git clone https://github.com/A2OH/a2oh-orchestrator.git
cd a2oh-orchestrator
./a2oh-worker.sh /tmp/worker1

# Or run 3 workers in parallel
./a2oh-worker.sh /tmp/w1 10 &
./a2oh-worker.sh /tmp/w2 10 &
./a2oh-worker.sh /tmp/w3 10 &
```

See the [orchestrator README](https://github.com/A2OH/a2oh-orchestrator#readme) for full docs on worker setup, loop mode, tier selection, and throughput estimates.

---

## Orchestrator Dashboard

The Orchestrator is a React web app deployed to GitHub Pages that provides:

- **Real-time issue status** — auto-refreshes every 30s from GitHub API
- **Progress tracking** — completion percentage, per-tier statistics
- **Issue management** — Claim/Done/Fail/Release/Reopen buttons per issue
- **Batch issue creation** — select classes by tier from DB (5,318 classes), create issues in bulk
- **Single issue creation** — free-form class name with auto skill detection
- **Search/filter** — filter by status (todo/in-progress/done/failed) and tier (A/B/C/D)

---

## Conversion Skills

Each Android package maps to a skill file that describes how to convert Android APIs to OpenHarmony equivalents:

| Skill | Covers | File |
|-------|--------|------|
| A2OH-LIFECYCLE | Activity, Service, Intent, Bundle, ContentProvider | `skills/A2OH-LIFECYCLE.md` |
| A2OH-UI-REWRITE | View, Widget, Animation, Layout | `skills/A2OH-UI-REWRITE.md` |
| A2OH-DATA-LAYER | SQLite, Cursor, ContentProvider | `skills/A2OH-DATA-LAYER.md` |
| A2OH-DEVICE-API | Sensors, Camera, Bluetooth, Location, NFC | `skills/A2OH-DEVICE-API.md` |
| A2OH-MEDIA | MediaPlayer, Audio, DRM, Speech | `skills/A2OH-MEDIA.md` |
| A2OH-NETWORKING | Connectivity, WiFi, HTTP | `skills/A2OH-NETWORKING.md` |
| A2OH-JAVA-TO-ARKTS | Text, Util, Graphics (pure logic) | `skills/A2OH-JAVA-TO-ARKTS.md` |
| A2OH-CONFIG | Print, Security, System services, WebKit | `skills/A2OH-CONFIG.md` |

CC workers automatically look up the right skill:
```bash
sqlite3 database/api_compat.db \
  "SELECT ap.skill FROM android_types at
   JOIN android_packages ap ON at.package_id = ap.id
   WHERE at.full_name = 'BluetoothAdapter'"
# → A2OH-DEVICE-API
```

---

## Database

`database/api_compat.db` is a SQLite database with the complete Android API surface:

| Table | Rows | Description |
|-------|------|-------------|
| `android_packages` | 137 | Package names + skill mappings |
| `android_types` | 4,617 | Classes/interfaces/enums |
| `android_apis` | 57,289 | Methods, fields, constructors |
| `api_mappings` | 57,289 | OH equivalents, scores, tier classification |
| `oh_packages` | — | OpenHarmony packages |
| `oh_types` | — | OpenHarmony types |
| `oh_apis` | — | OpenHarmony APIs |

### Useful Queries

```bash
# Count APIs per tier
sqlite3 database/api_compat.db \
  "SELECT mapping_tier, COUNT(*) FROM api_mappings GROUP BY mapping_tier"

# Find all methods for a class
sqlite3 database/api_compat.db \
  "SELECT a.name, a.signature, m.score, m.mapping_tier
   FROM api_mappings m
   JOIN android_apis a ON m.android_api_id = a.id
   JOIN android_types t ON a.type_id = t.id
   WHERE t.full_name = 'Bundle' ORDER BY m.score DESC"

# List classes with highest API counts
sqlite3 database/api_compat.db \
  "SELECT t.full_name, COUNT(*) as cnt
   FROM android_apis a JOIN android_types t ON a.type_id = t.id
   GROUP BY t.full_name ORDER BY cnt DESC LIMIT 20"
```

---

## Dalvik VM Port

The KitKat-era Dalvik VM has been ported to run on modern 64-bit systems:

| Target | Status | Path |
|--------|--------|------|
| x86_64 Linux | Working | `dalvik-port/build/dalvikvm` |
| OHOS aarch64 | Compiles | `dalvik-port/build-ohos-aarch64/` |
| OHOS ARM32 | Compiles | `dalvik-port/build-ohos-arm32/dalvikvm` |

```bash
# Run a .dex file on x86_64 Linux
dalvik-port/build/dalvikvm -cp dalvik-port/hello.dex HelloAndroid
```

---

## Monitoring Progress

```bash
# Issue status counts
gh issue list --repo A2OH/harmony-android-guide --label done --json number \
  | python3 -c "import json,sys; print(len(json.load(sys.stdin)),'done')"
gh issue list --repo A2OH/harmony-android-guide --label in-progress --json number \
  | python3 -c "import json,sys; print(len(json.load(sys.stdin)),'in-progress')"
gh issue list --repo A2OH/harmony-android-guide --label todo --json number \
  | python3 -c "import json,sys; print(len(json.load(sys.stdin)),'todo')"

# Or use the Orchestrator dashboard
# https://a2oh.github.io/harmony-android-guide/
```

## License

This project is for research and educational purposes. Android is a trademark of Google LLC. OpenHarmony is a project of the OpenAtom Foundation.
