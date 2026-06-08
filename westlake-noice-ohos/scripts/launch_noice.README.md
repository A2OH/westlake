# launch_noice.sh — contract (collect the full file from the device)

The authoritative `launch_noice.sh` lives on the device at
`/data/local/tmp/launch_noice.sh`. **Collect it** (it is listed in
`BASELINE-MANIFEST.md` item 12). This file documents its contract so you can
verify or re-derive it.

It is a deterministic, self-recovering noice launcher that runs ON DEVICE and
addresses these root causes:

1. **`start_asx.sh` must NOT rm the AppSpawnX socket** — AMS binds to its inode;
   removing it detaches AMS routing → "aa start ok but no fork" wedge. (Fixed in
   `start_asx.sh`, included here.)
2. **AMS↔appspawn-x race** — AMS may connect to the socket (created early at
   Phase 1) *before* appspawn-x finishes warming its ART VM
   ("Phase 4: Ready to accept spawn requests"). A premature connect → EPIPE
   (errno 32) → AMS marks the bundle failed-spawning → persistent wedge.
   ⇒ **WAIT for "Phase 4" in `/data/local/tmp/asx_run.err` before any `aa start`.**
3. **Do NOT restart appspawn-x as recovery** — it strands AMS's connection (stale
   socket) → harder wedge. If spawns persistently fail, exit
   `LAUNCH_NEEDS_REBOOT` (the only clean AMS reset is a reboot).
4. **Rate-limit the `bpfgrant` network-grant loop (sleep 2)** — a tight loop
   destabilizes the kernel (observed: full-device reboot loop).
5. **Correct child detection**: noice = an `appspawn-x --socket-name` process
   owned by uid **13731**.

Output contract:
- `LAUNCH_OK pid=<pid> drew=<0|1> attempt=<n>` (exit 0)
- `LAUNCH_NEEDS_REBOOT` (exit 2) — host wrapper should reboot, then re-run
- `LAUNCH_FAIL` (exit 1)

Bring-up order (host side):
```sh
setenforce 0
setsid /system/bin/sh /data/local/tmp/start_asx.sh >/dev/null 2>&1 &
# wait for socket, relabel:
chcon u:object_r:appspawn_socket:s0 /dev/unix/socket/AppSpawnX
# wait for "Phase 4" in asx_run.err, then:
sh /data/local/tmp/launch_noice.sh
```

Known flakiness: ~50% of boots are "bad" (empty sound library from a flow race,
or a busy main-thread spin where posted input runnables never run —
`dispatchTouchViaViewRoot invokeCount>0 runCount=0`, main-thread CPU climbing).
A good boot shows a populated library (~68 KB screenshot) and main-thread CPU
near-idle. Reroll (reboot + relaunch) until good.
