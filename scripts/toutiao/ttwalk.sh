#!/bin/bash
# ttwalk.sh — Toutiao walkthrough harness, modelled on walkcat1.sh + wl_ui.sh/wl_click_all.sh.
#
# Gates on `side-channels started` (NEVER a timer), writes walkpid so every liveness check
# names the RIGHT pid, then drives the app through the bridge side-channel and records BOTH
# oracles at each step:
#   pixels  -> snapshot_display + shotlit.py  (lum / lit% / colours / verdict)
#   widgets -> `echo v > noice_tap.<child-pid>` view-tree dump (rect/id/text/clickable), which is
#              INDEPENDENT of pixels -- if VT lists laid-out widgets the UI exists even
#              when the framebuffer is black.
#
# usage: ttwalk.sh [launch|status|vt [root-index]|root <index>|tap <x> <y>|click <x> <y>|taps <id-or-text>|swipe <x1> <y1> <x2> <y2>|back|text <value>|shot <name>|walk]
set -uo pipefail
H=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
EVIDENCE_ROOT=${WL_EVIDENCE_ROOT:-/home/dspfac/openharmony/toutiao-visual-loop-20260826}
RUN_ID=${WL_RUN_ID:-manual}
RUN_DIR=$EVIDENCE_ROOT/$RUN_ID
SHOTS=$RUN_DIR/shots
mkdir -p "$SHOTS"
ASX=/data/service/el1/public/appspawnx
# The async UI-thread input route now preserves Android's normal RecyclerView
# gesture/click contract. Forced performClick ignores RecyclerView transforms
# and can select an offscreen child after an otherwise valid tap.
FORCE_CLICK=${WL_TOUCH_FORCE_CLICK:-0}
TOUCH_DIRECT=${WL_TOUCH_DIRECT:-0}
TOUCH_ENQUEUE=${WL_TOUCH_ENQUEUE:-1}
TLS_HTTP_HEADS=${WL_TLS_DUMP_HTTP_HEADS:-0}
TLS_SEARCH_BODY=${WL_TLS_DUMP_SEARCH_BODY:-0}
WEB_DIAG_ASSETS=${WL_WEB_DIAG_ASSETS:-0}
WEB_NATIVE_FALLBACK_DIAG=${WL_WEB_NATIVE_FALLBACK_DIAG:-0}
FRESCO_TRACE=${WL_FRESCO_TRACE:-0}
FORCE_JIT=${APPSPAWNX_FORCE_JIT:-1}
# A prior process can flush its cached teen-mode preference while it exits and
# undo an earlier file edit. The full visual walkthrough needs normal mode, so
# apply this control after appspawn-x has stopped and preserve a restore copy.
NORMAL_MODE=${WL_TOUTIAO_NORMAL_MODE:-1}
TTVIDEO_CALLS=${WL_TRACE_TOUTIAO_VIDEO:-0}
NATIVE_LOAD_TRACE=${WL_TRACE_NATIVE_LOAD:-0}
NATIVE_NET_TRACE=${WL_TRACE_ANDROID_NATIVE_NET:-0}
NATIVE_WINDOW_TRACE=${WL_TRACE_ANDROID_NATIVE_WINDOW:-0}
EXIT_TRACE=${WL_EXIT_TRACE:-0}
PTRACE_TRACE=${WL_PTRACE_TRACE:-0}
POST_CLICK=${WL_TOUCH_POST_CLICK:-0}
TOUCH_FD_MAIN=${WL_TOUCH_FD_MAIN:-0}
KEY_ENQUEUE=${WL_KEY_ENQUEUE:-0}
# The standalone AttachToDisplay path is the only path that has produced
# visually verified Toutiao feed and decoded-video pixels on this board. A
# parented OH sub-window remains available as a diagnostic mode.
OH_HOST=${WL_OH_HOST:-0}
TOUCH_ENQUEUE_ENV=
if [ "$TOUCH_ENQUEUE" != "0" ]; then
  TOUCH_ENQUEUE_ENV=WL_TOUCH_ENQUEUE=1
fi
POST_CLICK_ENV=
if [ "$POST_CLICK" != "0" ]; then
  POST_CLICK_ENV=WL_TOUCH_POST_CLICK=1
