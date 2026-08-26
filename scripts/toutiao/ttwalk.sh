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
# usage: ttwalk.sh [launch|vt|tap <x> <y>|shot <name>|walk]
set -uo pipefail
H=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
SP=/tmp/claude-1000/-home-dspfac-openharmony/9b468523-43c8-4767-a2c0-caaf78c59c9d/scratchpad
SHOTS=$SP/shots; mkdir -p "$SHOTS"
ASX=/data/service/el1/public/appspawnx
# The async UI-thread input route now preserves Android's normal RecyclerView
# gesture/click contract. Forced performClick ignores RecyclerView transforms
# and can select an offscreen child after an otherwise valid tap.
FORCE_CLICK=${WL_TOUCH_FORCE_CLICK:-0}
TOUCH_DIRECT=${WL_TOUCH_DIRECT:-0}
TOUCH_ENQUEUE=${WL_TOUCH_ENQUEUE:-1}
TLS_HTTP_HEADS=${WL_TLS_DUMP_HTTP_HEADS:-0}
FORCE_JIT=${APPSPAWNX_FORCE_JIT:-1}
TTVIDEO_CALLS=${WL_TRACE_TOUTIAO_VIDEO:-0}
NATIVE_LOAD_TRACE=${WL_TRACE_NATIVE_LOAD:-0}
NATIVE_NET_TRACE=${WL_TRACE_ANDROID_NATIVE_NET:-0}
NATIVE_WINDOW_TRACE=${WL_TRACE_ANDROID_NATIVE_WINDOW:-0}
POST_CLICK=${WL_TOUCH_POST_CLICK:-0}
TOUCH_FD_MAIN=${WL_TOUCH_FD_MAIN:-0}
KEY_ENQUEUE=${WL_KEY_ENQUEUE:-0}
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

awake() { dev "power-shell timeout -o 3600000 >/dev/null 2>&1; power-shell wakeup >/dev/null 2>&1; sleep 1; uinput -T -m 600 1500 600 400 300 >/dev/null 2>&1" >/dev/null; }

shot() {  # shot <name> -> capture + quantify
  local n=$1
  dev "power-shell wakeup >/dev/null 2>&1; snapshot_display -f /data/local/tmp/s.jpeg >/dev/null 2>&1" >/dev/null
  ( cd "$SHOTS" && $H file recv /data/local/tmp/s.jpeg ./"$n".jpeg >/dev/null 2>&1 )
  python3 "$SP/shotlit.py" "$SHOTS/$n.jpeg" 2>/dev/null | tail -1
}

vt() {  # dump the view tree; prints the widget lines emitted by this dump only
  # NOTE: the tag is "[N/OH_InputBridge] VT" where N varies per run, so match with a
  # wildcard, not a literal 'OH_InputBridge:'. Getting this wrong makes a WORKING
  # side-channel look dead (it reported 0 widgets while the dialog was right there).
  local C N; C=$(childlog); N=$(dev "wc -l < $C" | tr -d ' ')
  dev "echo 'v' > $(tapch)"; sleep 8
  dev "tail -n +$N $C | grep -a 'OH_InputBridge. VT'" | sed -E 's/.*VT +//'
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

launch() {
  echo "=== launching Toutiao (gate: side-channels started) ==="
  dev "kill -9 \$(pidof appspawn-x) 2>/dev/null; sleep 3; cd /data/local/tmp/asx; rm -f asx.err walkpid /data/local/tmp/asx/THROWTRACE /data/local/tmp/noice_tap.*; mkdir -p $ASX/prev; for f in $ASX/adapter_child_*.stderr; do [ -e \"\$f\" ] && mv \"\$f\" $ASX/prev/; done; setenforce 0 2>/dev/null; env APPSPAWNX_FORCE_JIT=$FORCE_JIT $TOUCH_DIRECT_ENV WL_TOUCH_FORCE_CLICK=$FORCE_CLICK $TOUCH_ENQUEUE_ENV $POST_CLICK_ENV $TOUCH_FD_MAIN_ENV $KEY_ENQUEUE_ENV WL_TLS_DUMP_HTTP_HEADS=$TLS_HTTP_HEADS WESTLAKE_TRACE_TOUTIAO_VIDEO=$TTVIDEO_CALLS WESTLAKE_TRACE_NATIVE_LOAD=$NATIVE_LOAD_TRACE WESTLAKE_TRACE_ANDROID_NATIVE_NET=$NATIVE_NET_TRACE WL_TRACE_ANDROID_NATIVE_WINDOW=$NATIVE_WINDOW_TRACE WESTLAKE_TRACE_ANDROID_NATIVE_WINDOW=$NATIVE_WINDOW_TRACE ASX_KEEP_THEME=1 ASX_DIRECT_LAUNCH=1 ASX_LAUNCH_PKG=com.ss.android.article.news ASX_APK_PATH=/data/local/tmp/asx/toutiao.apk ASX_LAUNCH_ACTIVITY=com.ss.android.article.news.activity.MainActivity ASX_WEBVIEW_APK=/data/local/tmp/asx/webview-t.apk ASX_WEBVIEW_LIB_DIR=/data/local/tmp/asx/webview-t-lib ASX_WEBVIEW_DATA_DIR=/data/local/tmp/asx/webview-t-data nohup sh /data/local/tmp/asx/run_tt.sh >/dev/null 2>&1 & i=0; while [ \$i -lt 900 ]; do grep -q 'Ready to accept' asx.err 2>/dev/null && break; sleep 0.1; i=\$((i+1)); done; ./spawn_client /dev/unix/socket/AppSpawnX com.ss.android.article.news >/dev/null 2>&1; echo spawned" >/dev/null
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
  dev "echo \"  sidechan=\$(grep -ac 'side-channels started' $C) VRI=\$(grep -ac ViewRootImpl $C) segv=\$(grep -ac CHILDSEGV $C) ifoob=\$(grep -ac WESTLAKE-IFOOB $C)\""
  echo "  RS nodes visible: $(dev "hidumper -s RenderService -a RSTree 2>/dev/null | grep -ac 'ss.android.article.news.*Visible: 1'")"
}

case "${1:-walk}" in
  launch) launch ;;
  vt)     vt ;;
  tap)    tap "$2" "$3"; echo "tapped $2 $3" ;;
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
