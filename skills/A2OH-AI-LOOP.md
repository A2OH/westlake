# A2OH Self-Evolving AI Coding Loop

## Concept

An autonomous AI agent loop that incrementally builds the Android→OpenHarmony shim layer by:

1. **Querying** the API mapping database for the next unimplemented API
2. **Generating** Java shim code + test cases
3. **Compiling** and running tests locally (mock bridge)
4. **Analyzing** failures and self-correcting
5. **Deploying** to OHOS QEMU VM for real validation
6. **Committing** passing code and moving to the next API

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    AI ORCHESTRATOR (Claude Code)                 │
│                                                                 │
│  ┌─────────────┐   ┌──────────────┐   ┌──────────────────────┐ │
│  │ API Picker   │──▶│ Code Generator│──▶│ Compile & Test (JVM) │ │
│  │ (DB query)   │   │ (shim + test) │   │ (mock bridge)        │ │
│  └──────┬──────┘   └──────────────┘   └──────────┬───────────┘ │
│         │                                         │             │
│         │              ┌──────────────┐           │             │
│         │              │ Self-Correct  │◀──────────┤             │
│         │              │ (fix errors)  │───────────┘ (if fail)  │
│         │              └──────────────┘                         │
│         │                                         │ (if pass)   │
│  ┌──────▼──────┐                        ┌────────▼───────────┐ │
│  │ Progress     │◀───────────────────────│ Device Validation   │ │
│  │ Tracker      │                        │ (QEMU OHOS VM)     │ │
│  │ (DB + git)   │                        └────────────────────┘ │
│  └─────────────┘                                                │
└─────────────────────────────────────────────────────────────────┘
```

## Loop Steps (Detailed)

### Step 1: Pick Next API

Query the database for the highest-impact unimplemented API:

```sql
-- Find best candidate: high score, callable, not yet shimmed
SELECT t.full_name AS android_class,
       a.name AS method_name,
       a.signature,
       a.kind,
       m.score,
       m.mapping_type,
       oa.name AS oh_api_name,
       om.name AS oh_module
FROM api_mappings m
JOIN android_apis a ON m.android_api_id = a.id
JOIN android_types t ON a.type_id = t.id
LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
LEFT JOIN oh_modules om ON oa.module_id = om.id
WHERE a.kind IN ('method', 'constructor')
  AND m.score >= 7                          -- Tier 1-2 only
  AND m.mapping_type IN ('direct', 'near')  -- feasible
  AND t.full_name NOT IN (                  -- skip already shimmed
      'android.util.Log',
      'android.content.SharedPreferences',
      -- ... list of completed classes
  )
ORDER BY m.score DESC, t.full_name
LIMIT 1;
```

Priority ranking factors:
1. **Score** (8-10 first) — highest feasibility
2. **App frequency** — classes used by >50% of top-1000 apps
3. **Dependency chain** — APIs that unblock other APIs
4. **Mapping type** — direct > near > partial > composite

### Step 2: Generate Shim Code

The AI reads:
- **Android API signature** from the database
- **OH API signature** from the database (linked via `api_mappings`)
- **Existing shim patterns** from `shim/java/android/` (learn conventions)
- **OH header files** for native API details when `needs_native = true`
- **SHIM-*.md skill files** for domain-specific guidance

Generates:
1. **Java shim class** (or methods added to existing class)
   - Same package/class/method signature as AOSP
   - Delegates to `OHBridge.xxx()` for native calls
   - Pure Java for data-only classes (Bundle, Intent, Uri, ContentValues)

2. **OHBridge additions** (if new native methods needed)
   - Java: `public static native xxx()` declaration
   - Mock: HashMap/stub implementation in `test-apps/mock/OHBridge.java`

3. **Test case** added to `HeadlessTest.java` or `UITestApp.java`
   - Exercise the full API surface of the new shim
   - Assert return values match expected Android behavior

### Step 3: Compile & Test (Local JVM)

```bash
# Compile all shim + mock + tests
./test-apps/run-local-tests.sh 2>&1
```

Parse output for:
- **Compilation errors** → fix imports, types, signatures
- **Test failures** → fix logic, mock behavior
- **Test passes** → proceed to Step 4

### Step 4: Self-Correct (if failures)

On failure, the AI:
1. Reads the error message
2. Identifies the root cause (type mismatch, missing method, wrong logic)
3. Edits the generated code
4. Re-runs compilation and tests
5. Repeats up to 5 times before flagging for human review

Common self-correction patterns:
- **Missing import** → add import statement
- **Type mismatch** → fix return type or cast
- **Mock doesn't support operation** → enhance mock or mark as device-only test
- **Logic error** → trace through Android API contract, fix implementation

### Step 5: Deploy to OHOS QEMU VM (Optional — device validation)

Prerequisites:
- OHOS built for `qemu-arm-linux-headless` target
- QEMU VM running with network access
- JVM/ART runtime available on OHOS (see "JVM on OHOS" section)

```bash
# Build DEX
javac -d build/ shim/java/**/*.java test-apps/02-headless-cli/src/*.java
d8 --output build/ build/**/*.class

