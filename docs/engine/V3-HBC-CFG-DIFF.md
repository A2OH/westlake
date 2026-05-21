# HBC V7 cfg diff — what we shipped vs what HBC's working V7 has
Date: 2026-05-21
Agent: 102 (~1h static cfg diff sweep, no board contact)
Predecessor: agent 101 (`V3-HBC-DEPLOY-STAGE-AS-IS-RUN.md`, commit `e1fdd2c5`)
Source-of-truth comparison: `[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]`
  $HOME/adapter/  (pulled fresh 2026-05-21 12:19 PDT)

## TL;DR (3 bullets)

1. **3 of 4 Stage-3f cfgs are byte-identical** to HBC's current authoritative copies
   (`appspawn_x_sandbox.json`, `file_contexts`, `ld-musl-namespace-arm.ini` —
   md5 match across both bundles).
2. **`appspawn_x.cfg` is divergent** — and the divergence is in our hardened-deploy
   staging copy (`v3-hbc/etc/appspawn_x.cfg`, 2989 B, md5 `76ed696d…`) NOT in our
   archived source copy (`v3-hbc/adapter-src/framework/appspawn-x/config/appspawn_x.cfg`,
   4246 B, md5 `3e1f9ac1…`, byte-identical to HBC's current).
3. **Highest-likelihood root cause of the soft-brick agent 101 reproduced:** our M7
   hardened deploy script pushes the divergent **boot-mode + critical:[0]** `etc/`
   copy, which differs from HBC's working **ondemand:true** version in *exactly* the
   way the `feedback_appspawn_x_critical_boot_brick.md` rule warns will brick the
   board. HBC's `deploy_stage.sh` (which agent 101 ran as-is) reads from
   `$FW_CFG=$ADAPTER_ROOT/framework/appspawn-x/config/` — i.e. our `adapter-src/`
   copy — so agent 101's brick is NOT caused by this divergence directly. But it IS
   a real, separate, blocking divergence for our hardened-deploy path.

## 1. cfg-by-cfg diff

### 1.1 `appspawn_x.cfg` — DIVERGENT (only in our `etc/` staging copy)

Four copies, three md5s match, one is different:

| Path | Size | md5 | Source |
|------|-----:|-----|--------|
| `$HOME/adapter/framework/appspawn-x/config/appspawn_x.cfg` (HBC live) | 4246 | `3e1f9ac1…` | HBC server, 2026-05-21 pulled |
| `westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/config/appspawn_x.cfg` (our archived source) | 4246 | `3e1f9ac1…` | initial v3-hbc bundle, 2026-05-16 |
| `hbc-deploy-as-is/adapter/framework/appspawn-x/config/appspawn_x.cfg` (our previously-pulled HBC snapshot) | 4246 | `3e1f9ac1…` | agent 101's pull, 2026-05-21 |
| `westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg` (what our M7 hardened script pushes) | **2989** | **`76ed696d…`** | locally edited 2026-05-21 09:44 |

Diff (ours → HBC's, in-position):

```diff
@@ services[0] -- key block
-"path" : ["/system/bin/appspawn-x", "--socket-name", "AppSpawnX"],
+"path" : ["/system/bin/sh", "-c",
+          "exec /system/bin/appspawn-x --socket-name AppSpawnX 2>>/data/local/tmp/parent_appspawnx.stderr"],
 "importance" : -20,
-"critical" : [0],          <-- BIGGEST DELTA: forces init reboot-loop on appspawn-x failure
 "uid" : "root",
 ...
 "sandbox" : 0,
-"disabled" : 0,
-"start-mode" : "boot",     <-- ditto: HBC has ondemand instead, init won't reboot-loop
+"ondemand" : true,
 "console" : 0,
 ...
@@ services[0].env -- new entry HBC adds, ours lacks
+{ "name": "APPSPAWNX_CHECK_JNI", "value": "1" },
 { "name": "LD_LIBRARY_PATH", "value": "/system/android/lib:/system/lib/chipset-sdk-sp:/system/lib:/system/lib/platformsdk" }
```

Three concrete differences:

(a) **Service start mode**: ours `critical:[0]` + `disabled:0` + `start-mode:"boot"`; HBC
    `ondemand:true`. This is the *exact* anti-pattern called out in
    `feedback_appspawn_x_critical_boot_brick.md`. critical:[0]+boot = init
    reboot-loop if service fails to start; ondemand+listen-socket = init lazy-spawn
    only when AMS first connects, no reboot-loop.

(b) **Exec wrapper for stderr capture**: HBC wraps the exec in `sh -c "… 2>>/data/local/tmp/parent_appspawnx.stderr"`.
    This is described as a "2026-05-06 DIAGNOSTIC (TEMPORARY)" but is still in the
    live HBC cfg — they kept it.

(c) **CheckJNI env var**: HBC adds `APPSPAWNX_CHECK_JNI=1` in the env block. Diagnostic
    aid — not security-critical.

### 1.2 `appspawn_x_sandbox.json` — IDENTICAL

```
md5: ours = HBC = 35aa3aff… (both 6666 bytes)
diff: <empty>
```

### 1.3 `file_contexts` — IDENTICAL

```
md5: ours = HBC = (both 1475 bytes)
diff: <empty>
```

HBC source path is `ohos_patches/base/security/selinux_adapter/sepolicy/ohos_policy/startup/appspawn/system/file_contexts`,
not `out/oh-service/file_contexts` (the latter is what deploy_stage.sh references —
see anomaly §3 below). The file content is identical regardless.

### 1.4 `ld-musl-namespace-arm.ini` — IDENTICAL

```
md5: ours = HBC = (both 3293 bytes)
diff: <empty>
```

## 2. `deploy_stage.sh` diff (our cached HBC pull vs HBC's current)

Sizes: our local `hbc-deploy-as-is/deploy/deploy_stage.sh` = 38472 B,
HBC server current = 38398 B. 74-byte delta. Single hunk:

```diff
@@ -104,8 +104,6 @@ to_winpath() {
     local p="$1"
     if command -v cygpath >/dev/null 2>&1; then
         cygpath -w "$p"
-    elif command -v wslpath >/dev/null 2>&1; then
-        wslpath -w "$p"
     else
         echo "$p" | sed 's|/|\\|g'
     fi
```

We added a `wslpath` fallback in `to_winpath()` — local enhancement only used when
running deploy from WSL. **Not relevant to the brick.** HBC's server runs the
script under Git-Bash (cygpath path), so they don't need this branch.

## 3. Artifact inventory deltas

### 3.1 Files HBC's deploy_stage.sh references that don't exist in HBC's tree

`$OUT/oh-service/file_contexts` (line 508, line 615). HBC's
`$HOME/adapter/out/oh-service/` directory exists but contains only `.so`
files — **no `file_contexts`**. The canonical file_contexts lives under
`ohos_patches/base/security/selinux_adapter/sepolicy/ohos_policy/startup/appspawn/system/file_contexts`
(found via find on HBC server).

**Implication for agent 101's run**: HBC's deploy_stage.sh as-is on HBC server would
likely have errored at Stage 3f line 508 (`stage_push "$OUT/oh-service/file_contexts" …`)
because that file does not exist. Either:
  (i) agent 101 happened to have the file in our local mirror (yes — we cached it
       there at `hbc-deploy-as-is/adapter/out/oh-service/file_contexts` since 2026-05-16),
       which lets the script complete locally; OR
  (ii) HBC has some unbaked path-rewrite they do manually before running.

Either way, the script's reference to a non-existent source is a real bug in the
HBC procedure — not a smoking-gun root-cause for our brick (since our pulled mirror
has the file), but worth flagging.

### 3.2 Files HBC ships in `out/oh-service/lib.unstripped/` we don't have

HBC `out/oh-service/lib.unstripped/` contains 7 unstripped `.so` debug symbols
(libappms, libscene_session, librender_service, librender_service_base, libbms,
libsurface, libskia_canvaskit). We don't ship these. **Not deployed**, so not
relevant to brick.

### 3.3 Files HBC's `out/` has that suggest active work we may have older copies of

HBC has 7 boot-image variants: `boot-image-new`, `boot-image-v8`, `boot-image-v9`,
`boot-image-v9b-v9f`. Our `v3-hbc/bcp/` is a single snapshot from May 16 — possibly
older than HBC's current boot-image. Out of scope for cfg diff but flagging for a
future bytecode/dex-cache mismatch investigation.

## 4. Suspected root-cause cfg difference

**For our hardened-deploy path (M7/M7-v2 series, agents 94-100):** the divergent
`v3-hbc/etc/appspawn_x.cfg` is the most plausible cause of the boot-brick.
It declares appspawn-x as `critical:[0] + start-mode:"boot"` instead of
`ondemand:true`. Per `feedback_appspawn_x_critical_boot_brick.md`, this exact
configuration triggers init's reboot-loop on any appspawn-x start failure.

**For agent 101's run (HBC deploy_stage.sh as-is):** this is NOT the cause, because
that script reads from `$FW_CFG = $ADAPTER_ROOT/framework/appspawn-x/config/` —
which on our mirror is `hbc-deploy-as-is/adapter/framework/appspawn-x/config/`,
which is byte-identical (md5 `3e1f9ac1…`) to HBC's current. So agent 101 pushed
the *correct* (ondemand) cfg and still got a brick. Agent 101's brick must have a
different root cause — something else in Stages 3b-3e (since 3f is now ruled out
for cfg-divergence).

## 5. Recommendations

### Immediate (zero board contact, no risk):

R1. **Re-sync `v3-hbc/etc/appspawn_x.cfg` from `v3-hbc/adapter-src/.../appspawn_x.cfg`**
    (the archived canonical copy that matches HBC live). One-line fix:
    ```
    cp westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/config/appspawn_x.cfg \
       westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg
    ```
    This restores the ondemand+listen-socket variant that agent 99 already proved
    is the safe pre-deploy posture (their isolation test changed start-mode to
    ondemand but kept critical:[0]; the canonical HBC config drops critical
    entirely AND uses ondemand).

R2. **Pin the etc/ copy** with a comment header (`# AUTHORITATIVE: do not edit; sync
    from adapter-src/`) to prevent the next ondemand-isolation test from leaving
    the staging copy stale again.

### Before next deploy retry:

R3. Even after R1, **don't run --reboot on V7 until we have a separate working
    explanation for agent 101's brick** (which used the correct ondemand cfg and
    still bricked). The cfg fix alone is necessary but not sufficient.

R4. Bisect Stages 3b-3e (per agent 99's R-list) to find what else triggers the
    post-reboot wedge. Candidates: boot-image dexopt mismatch (we have older boot
    image than HBC current — see §3.3); SELinux relabel storm on `/system/android`;
    a specific .so version mismatch.

### Process hygiene:

R5. The drift happened because agent 99 modified `etc/appspawn_x.cfg` in place for
    an isolation test, then "restored to ship-original" — but they restored to a
    DIFFERENT "ship-original" (the boot-mode variant) than the actual HBC ship
    version (ondemand). Going forward: any ondemand/critical/start-mode field
    changes should be done in a side-cfg with a clear name (e.g.
    `appspawn_x.cfg.isolation-test`) and the deploy script gated to read the
    canonical name only.

## Appendix: raw file listings

```
HBC live source-of-truth (pulled 2026-05-21 12:19 PDT):
  $HOME/adapter/framework/appspawn-x/config/appspawn_x.cfg            4246  md5=3e1f9ac10c0d1a079db04e01330d5b8f
  $HOME/adapter/framework/appspawn-x/config/appspawn_x_sandbox.json   6666
  $HOME/adapter/ohos_patches/third_party/musl/config/ld-musl-namespace-arm.ini  3293
  $HOME/adapter/ohos_patches/base/security/selinux_adapter/sepolicy/ohos_policy/startup/appspawn/system/file_contexts  1475
  $HOME/adapter/deploy/deploy_stage.sh                                38398 (mtime 2026-05-11 19:38 server-local)

Ours, hardened-deploy staging (what M7 pushes):
  westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg                                  2989  md5=76ed696da31ba71c46f9ad1c76d4a892   <-- divergent
  westlake-deploy-ohos/v3-hbc/etc/appspawn_x_sandbox.json                         6666
  westlake-deploy-ohos/v3-hbc/etc/file_contexts                                   1475
  westlake-deploy-ohos/v3-hbc/etc/ld-musl-namespace-arm.ini                       3293

Ours, archived source bundle (what we originally pulled from HBC):
  westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/config/appspawn_x.cfg  4246  md5=3e1f9ac10c0d1a079db04e01330d5b8f  <-- canonical

Ours, HBC-as-is mirror (agent 101 pulled, what HBC deploy_stage.sh reads):
  hbc-deploy-as-is/adapter/framework/appspawn-x/config/appspawn_x.cfg              4246  md5=3e1f9ac10c0d1a079db04e01330d5b8f
  hbc-deploy-as-is/adapter/out/oh-service/file_contexts                            1475   (cached locally; ABSENT on HBC server!)
```
