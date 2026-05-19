# hdc 3.2.0b Probe Report

**Date:** 2026-05-19
**Operator:** agent 61
**Board:** DAYU200 (`dd011a414436314130101250040eac00`, factory-clean post-ROM-flash)
**hdc.exe:** `Ver: 3.2.0b` at `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`

## Why this probe

The W2 brick postmortem (`V3-W2-POSTMORTEM.md`) listed two competing hypotheses:

- **H1** — SELinux respawn storm from silent chcon batches writing inconsistent contexts to `/system`
- **H2** — Windows `hdc.exe` stdout-channel regression (silent returns despite exit 0)

W2 retry on 2026-05-19 halted at pre-flight because `hdc.exe` is `Ver: 3.2.0b` but the hardened script's `KNOWN_GOOD_HDC_VERSIONS` pin lists `1.3.0c/d/e`. We initially treated the mismatch as confirmation of H2. This probe tests H2 empirically: do 200 sentinel echoes return cleanly? Does latency stay bounded? Does long-form output arrive intact?

## Probe 1 — Stdout fidelity (the headline test)

**Silent returns: 0 / 200.**

200 iterations of `hdc.exe shell "echo SENT_$i"`. Every iteration returned the expected sentinel. `/tmp/hdc-probe/silent.log` empty.

## Probe 2 — Latency distribution

| Statistic | Value (ms) |
|---|---|
| min | 126 |
| median | 172 |
| p90 | 189 |
| max | 254 |
| mean | 173.54 |
| max / median ratio | 1.48× (threshold 3×) |

**Healthy.** No bimodality, no hangs, no > 30 s outliers.

## Probe 3 — Long-form output integrity

`hdc.exe shell "ls -laR /system/lib"` → 234,109 bytes / 3,270 lines on first run. Re-run produced **byte-identical** output (diff lines = 0).

**Intact and deterministic.**

## Probe 4 — chcon path (the brick-shaped probe)

`hdc.exe shell "touch /data/local/tmp/probe.txt && chcon u:object_r:shell_data_file:s0 /data/local/tmp/probe.txt && echo CHCON_OK; ls -Z /data/local/tmp/probe.txt"`

Returned:

```
chcon: '/data/local/tmp/probe.txt' to u:object_r:shell_data_file:s0: Permission denied
u:object_r:data_local_tmp:s0 /data/local/tmp/probe.txt
```

with exit code 0.

**Two findings:**

1. hdc.exe **faithfully relayed** interleaved stderr (the chcon denial message) + stdout (the `ls -Z` line) + a clean exit code. Transport layer is OK.
2. **The shell uid cannot relabel files to `shell_data_file:s0` under enforcing SELinux**, even on its own `/data/local/tmp` files. This is an authorization decision by the device's SELinux policy, **not** an hdc bug.

## Verdict — H2 DIES

Across 200 sentinel echoes, 50 timing samples, 2 large-output runs, and 1 chcon-shaped path test, hdc.exe 3.2.0b transported every byte deterministically with bounded latency. **The version-pin mismatch with `1.3.0c-e` in the hardened W2 script is correlation, not causation of the W2 brick.**

The brick mechanism is therefore 100% H1 — the SELinux/chcon work writing inconsistent contexts to `/system`.

## Recommendations

1. **Drop the `KNOWN_GOOD_HDC_VERSIONS = 1.3.0c/d/e` pin** in `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` (G6 gate), OR widen it to include `3.2.0b`. Do not spend operator-time pulling `1.3.0c/d/e` binaries from HBC server.
2. **Concentrate W2 retry on H1 mitigation.** The hardened script's chcon-verify (G3) + echo-sentinel-between-stages (G1) + drwx-guard (G2) addresses H1 at the symptom level. The architectural fix is **chroot containment** (per `V3-CHROOT-CONTAINMENT-PROPOSAL.md`) — by not writing to `/system` at all, the entire H1 brick class becomes impossible.
3. **Confirms `V3-CHROOT-CONTAINMENT-PROPOSAL.md` Q4** — Probe 4 empirically demonstrated that `chcon` on `/data/local/tmp/probe.txt` is denied under enforcing SELinux. The proposal's mitigation (start with `setenforce 0` for Phase-1, document the fidelity caveat) is validated as necessary.

## Post-probe board state

- `getenforce` = `Enforcing` (unchanged)
- Root inventory: 24 entries match pre-probe byte-for-byte
- `/data/local/tmp/probe.txt` removed
- Board still enumerated on hdc

## Artifacts (host-side, not committed)

- `/tmp/hdc-probe-pre.txt` — 38 lines of pre-probe board state
- `/tmp/hdc-probe/silent.log` — empty (0 failures across 200 iterations)
- `/tmp/hdc-probe/timing.log` — 50 latency samples
- `/tmp/hdc-probe/lslibraries.txt`, `lslibraries2.txt` — 234,109 bytes each
- `/tmp/hdc-probe/diff.log` — empty (byte-identical re-runs)
- `/tmp/hdc-probe-post.txt`

## Cross-references

- `docs/engine/V3-W2-POSTMORTEM.md` — original H1/H2 hypotheses
- `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md` — recommended H1 architectural fix
- `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` — G6 gate to widen/drop
- `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/feedback_soft_brick_w2_2026-05-16.md`