fi
TOUCH_FD_MAIN_ENV=
if [ "$TOUCH_FD_MAIN" != "0" ]; then
  TOUCH_FD_MAIN_ENV=WL_TOUCH_FD_MAIN=1
fi
TOUCH_DIRECT_ENV=
if [ "$TOUCH_DIRECT" != "0" ]; then
  TOUCH_DIRECT_ENV=WL_TOUCH_DIRECT=1
fi
KEY_ENQUEUE_ENV=
if [ "$KEY_ENQUEUE" != "0" ]; then
  KEY_ENQUEUE_ENV=WL_KEY_ENQUEUE=1
fi

dev() { $H shell "$1" 2>/dev/null | tr -d '\r'; }
childlog() { dev 'ls -t '"$ASX"'/adapter_child_*.stderr 2>/dev/null | head -1'; }
walkpid()  { dev 'cat /data/local/tmp/asx/walkpid 2>/dev/null'; }
tapch()    { echo "/data/local/tmp/noice_tap.$(walkpid)"; }

awake() {
  # Waking the panel must be observational. The old helper also injected an
  # upward swipe, so every screenshot silently mutated the RecyclerView and
  # could trigger a scroll-path crash before the capture was taken.
  dev "power-shell timeout -o 3600000 >/dev/null 2>&1; power-shell wakeup >/dev/null 2>&1; sleep 1" >/dev/null
}

shot() {  # shot <name> -> capture + quantify
  local n=$1
  dev "power-shell wakeup >/dev/null 2>&1; snapshot_display -f /data/local/tmp/s.jpeg >/dev/null 2>&1" >/dev/null
  ( cd "$SHOTS" && $H file recv /data/local/tmp/s.jpeg ./"$n".jpeg >/dev/null 2>&1 )
  python3 "$SCRIPT_DIR/shotlit.py" "$SHOTS/$n.jpeg" 2>/dev/null | tail -1
}

vt() {  # vt [root-index] — dump all roots or one exact root
  # NOTE: the tag is "[N/OH_InputBridge] VT" where N varies per run, so match with a
  # wildcard, not a literal 'OH_InputBridge:'. Getting this wrong makes a WORKING
  # side-channel look dead (it reported 0 widgets while the dialog was right there).
  local C N CMD; C=$(childlog); N=$(dev "wc -l < $C" | tr -d ' ')
  CMD=v
  [ -n "${1:-}" ] && CMD="v $1"
  dev "echo '$CMD' > $(tapch)"; sleep 8
  dev "tail -n +$N $C | grep -a 'OH_InputBridge. VT'" | sed -E 's/.*VT +//'
}

root() {  # root <zero-based-index> — aim later input at one ViewRootImpl
  dev "echo 'r$1' > $(tapch)" >/dev/null
}

taps() {  # taps <id-or-text-substring> — tap the centre of the first matching widget
  local pat=$1 line rect x y w h
  line=$(vt | grep -a "$pat" | head -1)
  [ -z "$line" ] && { echo "no widget matching '$pat'"; return 1; }
  rect=$(echo "$line" | grep -oE 'rect=\[[0-9]+,[0-9]+ [0-9]+x[0-9]+\]')
  set -- $(echo "$rect" | sed -E 's/rect=\[([0-9]+),([0-9]+) ([0-9]+)x([0-9]+)\]/\1 \2 \3 \4/')
  x=$(( $1 + $3 / 2 )); y=$(( $2 + $4 / 2 ))
  echo "tap '$pat' -> ($x,$y)  [$line]"
  tap "$x" "$y"
}

tap() { dev "echo '$1 $2' > $(tapch)" >/dev/null; }

# Explicit hit-tested click on the native main MessageQueue. Use only after a
# normal tap is acknowledged but a delayed RecyclerView CheckForTap remains
# stranded behind the standalone runtime's Choreographer sync barrier.
click() { dev "echo 'c $1 $2' > $(tapch)" >/dev/null; }

swipe() { dev "echo '$1 $2 $3 $4' > $(tapch)" >/dev/null; }

back() { dev "echo back > $(tapch)" >/dev/null; }