# Deploy via hdc (OHOS device connector — works over network to QEMU VM)
hdc -t <device> file send build/classes.dex /data/local/tmp/shim_test/
hdc -t <device> file send liboh_bridge.so /data/local/tmp/shim_test/

# Run
hdc -t <device> shell "app_process \
    -Djava.library.path=/data/local/tmp/shim_test \
    -cp /data/local/tmp/shim_test/classes.dex \
    / HeadlessTest"
```

Parse device output — compare with mock results.

### Step 6: Commit & Track Progress

```bash
git add shim/java/android/<package>/<Class>.java
git add test-apps/02-headless-cli/src/HeadlessTest.java  # if modified
git add test-apps/mock/com/ohos/shim/bridge/OHBridge.java  # if modified
git commit -m "shim: add <Class>.<method> (score=X, mapping=Y)"
```

Update progress tracker (a simple DB table or status file):

```sql
-- Track what's been shimmed
CREATE TABLE IF NOT EXISTS shim_progress (
    android_api_id INTEGER PRIMARY KEY REFERENCES android_apis(id),
    status TEXT DEFAULT 'pending',  -- pending, shimmed, tested_mock, tested_device, failed
    shim_file TEXT,
    test_file TEXT,
    commit_hash TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Feasibility Analysis

### What works today (no QEMU needed)

The **local JVM loop** is fully functional right now:

```
┌──────────────────────────────────────────────────────┐
│  LOOP (runs today, no device needed):                │
│                                                      │
│  1. Query DB for next API  ───────────────────────┐  │
│  2. Read existing shim patterns                   │  │
│  3. Generate Java shim + mock + test              │  │
│  4. javac compile                                 │  │
│  5. java -cp build/ HeadlessTest                  │  │
│  6. Parse results                                 │  │
│  7. If fail → self-correct → goto 4              │  │
│  8. If pass → git commit → goto 1               │  │
│                                                      │
│  Coverage: ALL Tier 1-2 non-UI APIs (~22K APIs)     │
│  Validation: API surface correctness, not OH calls   │
└──────────────────────────────────────────────────────┘
```

This validates:
- Java API signatures match AOSP
- Internal logic (Bundle, Intent, Uri, ContentValues — pure Java)
- OHBridge call patterns are correct
- Test coverage exists for every shimmed API

### What needs QEMU

The **device validation loop** adds real OH API calls:
- SharedPreferences → actual `@ohos.data.preferences` persistence
- SQLiteDatabase → actual `OH_Rdb_*` operations
- Log → actual HiLog output
- Network → actual `OH_NetConn_*` + libcurl
- ArkUI views → actual native node rendering

### Critical blocker: JVM on OHOS

**Confirmed: OHOS has ZERO Java/JVM/Dalvik support.** ArkCompiler only runs ArkTS/JS → ARK bytecode (.abc). No dex2oat, no app_process, no Java runtime anywhere in the OHOS tree.

Options to run the Java shim layer on OHOS:

| Option | Effort | Fidelity | Notes |
|--------|--------|----------|-------|
| **Port OpenJDK Zero VM** | Medium | Full JVM | Zero is the portable interpreter, no JIT, compiles with any C++. Cross-compile for `aarch64-linux-ohos`. |
| **Java→ArkTS transpiler** | High | Native OHOS | Transpile Java shim classes → TypeScript → ArkTS. No VM needed. But loses "unmodified APK" goal. |
| **GraalVM native-image** | Medium | Static AOT | AOT compile entire shim to native ELF. No classloading, but shim classes are static. |
| **DEX→ARK bytecode translator** | Very High | Native runtime | Build a tool to convert .dex → .abc (Panda bytecode). Would need to map Java stdlib → ArkTS stdlib. |
| **Android ART port** | Very High | Full Dalvik | Port ART runtime to OHOS. Most faithful to original goal but massive engineering effort. |

**Recommended path for MVP**: **OpenJDK Zero VM**
- Zero VM is designed for portability — no target-specific assembly, no JIT
- Cross-compile with OHOS clang toolchain for `aarch64-linux-ohos`
- The shim Java classes run on it unchanged
- APK's DEX can be converted back to .class via dex2jar, then run on Zero VM
- Performance is interpreter-only (~10x slower than JIT), but functional

**Long-term path**: **DEX→ARK translator** or **ART port**
- For production quality, running on OHOS's native ArkCompiler runtime is ideal
- A DEX→ARK translator would convert Android bytecode to Panda bytecode
- The shim layer would need to be transpiled to ArkTS to act as the bridge

---

## Loop Orchestration Script

The orchestrator is a shell script + Claude Code that runs in a loop:

```bash
#!/bin/bash
# a2oh-loop.sh — Self-evolving shim development loop
# Usage: ./a2oh-loop.sh [--max-apis N] [--device]

MAX_APIS=${MAX_APIS:-50}
DEVICE_MODE=false

for arg in "$@"; do
    case $arg in
        --max-apis=*) MAX_APIS="${arg#*=}" ;;
        --device) DEVICE_MODE=true ;;
    esac
done

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
DB="$PROJECT_ROOT/database/api_compat.db"
PROGRESS_DB="$PROJECT_ROOT/database/shim_progress.db"

# Initialize progress DB
sqlite3 "$PROGRESS_DB" "
CREATE TABLE IF NOT EXISTS shim_progress (
    android_api_id INTEGER PRIMARY KEY,
    android_class TEXT,
    android_method TEXT,
    status TEXT DEFAULT 'pending',
    shim_file TEXT,
    attempts INTEGER DEFAULT 0,
    last_error TEXT,
    commit_hash TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);
"

apis_done=0

while [ $apis_done -lt $MAX_APIS ]; do
    echo "═══ Iteration $((apis_done + 1))/$MAX_APIS ═══"

    # Step 1: Pick next API (query main DB, exclude already done)
    NEXT_API=$(sqlite3 "$DB" "
        SELECT a.id, t.full_name, a.name, a.signature, m.score, m.mapping_type
        FROM api_mappings m
        JOIN android_apis a ON m.android_api_id = a.id
        JOIN android_types t ON a.type_id = t.id
        WHERE a.kind IN ('method','constructor')
          AND m.score >= 7
          AND m.mapping_type IN ('direct','near')
          AND a.id NOT IN (SELECT android_api_id FROM shim_progress)
        ORDER BY m.score DESC
        LIMIT 1;
    " --attach="$PROGRESS_DB" as progress)

    if [ -z "$NEXT_API" ]; then
        echo "No more APIs to shim at score >= 7"
        break
    fi

    API_ID=$(echo "$NEXT_API" | cut -d'|' -f1)
    CLASS=$(echo "$NEXT_API" | cut -d'|' -f2)
    METHOD=$(echo "$NEXT_API" | cut -d'|' -f3)

    echo "Target: $CLASS.$METHOD (score=$(echo "$NEXT_API" | cut -d'|' -f5))"

    # Step 2-4: Invoke Claude Code to generate + compile + test + self-correct
    # Claude Code handles the actual code generation and iterative fixing
    claude --print \
        "Generate a Java shim for $CLASS.$METHOD based on the API mapping in api_compat.db.
         Follow existing patterns in shim/java/android/.
         Add a test to HeadlessTest.java or UITestApp.java.
         Update the mock OHBridge if needed.
         Compile and run tests via test-apps/run-local-tests.sh.
         Fix any errors until tests pass.
         Do NOT commit — just make the code work." \
        2>&1 | tee /tmp/a2oh-loop-output.txt

    # Step 3 (verify): Run tests ourselves to confirm
    cd "$PROJECT_ROOT/test-apps"
    TEST_OUTPUT=$(./run-local-tests.sh 2>&1)
    HEADLESS_PASS=$(echo "$TEST_OUTPUT" | grep "Passed:" | head -1 | grep -o '[0-9]*')
    HEADLESS_FAIL=$(echo "$TEST_OUTPUT" | grep "Failed:" | head -1 | grep -o '[0-9]*')

    if [ "$HEADLESS_FAIL" = "0" ] || [ "$HEADLESS_FAIL" = "2" ]; then
        # 0 = all pass, 2 = known mock stubs
        STATUS="tested_mock"

        # Step 5: Device validation (optional)
        if $DEVICE_MODE; then
            # Deploy and test on QEMU OHOS
            cd "$PROJECT_ROOT/test-apps/02-headless-cli"
            DEVICE_OUTPUT=$(./build.sh all 2>&1)
            DEVICE_FAIL=$(echo "$DEVICE_OUTPUT" | grep "Failed:" | grep -o '[0-9]*')
            if [ "$DEVICE_FAIL" = "0" ]; then
                STATUS="tested_device"
            fi
        fi

        # Step 6: Record success
        sqlite3 "$PROGRESS_DB" "
            INSERT OR REPLACE INTO shim_progress
            (android_api_id, android_class, android_method, status, attempts, completed_at)
            VALUES ($API_ID, '$CLASS', '$METHOD', '$STATUS', 1, datetime('now'));
        "
        apis_done=$((apis_done + 1))
    else
        # Record failure for human review
        sqlite3 "$PROGRESS_DB" "
            INSERT OR REPLACE INTO shim_progress
            (android_api_id, android_class, android_method, status, attempts, last_error)
            VALUES ($API_ID, '$CLASS', '$METHOD', 'failed', 1, 'test failures');
        "
    fi

    echo ""
done

echo "═══ Loop Complete ═══"
echo "APIs shimmed: $apis_done"
sqlite3 "$PROGRESS_DB" "
    SELECT status, COUNT(*) FROM shim_progress GROUP BY status;
"
```

---

## Batching Strategy

Instead of one API at a time, batch by **class**:

```
Batch 1: android.os.Handler (11 methods, score 6-8)
  → Generate Handler.java with ALL methods
  → Generate HandlerTest section in HeadlessTest
  → One compile + test cycle for the whole class

Batch 2: android.os.Looper (8 methods, score 7-8)
  → Same pattern

Batch 3: android.net.ConnectivityManager (15 methods, score 7-9)
  → Same pattern
```

This is more efficient because:
- Methods within a class share internal state
- One import/constructor pattern per class
- Tests are naturally grouped

### Class-level priority queue (actual output from `a2oh-loop.sh --dry-run`)

```
Class                                          APIs  Score  Feasible  Types
android.opengl.GLES20                          184    8.0   184  direct
android.opengl.GLES30                          151    8.0   151  direct
android.system.Os                               83    8.6    83  near,direct
android.opengl.GLES31                           78    8.0    78  direct
android.icu.util.ULocale                        51    8.2    51  near,direct
android.icu.text.DateFormat                     41    8.7    41  near,direct
android.icu.text.NumberFormat                   39    8.9    39  near,direct
android.icu.text.UnicodeSet                     37    9.0    37  near,direct
android.icu.util.Calendar                       30    9.3    30  near,direct
android.media.MediaPlayer                       29    7.4    29  near,direct
android.content.ContextWrapper                  28    7.8    28  near,direct
android.net.wifi.WifiManager                    23    8.7    23  near,direct
android.location.Address                        23    8.6    23  near,direct
android.icu.util.TimeZone                       22    8.5    22  near,direct
android.icu.math.BigDecimal                     21    8.5    21  near,direct
```

Note: DB schema uses `android_packages.name` + `android_types.full_name` (e.g., pkg=`android.app`, type=`Activity`). The loop script joins them as `p.name || '.' || t.full_name`.

---

## Self-Correction Strategies

### Compilation errors

| Error Pattern | Fix Strategy |
|---|---|
| `cannot find symbol: class X` | Check if X exists in shim. If not, create stub. If so, add import. |
| `incompatible types` | Read AOSP source for correct return type, fix signature |
| `method does not override` | Check parent class signature, match exactly |
| `unreported exception` | Add try/catch or throws declaration |

### Test failures

| Failure Pattern | Fix Strategy |
|---|---|
| `expected X but got null` | Shim method returns null — implement the actual logic |
| `expected X but got Y` | Logic error — trace through, compare with AOSP behavior |
| `ClassCastException` | Wrong type in mock or shim — fix type hierarchy |
| `NullPointerException` | Missing initialization — add null checks or init in constructor |

### Mock limitations

| Issue | Resolution |
|---|---|
| Mock can't simulate native behavior | Mark test as `@DeviceOnly`, skip in mock mode |
| Mock returns different values than device | Document in test, assert mock-specific expected values |
| Mock doesn't persist across restarts | Acceptable — persistence tested on device only |

---

## Metrics & Dashboard

Track loop progress with:

```sql
-- Coverage summary
SELECT
    (SELECT COUNT(*) FROM shim_progress WHERE status IN ('tested_mock','tested_device')) as shimmed,
    (SELECT COUNT(DISTINCT t.full_name) FROM android_apis a
     JOIN android_types t ON a.type_id = t.id
     WHERE a.kind IN ('method','constructor')
     AND t.full_name LIKE 'android.%') as total_classes,
    (SELECT COUNT(*) FROM api_mappings m
     JOIN android_apis a ON m.android_api_id = a.id
     WHERE a.kind IN ('method','constructor')
     AND m.score >= 7) as feasible_apis,
    (SELECT COUNT(*) FROM shim_progress WHERE status = 'failed') as blocked;
```

### Expected throughput

| Phase | APIs/hour | Bottleneck |
|---|---|---|
| Tier 1 direct (score 8-10) | ~20-30 | Mostly thin wrappers, fast generation |
| Tier 2 near (score 7-8) | ~10-15 | Need adaptation logic, more test cases |
| Tier 2 composite (score 5-7) | ~5-8 | Complex mapping, may need new bridge methods |
| With device validation | ÷2 | Deploy + run cycle adds ~30s per iteration |

### Projected timeline

| Target | API Count | Estimated Loop Iterations | Time (at 15 APIs/hr) |
|---|---|---|---|
| Core 38 classes shimmed | ~500 methods | ~35 batches | ~2-3 hours |
| All Tier 1 (score 8-10) | ~4,600 APIs | ~300 batches | ~20 hours |
| All Tier 1-2 (score 5-10) | ~17,800 APIs | ~1,200 batches | ~80 hours |
| Full coverage attempted | ~34,500 APIs | ~2,300 batches | ~150+ hours |

---

## Prerequisites Checklist

### For local JVM loop (works NOW)

- [x] API mapping database (`api_compat.db`) with 57K mappings
- [x] Mock OHBridge for local testing
- [x] Compilation toolchain (`javac` from AOSP tree)
- [x] Test harness (HeadlessTest + UITestApp)
- [x] 38 shim classes as patterns to learn from
- [ ] Progress tracking DB (create `shim_progress.db`)
- [ ] Orchestration script (`a2oh-loop.sh`)

### For QEMU device loop (requires setup)

- [ ] Install `qemu-system-aarch64` (or x86_64) v5.1+
- [ ] Build OHOS: `./build.sh --product-name qemu-arm-linux-headless`
- [ ] Boot OHOS VM and verify `hdc` connectivity
- [ ] Port OpenJDK Zero VM to OHOS (or alternative JVM solution)
- [ ] Cross-compile `liboh_bridge.so` + `liboh_cpp_shim.so` for target arch
- [ ] Deploy shim + tests to device, verify one test suite passes

---

## Answer: Is It Possible?

**Yes, absolutely.** The E2E self-evolving loop is viable because:

1. **Structured data exists**: 57K API mappings with scores, types, and OH API links — the AI doesn't need to figure out *what* to map, only *how*
2. **Patterns are established**: 38 shim classes demonstrate every pattern (thin wrapper, composite adapter, pure Java, bridge call, view hierarchy)
3. **Automated validation**: `javac` + `java` + mock bridge = fully automated compile-test cycle with zero hardware
4. **Self-correction is bounded**: Java compilation errors are deterministic and parseable; test assertions have clear expected values
5. **Incremental**: Each API is independent enough to commit separately, so failures don't block the whole pipeline

### What works NOW (no blockers)

The **local JVM loop** — generates shim code, compiles, tests against mock bridge on any machine with a JDK. Covers API surface correctness for ~7,500 feasible APIs (score >= 7). Run: `./a2oh-loop.sh --dry-run` to see candidates.

### What needs work (JVM-on-OHOS blocker)

The **device validation loop** — requires a JVM running on OHOS to execute the Java shim + test harness against real OH native APIs. Since OHOS has no Java runtime, this requires porting OpenJDK Zero VM (recommended) or building a DEX→ARK translator. This is a separate workstream that can proceed in parallel with the local loop.

### Two-phase strategy

1. **Phase 1 (now)**: Run the local JVM loop to shim all Tier 1-2 APIs. Output: complete Java shim library with 100% API surface coverage, tested against mock bridge.
2. **Phase 2 (after JVM port)**: Deploy to OHOS QEMU VM, run tests against real OH APIs, fix any mock-vs-real divergences.
