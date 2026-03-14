# Android-to-OpenHarmony Migration — Distributed API Porting

## What This Project Is

This project runs **unmodified Android APKs on OpenHarmony** using:
1. **Dalvik VM** — KitKat-era VM ported to 64-bit (x86_64, OHOS aarch64, OHOS ARM32)
2. **Java Shim Layer** — 1,968 `android.*` stub classes (compile cleanly, but most return null/0)
3. **OHBridge** — JNI bridge routing Android API calls to OHOS native APIs

The stubs compile but don't work. **Your job: implement real logic in shim classes** so the APIs actually function correctly.

## Distributed Task Queue (GitHub Issues)

Multiple Claude Code sessions work in parallel, each picking tasks from GitHub Issues.

### How it works

```
GitHub Issues (task queue)          Multiple CC Workers
┌─────────────────────┐       ┌──────┐ ┌──────┐ ┌──────┐
│ [SHIM] Bundle  todo │──────▶│ CC#1 │ │ CC#2 │ │ CC#3 │
│ [SHIM] Intent  todo │       │      │ │      │ │      │
│ [SHIM] Uri     todo │       │ pick │ │ pick │ │ pick │
│ [SHIM] Color   done │       │ impl │ │ impl │ │ impl │
│ ...                 │       │ test │ │ test │ │ test │
└─────────────────────┘       │ push │ │ push │ │ push │
                              └──────┘ └──────┘ └──────┘
```

### Step 1: Pick a task
```bash
# List available tasks
gh issue list --repo A2OH/harmony-android-guide --label todo --label tier-a --limit 10

# Claim it (atomic: remove todo, add in-progress)
ISSUE=<NUMBER>
gh issue edit $ISSUE --repo A2OH/harmony-android-guide --remove-label todo --add-label in-progress
```

### Step 2: Read the issue
Each issue has:
- **Class name** and file path
- **API methods** to implement (from api_compat.db with scores)
- **Self-validation test** checklist
- **Current stub** analysis (how many methods return null/0)

### Step 3: Look up the skill for this class
```bash
# Query the DB for the right conversion skill file
sqlite3 database/api_compat.db "SELECT ap.skill FROM android_types at2 JOIN android_packages ap ON at2.package_id = ap.id WHERE at2.full_name = 'YourClassName'"
# Returns e.g. A2OH-LIFECYCLE, A2OH-DATA-LAYER, A2OH-UI-REWRITE, etc.
# Then read skills/<SKILL_NAME>.md for Android→OH conversion rules
```

Available skills:
| Skill | Covers |
|-------|--------|
| `A2OH-LIFECYCLE` | Activity, Service, BroadcastReceiver, Intent, Bundle, ContentProvider |
| `A2OH-UI-REWRITE` | View, Widget, Animation, Layout, Transition |
| `A2OH-DATA-LAYER` | SQLite, Cursor, Provider |
| `A2OH-DEVICE-API` | Sensors, Camera, Bluetooth, Location, Telephony, NFC |
| `A2OH-MEDIA` | MediaPlayer, Audio, DRM, Speech |
| `A2OH-NETWORKING` | Connectivity, WiFi, HTTP |
| `A2OH-JAVA-TO-ARKTS` | Text, Util, Graphics (pure logic), Annotation, ICU |
| `A2OH-CONFIG` | Print, Security, System services, WebKit |

### Step 4: Implement
1. Read the existing stub at `shim/java/android/<path>/<Class>.java`
2. Read the skill file for conversion rules and OH API mappings
3. Replace `return null` / `return 0` / `return false` with **real Java logic**
4. For pure-data classes (Tier A): use `HashMap`, `ArrayList`, standard Java — no JNI needed
5. Match AOSP method signatures exactly — apps depend on them
6. Look at the AOSP source for reference behavior if unsure

### Step 5: Write self-test
Add a test method to `test-apps/02-headless-cli/src/HeadlessTest.java`:
```java
static void testYourClass() {
    section("YourClass");
    YourClass obj = new YourClass();
    obj.putFoo("key", "value");
    check("putFoo/getFoo round-trip", "value".equals(obj.getFoo("key")));
}
```
Add `testYourClass();` to the `main()` method.

### Step 6: Verify
```bash
# This compiles ALL shims + mock bridge + tests and runs them
cd test-apps && ./run-local-tests.sh headless

# MUST: compile cleanly, new tests pass, existing tests don't regress
```

### Step 7: Complete
```bash
# Create branch, commit, push
git checkout -b shim/<class-name-lowercase>
git add shim/java/android/<path>/<Class>.java
git add test-apps/02-headless-cli/src/HeadlessTest.java
git commit -m "Implement <ClassName> shim with self-validation tests"
git push origin shim/<class-name-lowercase>

# Close the issue
gh issue close $ISSUE --repo A2OH/harmony-android-guide \
  --comment "Implemented and tested. Branch: shim/<class-name-lowercase>"
gh issue edit $ISSUE --repo A2OH/harmony-android-guide --remove-label in-progress --add-label done
```

## Environment Setup (for new CC workers)