text_entry() { dev "printf '%s' '$1' > /data/local/tmp/noice_text" >/dev/null; }

launch() {
  echo "=== launching Toutiao (gate: side-channels started) ==="
  local NORMAL_MODE_CMD= HOST_PARENT_ID=${WL_PARENT_ID:-} WINDOW_ENV=
  local SESSION_BUNDLE=${WL_SESSION_BUNDLE:-com.westlake.surface}
  if [ "$NORMAL_MODE" != "0" ]; then
    NORMAL_MODE_CMD="P=/data/data/com.ss.android.article.news/shared_prefs/teen_mode_data.xml; if [ -f \"\$P\" ]; then [ -f \"\$P.pre-ttwalk-normal\" ] || cp \"\$P\" \"\$P.pre-ttwalk-normal\"; sed -i 's/name=\"open\" value=\"true\"/name=\"open\" value=\"false\"/; s/name=\"age\" value=\"0\"/name=\"age\" value=\"-1\"/' \"\$P\"; fi;"
  fi
  dev "kill -9 \$(pidof appspawn-x) 2>/dev/null; sleep 3" >/dev/null
  if [ "$OH_HOST" != "0" ]; then
    # Diagnostic path: establish an OH ability window and ask SceneBoard to
    # adopt Android windows beneath it.
    dev "aa start -a EntryAbility -b com.github.ashutoshgngwr.noice >/dev/null 2>&1; sleep 2" >/dev/null
    if [ -z "$HOST_PARENT_ID" ]; then
      HOST_PARENT_ID=$(dev "hidumper -s WindowManagerService -a '-a' 2>/dev/null | sed -n 's/^noice0[[:space:]]*[^ ]*[[:space:]]*[^ ]*[[:space:]]*\\([0-9][0-9]*\\).*/\\1/p' | head -1" | tr -d '\r')
    fi
    if ! [[ "$HOST_PARENT_ID" =~ ^[1-9][0-9]*$ ]]; then
      echo "launch failed: noice0 host persistent ID was not found" >&2
      return 1
    fi
    WINDOW_ENV="WL_SUB_WINDOW=1 WL_PARENT_ID=$HOST_PARENT_ID WL_SESSION_BUNDLE=$SESSION_BUNDLE WL_WINDOW_ISWINDOW=1"
    echo "window mode=OH-host parent id=$HOST_PARENT_ID session bundle=$SESSION_BUNDLE"
  else
    # Even a transparent OH ability makes SceneBoard background/blur the
    # launcher above a sessionless RS node. Stop it before Android creates its
    # direct-display surfaces so the pixel oracle observes Android itself.
    dev "aa force-stop com.github.ashutoshgngwr.noice >/dev/null 2>&1; P=\$(pidof com.github.ashutoshgngwr.noice); [ -n \"\$P\" ] && kill -9 \$P; sleep 2" >/dev/null
    echo "window mode=standalone direct-display (OH host stopped)"
  fi
  dev "$NORMAL_MODE_CMD cd /data/local/tmp/asx; rm -f asx.err walkpid /data/local/tmp/asx/THROWTRACE /data/local/tmp/noice_tap.* $ASX/hook.log; mkdir -p $ASX/prev; for f in $ASX/adapter_child_*.stderr; do [ -e \"\$f\" ] && mv \"\$f\" $ASX/prev/; done; setenforce 0 2>/dev/null; env APPSPAWNX_FORCE_JIT=$FORCE_JIT $WINDOW_ENV WL_EXIT_TRACE=$EXIT_TRACE WL_PTRACE_TRACE=$PTRACE_TRACE WL_FRESCO_TRACE=$FRESCO_TRACE $TOUCH_DIRECT_ENV WL_TOUCH_FORCE_CLICK=$FORCE_CLICK $TOUCH_ENQUEUE_ENV $POST_CLICK_ENV $TOUCH_FD_MAIN_ENV $KEY_ENQUEUE_ENV WL_TLS_DUMP_HTTP_HEADS=$TLS_HTTP_HEADS WL_TLS_DUMP_SEARCH_BODY=$TLS_SEARCH_BODY WL_WEB_DIAG_ASSETS=$WEB_DIAG_ASSETS WL_WEB_NATIVE_FALLBACK_DIAG=$WEB_NATIVE_FALLBACK_DIAG WESTLAKE_TRACE_TOUTIAO_VIDEO=$TTVIDEO_CALLS WESTLAKE_TRACE_NATIVE_LOAD=$NATIVE_LOAD_TRACE WESTLAKE_TRACE_ANDROID_NATIVE_NET=$NATIVE_NET_TRACE WL_TRACE_ANDROID_NATIVE_WINDOW=$NATIVE_WINDOW_TRACE WESTLAKE_TRACE_ANDROID_NATIVE_WINDOW=$NATIVE_WINDOW_TRACE ASX_KEEP_THEME=1 ASX_DIRECT_LAUNCH=1 ASX_LAUNCH_PKG=com.ss.android.article.news ASX_APK_PATH=/data/local/tmp/asx/toutiao.apk ASX_LAUNCH_ACTIVITY=com.ss.android.article.news.activity.MainActivity ASX_WEBVIEW_APK=/data/local/tmp/asx/webview-t.apk ASX_WEBVIEW_LIB_DIR=/data/local/tmp/asx/webview-t-lib ASX_WEBVIEW_DATA_DIR=/data/local/tmp/asx/webview-t-data nohup sh /data/local/tmp/asx/run_tt.sh >/dev/null 2>&1 & i=0; while [ \$i -lt 900 ]; do grep -q 'Ready to accept' asx.err 2>/dev/null && break; sleep 0.1; i=\$((i+1)); done; ./spawn_client /dev/unix/socket/AppSpawnX com.ss.android.article.news >/dev/null 2>&1; echo spawned" >/dev/null
  local C P
  for i in $(seq 1 90); do
    sleep 4; C=$(childlog); [ -z "$C" ] && continue
    if dev "grep -aqc 'side-channels started' $C && echo yes" | grep -q yes; then
      P=$(echo "$C" | sed 's|.*adapter_child_||; s|\.stderr||')
      dev "echo $P > /data/local/tmp/asx/walkpid" >/dev/null
      echo "LAUNCHED child=$P after ~$((i*4))s  (walkpid written)"
      return 0
    fi
  done
  echo "LAUNCH_FAIL: side-channels never started"; return 1
}

