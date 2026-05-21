# appspawn-x Ondemand Isolation Test

Date: 2026-05-21
Board: DAYU200 V7 ROM (post-operator-reflash)
Agent: 99
Deploy script: `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` (M7-v2)
Deploy log: `/tmp/v3-isolate-99-deploy.log` (234 lines)

## TL;DR (3 bullets)

- **Hypothesis REFUTED**: rendering appspawn-x non-critical (`critical:[]` + `start-mode:ondemand`) and rebooting via `hdc target boot` (NOT `--reboot`-flagged auto-deploy) did NOT prevent the post-Stage-3.8 brick. Board went silent for 22+ minutes despite appspawn-x being unable to even attempt to boot.
- The bricking is therefore NOT (solely) caused by appspawn-x critical-fail reboot looping. Stages 0-3.8 all passed; only the soft reboot brings the board down. Some OTHER artifact landed by V3 Stages 3b-3f triggers the wedge on the very first post-deploy boot.
- Crash signature CANNOT be captured because the board never re-enumerated — we never reached the "manually start appspawn-x as one-shot" step. The diagnostic this dispatch was designed to enable is itself blocked by an upstream wedge whose root cause is now narrowed to *not-appspawn-x-critical-fail*.

## 1. Pre-deploy board state

```
$ hdc list targets
dd011a414436314130101250040eac00

$ hdc shell 'echo SANITY_$(date +%s); getenforce; uname -a'
SANITY_1609503069
Enforcing
Linux localhost 6.6.101 #1 SMP Sat Apr  4 16:40:55 CST 2026 aarch64 Toybox
```

Channel A alive, SELinux Enforcing, kernel 6.6.101 aarch64, factory baseline (Stage 0 confirmed factory-clean).

## 2. Cfg modification diff (original vs test)

```diff
--- /tmp/appspawn_x.cfg.original
+++ westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg
@@ -30,1 +30,1 @@
-            "critical" : [0],
+            "critical" : [],
@@ -50,1 +50,1 @@
-            "start-mode" : "boot",
+            "start-mode" : "ondemand",
```

Exactly the 2-line diff requested. Cfg backed up to `/tmp/appspawn_x.cfg.original`.

## 3. Deploy result (Stages 0-3.8 without --reboot)

All stages PASS:

| Stage | Result | Notes |
| --- | --- | --- |
| 0    | PASS | preflight, factory baseline clean |
| 1    | PASS | 13 .orig_20260521 backups |
| 2    | SKIP | foundation-stop opt-out |
| 3.0  | PASS | 4 dirs + staging area |
| 3b   | PASS | 11 .so + 1 symlink |
| 3c   | PASS | 38 AOSP + 3 dual-path shims |
| 3d   | PASS | 12 jars + fonts + ICU |
| 3e   | PASS | 27 boot files |
| 3f   | PASS | M7-v2 tarball: bins/cfgs/symlinks + restorecon -R |
| 3.7  | PASS | chcon final sweep G2 |
| 3.9  | PASS | 101 files md5+size verified |
| 3.8  | PASS | channel-alive sentinel G1 ANTI-W2 |

Script correctly halted after Stage 3.8 with WARN message (no `--reboot` flag). No implicit reboot triggered.

On-device verification of cfg landing:

```
-rwxr-xr-x 1 root root u:object_r:appspawn_exec:s0    110256 2026-05-22 00:24 /system/bin/appspawn-x
-rw-r--r-- 1 root root u:object_r:system_etc_file:s0    2992 2026-05-22 00:24 /system/etc/init/appspawn_x.cfg

# cat snippet (relevant lines):
"name" : "appspawn-x",
"critical" : [],
"start-mode" : "ondemand",
"secon" : "u:r:appspawn:s0",
```

Cfg confirmed landed with safe semantics. SELinux labels correct.

## 4. Soft reboot result

```
[reboot issuing at Thu May 21 09:27:11 PDT 2026]
hdc target boot   →   returned immediately
[reboot issued at Thu May 21 09:27:12 PDT 2026]

# Poll loop: every 5-15 s for 22+ minutes
# Final check at Thu May 21 09:49:20 PDT 2026 → list targets [Empty]
```

Board re-enumeration: **NEVER (>22 min)**. Tried `hdc kill` + `hdc start` reset of host-side server — no effect. Tried second extended poll round — no effect.

Post-reboot state: **dead (USB-silent)**.

## 5. Manual appspawn-x start result + crash signature

**N/A — board never returned**. The probe this dispatch was designed to run could not be executed because the upstream wedge prevented us from reaching a post-reboot shell.

No exit code captured. No avc denials captured. No faultlog captured. No crash backtrace captured.

## 6. Symptom classification

**Symptom NEW (E): board fails to reach hdcd-listen state after V3 deploy reboot, even with appspawn-x explicitly disabled-at-boot.**