### Prerequisites
```bash
# Clone the repo
git clone https://github.com/A2OH/harmony-android-guide.git
cd harmony-android-guide

# Verify Java
javac -version   # Need JDK 8+ (JDK 21 works)
java -version

# Verify GitHub CLI
gh auth status   # Must be authenticated

# Verify test infrastructure
ls test-apps/run-local-tests.sh
ls test-apps/mock/com/ohos/shim/bridge/OHBridge.java
ls test-apps/02-headless-cli/src/HeadlessTest.java
```

### Run baseline tests
```bash
cd test-apps && ./run-local-tests.sh headless
# Note the pass/fail counts — your changes must not increase failures
```

### Understanding the codebase
- `shim/java/` — 1,968 Java stub files mirroring Android API surface
- `test-apps/mock/` — Mock OHBridge for JVM testing (no OHOS device needed)
- `test-apps/02-headless-cli/src/HeadlessTest.java` — All self-validation tests
- `database/api_compat.db` — SQLite DB with 4,617 Android API types and OH mappings
- `scripts/create_issues.py` — Task generator (creates GitHub issues from DB)

### If compilation fails
The test runner compiles ALL shim files together. If you get errors from files you didn't change, those are pre-existing. Your changes must not add new errors. If you need to fix a pre-existing error to unblock your work, include that fix in your commit.

## Tier Definitions

| Tier | What | Dependency | Priority |
|------|------|------------|----------|
| **A** | Pure Java data structures | None | **DO FIRST** |
| **B** | I/O with Java fallback | File system | Second |
| **C** | System service wrappers | OHBridge | Third |
| **D** | UI components | ArkUI | Last (skip for now) |

### Tier A examples (pure Java, self-validating)
Bundle, Intent, ContentValues, Uri, SparseArray, Base64, TextUtils, Color, Rect, Pair, LruCache, ComponentName, ArrayMap, Log, MatrixCursor

### Tier B examples (I/O, Java fallback)
SharedPreferences (→HashMap+file), SQLite (→in-memory), Environment, Handler, Looper, SystemClock, AtomicFile

## Key Rules

- **Don't break existing tests** — always run full suite before pushing
- **One class per issue** — one branch per class, keeps merges clean
- **Match AOSP signatures exactly** — apps compiled against real Android SDK
- **Use pure Java for Tier A/B** — no JNI, no OHBridge, no native code
- **Self-validating** — every shim must include tests proving it works
- **Check for conflicts** — if your branch can't merge cleanly, rebase on main

## Worker Optimization

- **Claim 5-10 issues at once** — don't implement one-at-a-time
- **Launch parallel agents** — each agent implements one shim class independently
- **Verify baseline once** after the entire batch completes (not after each class)
- **Close all in batch**, then claim the next batch immediately
- With 5 parallel agents per CC session, 3 CC sessions = ~15 concurrent implementations
- The bottleneck is context window, not concurrency — keep agents focused and independent

```bash
# Example: claim a batch of 5
for n in 1 2 3 4 5; do
  gh issue edit $n --repo A2OH/harmony-android-guide --remove-label todo --add-label in-progress
done

# Then use Claude Code agents to implement all 5 in parallel
# Verify baseline once after all 5 are done
cd test-apps && ./run-local-tests.sh headless

# Close all 5
for n in 1 2 3 4 5; do
  gh issue close $n --repo A2OH/harmony-android-guide --comment "Implemented and tested"
done
```

## Orchestration

The task queue is managed by `scripts/create_issues.py`:
```bash
# Create Tier A issues
python3 scripts/create_issues.py

# Create Tier B issues (when Tier A is mostly done)
python3 scripts/create_issues.py --tier b

# Dry run (preview without creating)
python3 scripts/create_issues.py --dry-run

# Monitor progress
gh issue list --repo A2OH/harmony-android-guide --label done --json number | python3 -c "import json,sys; print(len(json.load(sys.stdin)),'done')"
gh issue list --repo A2OH/harmony-android-guide --label in-progress --json number | python3 -c "import json,sys; print(len(json.load(sys.stdin)),'in-progress')"
gh issue list --repo A2OH/harmony-android-guide --label todo --json number | python3 -c "import json,sys; print(len(json.load(sys.stdin)),'todo')"
```

## Project Key Paths

| What | Path |
|------|------|
| Shim Java sources | `shim/java/android/` |
| OHBridge (real) | `shim/java/com/ohos/shim/bridge/OHBridge.java` |
| OHBridge (mock for testing) | `test-apps/mock/com/ohos/shim/bridge/OHBridge.java` |
| Headless test harness | `test-apps/02-headless-cli/src/HeadlessTest.java` |
| UI test harness | `test-apps/03-ui-mockup/src/UITestApp.java` |
| Test runner script | `test-apps/run-local-tests.sh` |
| API compatibility database | `database/api_compat.db` |
| Issue generator | `scripts/create_issues.py` |
| Dalvik VM (x86_64) | `dalvik-port/build/dalvikvm` |
| Dalvik VM (OHOS ARM32) | `dalvik-port/build-ohos-arm32/dalvikvm` |
