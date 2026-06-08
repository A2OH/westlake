#!/bin/sh
# start_asx.sh — Fixed AppSpawnX bringup (runs ON DEVICE).
#
# KEY FIX (G5): do NOT rm the AppSpawnX socket — AMS binds to its inode; removing it
# detaches AMS routing and causes the "aa start succeeds but no fork" wedge.
# We let appspawn-x reuse the path; if a stale socket exists from a dead instance,
# appspawn-x recreates it in place (same path, AMS re-resolves by path on next
# connect). We clear ONLY the per-run logs + child stderrs.
#
# Invocation (detached, so it survives the shell):
#   setsid /system/bin/sh /data/local/tmp/start_asx.sh >/dev/null 2>&1 &
# Then relabel the socket so AMS (SELinux appspawn domain) can connect:
#   chcon u:object_r:appspawn_socket:s0 /dev/unix/socket/AppSpawnX
# and run with SELinux permissive during bringup: setenforce 0
#
# NOTE: LD_PRELOAD must be set HERE (not in an init .cfg) — AT_SECURE strips
# LD_PRELOAD in the appspawn SELinux domain, so the shims only load when
# appspawn-x is started from this script's environment.
rm -f /data/local/tmp/asx_run.out /data/local/tmp/asx_run.err
rm -f /data/service/el1/public/appspawnx/adapter_child_*.stderr 2>/dev/null
# ensure perf_sensitive memcg writepid target exists (G5)
mkdir -p /dev/memcg/perf_sensitive 2>/dev/null
export LD_PRELOAD=/system/android/lib/libsetgidhook.so:/system/android/lib/libw14supp.so:/system/android/lib/libdnshook.so:/system/android/lib/libnetlog.so:/system/android/lib/libjdnshook.so:/system/android/lib/libv4force.so
exec /system/bin/appspawn-x --socket-name AppSpawnX > /data/local/tmp/asx_run.out 2> /data/local/tmp/asx_run.err
