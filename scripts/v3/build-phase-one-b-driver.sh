#!/usr/bin/env bash
# ============================================================================
# build-phase-one-b-driver.sh — build PhaseOneBDriver JAR for V3 chroot
#
# Compiles the V3 chroot-compatible mini-driver classes against HBC's
# framework.jar + core-* + oh-adapter-framework + oh-adapter-runtime, then
# packages into a JAR (and optionally a DEX JAR for ART zero-cost load).
#
# Output: $REPO_ROOT/engine/v3/phase-one-b/out/phase-one-b-driver.jar
#         $REPO_ROOT/engine/v3/phase-one-b/out/phase-one-b-driver-dex.jar (if d8 available)
#
# Push the .jar (or .jar with classes.dex inside) to the chroot at
#   /system/android/framework/phase-one-b-driver.jar
# then send a text-protocol spawn message with:
#   targetClass=com.westlake.engine.PhaseOneBDriver
#   dexPaths=/system/android/framework/phase-one-b-driver.jar:/data/app/<targetapp>/base.apk
#
# STATUS as of 2026-05-19: NOT YET FUNCTIONAL — substrate gap blocks the
# spawn child from binding android.util.Log.println_native, so even if
# PhaseOneBDriver loads, its first Log call would throw. See
# docs/engine/V3-W2-PHASE-1B-PROGRESS.md §4 for substrate fix path.
#
# Authored 2026-05-19 (agent 73) per docs/engine/V3-PHASE-ONE-B-DRIVER.md.
# ============================================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$REPO_ROOT/engine/v3/phase-one-b/src"
OUT_DIR="$REPO_ROOT/engine/v3/phase-one-b/out"
HBC_JARS_DIR="$REPO_ROOT/westlake-deploy-ohos/v3-hbc/jars"

# Build with system JDK (per memory: conda JDK has issues). System JDK 21 has
# javac that can target source/target 1.8 with bootclasspath override.
JAVAC="${JAVAC:-/usr/lib/jvm/java-21-openjdk-amd64/bin/javac}"
JAR="${JAR:-/usr/lib/jvm/java-21-openjdk-amd64/bin/jar}"

# Optional: d8 for DEX conversion (ART loads dex without dex2oat-during-run cost).
# Find d8 in Android SDK or AOSP build tools if present.
D8="${D8:-}"
if [ -z "$D8" ]; then
    for cand in \
        "$HOME/Android/Sdk/build-tools/"*"/d8" \
        "/usr/local/bin/d8" \
        "$(command -v d8 2>/dev/null)"; do
        if [ -x "$cand" ]; then D8="$cand"; break; fi
    done
fi

for tool in "$JAVAC" "$JAR"; do
    if [ ! -x "$tool" ]; then
        echo "ERROR: $tool not found / not executable" >&2
        exit 1
    fi
done
if [ ! -d "$SRC_DIR" ]; then
    echo "ERROR: src dir not found: $SRC_DIR" >&2
    exit 1
fi
if [ ! -d "$HBC_JARS_DIR" ]; then
    echo "ERROR: HBC jars dir not found: $HBC_JARS_DIR" >&2
    exit 1
fi

# Bootclasspath = HBC's framework chain. Order matters for resolution.
CP="$HBC_JARS_DIR/core-oj.jar:$HBC_JARS_DIR/core-libart.jar:$HBC_JARS_DIR/core-icu4j.jar:$HBC_JARS_DIR/framework.jar:$HBC_JARS_DIR/oh-adapter-framework.jar:$HBC_JARS_DIR/oh-adapter-runtime.jar:$HBC_JARS_DIR/adapter-mainline-stubs.jar"

# Verify each jar exists
for j in $(echo "$CP" | tr ':' '\n'); do
    if [ ! -f "$j" ]; then
        echo "ERROR: bootclasspath jar missing: $j" >&2
        exit 1
    fi
done

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR/classes"

echo "Compiling .java -> .class (source 1.8 target 1.8 bootcp HBC)"
SOURCES=$(find "$SRC_DIR" -name "*.java")
"$JAVAC" -source 1.8 -target 1.8 \
    -bootclasspath "$CP" \
    -d "$OUT_DIR/classes" \
    $SOURCES

echo "Packaging .jar"
"$JAR" --create --file "$OUT_DIR/phase-one-b-driver.jar" -C "$OUT_DIR/classes" .

if [ -n "$D8" ] && [ -x "$D8" ]; then
    echo "Converting to DEX via $D8"
    mkdir -p "$OUT_DIR/dex"
    "$D8" --output "$OUT_DIR/dex" \
        --lib "$HBC_JARS_DIR/core-oj.jar" \
        --lib "$HBC_JARS_DIR/framework.jar" \
        $(find "$OUT_DIR/classes" -name "*.class")
    # Package dex into jar so ART can load via PathClassLoader / -Xbootclasspath
    "$JAR" --create --file "$OUT_DIR/phase-one-b-driver-dex.jar" \
        -C "$OUT_DIR/dex" classes.dex
    echo "DEX jar: $OUT_DIR/phase-one-b-driver-dex.jar"
fi

echo ""
echo "BUILD OK"
echo "  Plain JAR: $OUT_DIR/phase-one-b-driver.jar ($(stat -c %s "$OUT_DIR/phase-one-b-driver.jar") bytes)"
if [ -f "$OUT_DIR/phase-one-b-driver-dex.jar" ]; then
    echo "  DEX JAR:   $OUT_DIR/phase-one-b-driver-dex.jar ($(stat -c %s "$OUT_DIR/phase-one-b-driver-dex.jar") bytes)"
fi
echo ""
echo "Next: push into chroot's framework dir (operator)"
echo "  bash scripts/v3/launch-mcd-chroot.sh push-driver"
