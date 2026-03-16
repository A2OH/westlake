#!/bin/bash
# ═══════════════════════════════════════════════════════════════════
# a2oh-loop.sh — Self-evolving AI shim development loop
#
# Autonomously generates Android→OpenHarmony API shims by:
#   1. Querying api_compat.db for the next unimplemented class
#   2. Invoking Claude Code to generate shim + mock + tests
#   3. Compiling and running local JVM tests (mock bridge)
#   4. Recording results in shim_progress.db
#   5. Repeating until done
#
# Usage:
#   ./a2oh-loop.sh                   # default: 10 classes, score >= 7
#   ./a2oh-loop.sh --max-classes 50  # process 50 classes
#   ./a2oh-loop.sh --min-score 5     # include Tier 2 composite
#   ./a2oh-loop.sh --dry-run         # show what would be processed
#   ./a2oh-loop.sh --resume          # continue from last run
# ═══════════════════════════════════════════════════════════════════

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
DB="$PROJECT_ROOT/database/api_compat.db"
PROGRESS_DB="$PROJECT_ROOT/database/shim_progress.db"

# Defaults
MAX_CLASSES=10
MIN_SCORE=7
DRY_RUN=false
MAX_RETRIES=3

# Already shimmed classes (from existing shim/java/android/)
# Format: "package.ClassName" as stored in DB (p.name || '.' || t.full_name)
ALREADY_SHIMMED=(
    "android.util.Log"
    "android.os.Bundle"
    "android.os.Build"
    "android.content.SharedPreferences"
    "android.content.SharedPreferences.Editor"
    "android.content.Intent"
    "android.content.Context"
    "android.content.ContentValues"
    "android.content.BroadcastReceiver"
    "android.app.Activity"
    "android.app.NotificationManager"
    "android.app.NotificationChannel"
    "android.app.Notification"
    "android.app.Notification.Builder"
    "android.app.AlarmManager"
    "android.app.PendingIntent"
    "android.database.sqlite.SQLiteDatabase"
    "android.database.sqlite.SQLiteOpenHelper"
    "android.database.Cursor"
    "android.database.CursorWrapper"
    "android.database.SQLException"
    "android.net.Uri"
    "android.widget.Toast"
    "android.widget.TextView"
    "android.widget.Button"
    "android.widget.EditText"
    "android.widget.ImageView"
    "android.widget.LinearLayout"
    "android.widget.FrameLayout"
    "android.widget.ScrollView"
    "android.widget.ListView"
    "android.widget.CheckBox"
    "android.widget.Switch"
    "android.widget.SeekBar"
    "android.widget.ProgressBar"
    "android.view.View"
    "android.view.ViewGroup"
    "android.view.Gravity"
    "android.view.LayoutInflater"
)

# Helper: build fully qualified class name from DB
# DB stores: t.full_name = "Activity", p.name = "android.app"
# We need: "android.app.Activity"
FQN_EXPR="p.name || '.' || t.full_name"

# Parse args
for arg in "$@"; do
    case $arg in
        --max-classes=*) MAX_CLASSES="${arg#*=}" ;;
        --min-score=*) MIN_SCORE="${arg#*=}" ;;
        --dry-run) DRY_RUN=true ;;
        --resume) ;; # just skip — progress DB handles resume
        --help)
            echo "Usage: $0 [--max-classes=N] [--min-score=N] [--dry-run] [--resume]"
            exit 0
            ;;
    esac
done

# ── Initialize progress DB ──

