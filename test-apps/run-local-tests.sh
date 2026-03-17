#!/bin/bash
# Run all tests locally using the mock OHBridge (no device needed).
#
# Usage:
#   ./run-local-tests.sh              # run all tests (headless + UI + MockDonalds)
#   ./run-local-tests.sh headless     # headless only
#   ./run-local-tests.sh ui           # UI mockup only
#   ./run-local-tests.sh mockdonalds  # MockDonalds end-to-end only

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

SHIM_JAVA="$PROJECT_ROOT/shim/java"
MOCK_JAVA="$SCRIPT_DIR/mock"
BUILD_DIR="$SCRIPT_DIR/build-local"

echo "═══ Building with mock OHBridge ═══"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

# Collect source: mock bridge first (overrides real OHBridge), then shim classes, then tests
JAVA_FILES=$(find "$MOCK_JAVA" -name "*.java")
JAVA_FILES="$JAVA_FILES $(find "$SHIM_JAVA" -name "*.java" ! -path "*/ohos/shim/bridge/OHBridge.java")"
JAVA_FILES="$JAVA_FILES $SCRIPT_DIR/02-headless-cli/src/HeadlessTest.java"
JAVA_FILES="$JAVA_FILES $SCRIPT_DIR/03-ui-mockup/src/UITestApp.java"
JAVA_FILES="$JAVA_FILES $(find "$SCRIPT_DIR/04-mockdonalds/src" -name "*.java")"

echo "Compiling $(echo "$JAVA_FILES" | wc -w) Java files..."
javac -d "$BUILD_DIR" \
    -sourcepath "$MOCK_JAVA:$SHIM_JAVA:$SCRIPT_DIR/02-headless-cli/src:$SCRIPT_DIR/03-ui-mockup/src:$SCRIPT_DIR/04-mockdonalds/src" \
    $JAVA_FILES 2>&1

echo "Compiled successfully."
echo ""

run_headless() {
    echo "═══ Running: Headless CLI Test (mock) ═══"
    echo ""
    java -cp "$BUILD_DIR" HeadlessTest 2>&1 || true
    echo ""
}

run_ui() {
    echo "═══ Running: UI Mockup Test (headless mode) ═══"
    echo ""
    java -cp "$BUILD_DIR" UITestApp --headless 2>&1 || true
    echo ""
}

run_mockdonalds() {
    echo "═══ Running: MockDonalds End-to-End Test ═══"
    echo ""
    java -cp "$BUILD_DIR" MockDonaldsRunner 2>&1 || true
    echo ""
}

case "${1:-all}" in
    headless)     run_headless ;;
    ui)           run_ui ;;
    mockdonalds)  run_mockdonalds ;;
    all)          run_headless; run_ui; run_mockdonalds ;;
    *)            echo "Usage: $0 {headless|ui|mockdonalds|all}"; exit 1 ;;
esac