This is a new class — not A/B/C/D from the dispatch. The dispatch's classes A-D presume appspawn-x at least *attempts* to start. We never got that far. The failure is earlier in init or in something else that V3 deploy mutates.

## 7. Root cause + recommended fix

### Root cause analysis

The bricking-root-cause memo (`feedback_appspawn_x_critical_boot_brick.md`) attributed Stage-4 bricks to "appspawn-x is critical:[0] + start-mode:boot, fails → init reboot loop". This dispatch tested that hypothesis by neutralising both attributes before reboot. **The brick reproduced anyway.** Therefore appspawn-x critical-fail-reboot-loop is NOT the (sole) root cause of V3 post-deploy bricks.

Candidate alternative root causes (not yet narrowed):

1. **One of the OTHER cfgs we land in Stage 3f** declares a critical boot service whose failure reboot-loops init. Stage 3f deploys 4 cfg files; only one is `appspawn_x.cfg`. The other 3 need an audit pass identical to the one we did for appspawn_x.cfg.
2. **A shim .so (Stages 3b/3c) violates a kernel/SELinux invariant** that only manifests at first-boot service init — e.g. a stub returning bad values that crashes a critical OHOS service like foundation or render_service.
3. **Boot image (Stage 3e) or framework jars (Stage 3d) trigger a kernel panic or selinux init-policy-load failure** before init even reaches our cfgs. The 27 boot files + 12 jars get loaded by appspawn/ART; if appspawn (ondemand or not) is never invoked but some other init job touches them, that job becomes the new suspect.
4. **`/system/android` directory existence itself** confuses some OH boot job (a check that "factory baseline" was supposed to confirm absence-of). Stage 3.0 creates this dir; once it exists, a subsequent boot may try to enumerate it under wrong policy.
5. **Init's `/system/etc/init/` scan picks up something other than appspawn-x as the SUPER-critical service** — e.g. a job declaration we land that has its own `"critical":[...]` we didn't notice. The `jobs` array at the top of appspawn_x.cfg still runs at "boot" trigger even though the service is ondemand. Its `mount cgroup`, `chown`, and `mkdir` lines could themselves trigger init abort if any predicate fails.

### Recommended next probe (NOT for this dispatch)

a) **Audit ALL 4 cfgs that Stage 3f lands** (mirror this dispatch's structure for each). Specifically check for any other `critical:[N]` + `start-mode:boot` combination.

b) **Strip the `jobs` array from `appspawn_x.cfg`** in addition to non-critical+ondemand. Lines 2-25 still execute at boot trigger; `mount cgroup none /dev/pids pids` could fail on V7 and re-trigger the wedge if any job is "must-succeed".

c) **Bisect Stage 3b vs 3c vs 3d vs 3e vs 3f** by deploying only Stage 3b then rebooting; if alive → add 3c; etc. This is operator-time-expensive (~5 reflash-reboot cycles) but pinpoints the bricking stage.

d) **Check if `/system/android` directory existence at boot** triggers a SELinux relabel/restorecon-on-boot that crashes. Specifically: factory image has no `/system/android`; our deploy creates it. First-boot init may scan it under wrong policy and abort.

### Anti-recommendation

Do NOT continue iterating on appspawn-x ondemand/critical attributes alone. The empirical evidence from this dispatch demonstrates that attribute is not the load-bearing pivot. Spend any further isolation cycles on (a)-(d) above, prioritising (a) and (b) (cheap, can be done without operator reflash if we can recover the board).

## 8. Cfg restored locally

YES.

```
$ diff /tmp/appspawn_x.cfg.original westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg
(no output — identical)
```

`westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg` is back to `critical:[0]` + `start-mode:boot` (the ship-original state). Test-only modification is local-to-/tmp only.

## Board state at end of dispatch

**OFFLINE / USB-silent for 22+ minutes after `hdc target boot`.** Requires operator hard power-cycle (or full reflash) before any further W2 work. Add to the `feedback_appspawn_x_critical_boot_brick.md` memo a CORRECTION: appspawn-x neutralisation is INSUFFICIENT to render V3 deploy reboot-safe. Some other Stage-3b-through-3f artifact is co-implicated.

## Artifacts

- `/tmp/v3-isolate-99-deploy.log` — 234-line full deploy transcript
- `/tmp/appspawn_x.cfg.original` — backup of original cfg
- `docs/engine/V3-W2-APPSPAWN-X-ISOLATION.md` — this report

## Memo updates required (suggested, not done by this dispatch)

`memory/feedback_appspawn_x_critical_boot_brick.md` should add a 2026-05-21 update note: "Hypothesis empirically refuted by agent-99 ondemand isolation test; appspawn-x non-critical+ondemand DID NOT prevent post-deploy brick. See `docs/engine/V3-W2-APPSPAWN-X-ISOLATION.md`. Bricking root cause is broader than appspawn-x and requires Stage 3b-3f bisection."