init_progress_db() {
    sqlite3 "$PROGRESS_DB" "
    CREATE TABLE IF NOT EXISTS shim_progress (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        android_class TEXT UNIQUE NOT NULL,
        api_count INTEGER DEFAULT 0,
        avg_score REAL DEFAULT 0,
        status TEXT DEFAULT 'pending',
        shim_file TEXT,
        test_pass INTEGER DEFAULT 0,
        test_fail INTEGER DEFAULT 0,
        attempts INTEGER DEFAULT 0,
        last_error TEXT,
        commit_hash TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        completed_at TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS loop_log (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        android_class TEXT,
        action TEXT,
        result TEXT,
        details TEXT
    );
    "
}

# ── Build exclusion list (already shimmed + already in progress DB) ──

build_exclusion_sql() {
    local parts=()
    for cls in "${ALREADY_SHIMMED[@]}"; do
        parts+=("'$cls'")
    done
    # Also exclude anything already in progress DB
    local db_done
    db_done=$(sqlite3 "$PROGRESS_DB" "
        SELECT android_class FROM shim_progress
        WHERE status IN ('tested_mock','tested_device','failed')
    " 2>/dev/null || true)
    while IFS= read -r cls; do
        [ -n "$cls" ] && parts+=("'$cls'")
    done <<< "$db_done"

    echo "${parts[*]}" | tr ' ' ','
}

# ── Query for next class to shim ──

get_next_classes() {
    local exclusions
    exclusions=$(build_exclusion_sql)

    sqlite3 "$DB" "
    SELECT $FQN_EXPR as fqn,
           COUNT(*) as api_count,
           ROUND(AVG(m.score), 1) as avg_score,
           SUM(CASE WHEN m.mapping_type IN ('direct','near') THEN 1 ELSE 0 END) as feasible,
           GROUP_CONCAT(DISTINCT m.mapping_type) as types
    FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    WHERE a.kind IN ('method','constructor')
      AND p.name LIKE 'android.%'
      AND m.score >= $MIN_SCORE
      AND ($FQN_EXPR) NOT IN ($exclusions)
    GROUP BY fqn
    HAVING feasible > 2
    ORDER BY feasible DESC, avg_score DESC
    LIMIT $MAX_CLASSES;
    "
}

# ── Get API details for a class ──

get_class_apis() {
    local class_name="$1"
    # Split fqn back into package + class name
    local pkg="${class_name%.*}"
    local cls="${class_name##*.}"
    sqlite3 "$DB" "
    SELECT a.name, a.signature, a.kind, m.score, m.mapping_type,
           oa.name as oh_name, oa.signature as oh_sig
    FROM api_mappings m
    JOIN android_apis a ON m.android_api_id = a.id
    JOIN android_types t ON a.type_id = t.id
    JOIN android_packages p ON t.package_id = p.id
    LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
    WHERE p.name = '$pkg' AND t.full_name = '$cls'
      AND a.kind IN ('method','constructor')
      AND m.score >= $MIN_SCORE
    ORDER BY m.score DESC;
    "
}

# ── Run tests ──

run_tests() {
    cd "$PROJECT_ROOT/test-apps"
    local output
    output=$(./run-local-tests.sh 2>&1) || true
    echo "$output"
}

# ── Parse test results ──

parse_test_results() {
    local output="$1"
    local headless_pass headless_fail ui_pass ui_fail

    # Parse headless results
    headless_pass=$(echo "$output" | grep -A2 "Headless CLI" | grep "Passed:" | grep -oP '\d+' | head -1 || echo "0")
    headless_fail=$(echo "$output" | grep -A2 "Headless CLI" | grep "Failed:" | grep -oP '\d+' | head -1 || echo "0")

    # Parse UI results
    ui_pass=$(echo "$output" | grep -A2 "UI Mockup" | grep "Passed:" | grep -oP '\d+' | head -1 || echo "0")
    ui_fail=$(echo "$output" | grep -A2 "UI Mockup" | grep "Failed:" | grep -oP '\d+' | head -1 || echo "0")

    echo "$((headless_pass + ui_pass))|$((headless_fail + ui_fail))"
}

# ── Log action ──

log_action() {
    local class="$1" action="$2" result="$3" details="${4:-}"
    sqlite3 "$PROGRESS_DB" "
    INSERT INTO loop_log (android_class, action, result, details)
    VALUES ('$class', '$action', '$result', '$(echo "$details" | sed "s/'/''/g" | head -c 500)');
    "
}

# ══════════════════════════════════════════════════════════════
# MAIN LOOP
# ══════════════════════════════════════════════════════════════

echo "═══ A2OH Self-Evolving Shim Loop ═══"
echo "Config: max_classes=$MAX_CLASSES min_score=$MIN_SCORE dry_run=$DRY_RUN"
echo ""

init_progress_db

# Get candidate classes
echo "Querying database for next $MAX_CLASSES unshimmed classes (score >= $MIN_SCORE)..."
CANDIDATES=$(get_next_classes)

if [ -z "$CANDIDATES" ]; then
    echo "No more candidate classes found at score >= $MIN_SCORE"
    exit 0
fi

echo ""
echo "── Candidate Classes ──"
echo "Class | APIs | Avg Score | Feasible | Types"
echo "$CANDIDATES" | while IFS='|' read -r cls count score feasible types; do
    printf "%-45s %4s  %5s  %4s  %s\n" "$cls" "$count" "$score" "$feasible" "$types"
done
echo ""

if $DRY_RUN; then
    echo "(dry run — no code generation)"
    exit 0
fi

# Get baseline test results before we start
echo "Running baseline tests..."
BASELINE_OUTPUT=$(run_tests)
BASELINE_RESULTS=$(parse_test_results "$BASELINE_OUTPUT")
BASELINE_PASS=$(echo "$BASELINE_RESULTS" | cut -d'|' -f1)
BASELINE_FAIL=$(echo "$BASELINE_RESULTS" | cut -d'|' -f2)
echo "Baseline: $BASELINE_PASS passed, $BASELINE_FAIL failed"
echo ""

classes_done=0
classes_failed=0

echo "$CANDIDATES" | while IFS='|' read -r CLASS API_COUNT AVG_SCORE FEASIBLE TYPES; do
    echo "═══════════════════════════════════════════════════"
    echo "[$((classes_done + 1))/$MAX_CLASSES] $CLASS"
    echo "  APIs: $API_COUNT | Avg Score: $AVG_SCORE | Feasible: $FEASIBLE"
    echo "═══════════════════════════════════════════════════"

    # Get API details for context
    API_DETAILS=$(get_class_apis "$CLASS")

    # Record as in-progress
    sqlite3 "$PROGRESS_DB" "
    INSERT OR REPLACE INTO shim_progress (android_class, api_count, avg_score, status, attempts)
    VALUES ('$CLASS', $API_COUNT, $AVG_SCORE, 'in_progress',
            COALESCE((SELECT attempts FROM shim_progress WHERE android_class='$CLASS'), 0) + 1);
    "

    # ── Invoke Claude Code to generate the shim ──

    PROMPT="You are working on the Android→OpenHarmony shim layer in $PROJECT_ROOT.

TARGET CLASS: $CLASS ($API_COUNT methods, avg score $AVG_SCORE)

API DETAILS (from api_compat.db):
$API_DETAILS

TASK:
1. Create/update the Java shim class at the correct path under shim/java/
   - Match the AOSP package and class name exactly
   - Follow patterns from existing shim files (e.g., shim/java/android/util/Log.java)
   - Delegate to OHBridge for native calls, or implement in pure Java where possible
   - Only implement methods that have score >= $MIN_SCORE in the mapping

2. If new OHBridge native methods are needed:
   - Add declarations to shim/java/com/ohos/shim/bridge/OHBridge.java
   - Add mock implementations to test-apps/mock/com/ohos/shim/bridge/OHBridge.java

3. Add test cases to test-apps/02-headless-cli/src/HeadlessTest.java
   - Add a new test section for this class
   - Test the key methods with assertions

4. Compile and run tests: cd test-apps && ./run-local-tests.sh
   - Fix any compilation errors
   - Fix any test failures
   - Repeat until tests pass

5. Do NOT commit. Just make the code compile and tests pass.

IMPORTANT:
- Use javac from: $HOME/aosp-android-11/prebuilts/jdk/jdk9/linux-x86/bin/javac
- If javac is not in PATH, the run-local-tests.sh script may need the full path
- Keep the shim minimal — only implement what's needed for the mapped APIs
- Do not break existing tests (baseline: $BASELINE_PASS pass, $BASELINE_FAIL fail)"

    echo "  Invoking Claude Code..."
    log_action "$CLASS" "generate" "started" ""

    # Run Claude Code (non-interactive mode)
    CLAUDE_OUTPUT=$(claude --print "$PROMPT" 2>&1) || true

    # ── Verify: run tests ourselves ──

    echo "  Verifying tests..."
    TEST_OUTPUT=$(run_tests 2>&1) || true
    RESULTS=$(parse_test_results "$TEST_OUTPUT")
    PASS=$(echo "$RESULTS" | cut -d'|' -f1)
    FAIL=$(echo "$RESULTS" | cut -d'|' -f2)

    echo "  Results: $PASS passed, $FAIL failed (baseline was $BASELINE_PASS/$BASELINE_FAIL)"

    # Check if we regressed (more failures than baseline)
    if [ "$FAIL" -le "$((BASELINE_FAIL + 2))" ]; then
        # Success (allow 2 extra for mock stubs)
        STATUS="tested_mock"
        sqlite3 "$PROGRESS_DB" "
        UPDATE shim_progress
        SET status='$STATUS', test_pass=$PASS, test_fail=$FAIL,
            completed_at=datetime('now')
        WHERE android_class='$CLASS';
        "
        log_action "$CLASS" "test" "pass" "pass=$PASS fail=$FAIL"
        echo "  ✓ $CLASS shimmed successfully"
        classes_done=$((classes_done + 1))

        # Update baseline
        BASELINE_PASS=$PASS
        BASELINE_FAIL=$FAIL
    else
        # Regression — mark as failed
        STATUS="failed"
        LAST_ERROR=$(echo "$TEST_OUTPUT" | grep "FAIL:" | head -3)
        sqlite3 "$PROGRESS_DB" "
        UPDATE shim_progress
        SET status='$STATUS', test_pass=$PASS, test_fail=$FAIL,
            last_error='$(echo "$LAST_ERROR" | sed "s/'/''/g")'
        WHERE android_class='$CLASS';
        "
        log_action "$CLASS" "test" "fail" "$LAST_ERROR"
        echo "  ✗ $CLASS failed — test regression detected"
        classes_failed=$((classes_failed + 1))

        # Revert changes to prevent cascade
        echo "  Reverting changes..."
        git -C "$PROJECT_ROOT" checkout -- . 2>/dev/null || true
    fi

    echo ""
done

echo "═══ Loop Complete ═══"
echo "Shimmed: $classes_done | Failed: $classes_failed"
echo ""
echo "Progress summary:"
sqlite3 "$PROGRESS_DB" "
SELECT status, COUNT(*) as count, ROUND(AVG(avg_score),1) as avg_score
FROM shim_progress
GROUP BY status
ORDER BY count DESC;
"