status() {
  local P C; P=$(walkpid); C=$(childlog)
  echo "walkpid=$P alive=$(dev "[ -d /proc/$P ] && echo YES || echo NO")  log=$(basename "$C")"
  dev "echo \"  sidechan=\$(grep -ac 'side-channels started' $C) VRI=\$(grep -ac ViewRootImpl $C) segv_events=\$(grep -ac 'CHILDSEGV] #' $C) fatal=\$(grep -ac 'FATAL EXCEPTION' $C) ifoob=\$(grep -ac WESTLAKE-IFOOB $C)\""
  echo "  RS nodes visible: $(dev "hidumper -s RenderService -a RSTree 2>/dev/null | grep -ac 'ss.android.article.news.*Visible: 1'")"
}

case "${1:-walk}" in
  launch) launch ;;
  status) status ;;
  vt)     vt "${2:-}" ;;
  root)   root "$2"; echo "input target root=$2" ;;
  tap)    tap "$2" "$3"; echo "tapped $2 $3" ;;
  click)  click "$2" "$3"; echo "main-queue clicked $2 $3" ;;
  taps)   taps "$2" ;;
  swipe)  swipe "$2" "$3" "$4" "$5"; echo "swiped $2 $3 -> $4 $5" ;;
  back)   back; echo "back sent" ;;
  text)   text_entry "$2"; echo "text sent" ;;
  shot)   awake; shot "${2:-shot}" ;;
  walk)
    launch || exit 1
    awake
    status
    echo; echo "=== PIXEL ORACLE ==="; shot "tt_00_launch"
    echo; echo "=== WIDGET ORACLE (view tree, pixel-independent) ==="
    V=$(vt); n=$(echo "$V" | grep -c 'rect=' || true)
    echo "view-tree widget lines: $n"
    echo "$V" | head -30
    ;;
esac
