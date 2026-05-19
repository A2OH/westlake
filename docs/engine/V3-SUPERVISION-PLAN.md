# V3 Supervision Plan — dispatch order + dependency DAG

**Date:** 2026-05-16 (initial); **2026-05-19 update:** DAG split for W4-empty + W6-prep + W6-perf + W7-prereq
**Author:** agent 42 (initial); agent 58 (2026-05-19 findings update)
**Status:** AUTHORITATIVE for V3 OHOS path
**Companion:** `V3-ARCHITECTURE.md`, `V3-WORKSTREAMS.md`, `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`, `WESTLAKE-ISLAND-BORROW-MAP.md`, `03-REQUIREMENT-INDEX.md`

---

## 0. TL;DR

- **Critical-path week 1:** W1 (HBC artifact pull) → W2 (smoke HBC standalone on DAYU200; **hardened retry in flight 2026-05-19**) → W3 (replace OhosMvpLauncher).
- **Parallelizable from day 1:** W8 (SceneBoard board config — entirely independent), W11 (audit V2 carryforward — read-only), W12 (CR61.1 disposition — most of which is done today by docs landing), W10 (memory refresh).
- **Post-W2 spike fan-out (NEW 2026-05-19):** W4-empty + W6-prep + W6-perf in parallel — all blocked only on W2 PASS, all parallel-safe with each other and with W8. W4-empty's findings scope W4-engine.
- **Mid-stream (week 2-3):** W4-engine (conditional scope from W4-empty), W5 (mock APK), W9 (HBC pattern adoption + the 2 supporting Island borrows).
- **End-state (week 3-5):** W6 (noice via V3), W7 (McD via V3 — **gated on W7-prereq for camera2/location/keystore coverage**), W13 (archive).
- **First-3-day swarm dispatch (§4):** W12 + W11 + W10 to one agent each; W1 to a separate agent (the rest gated on W1).

---

## 1. Dependency DAG

Updated 2026-05-19 to reflect the W4 split + W6-prep / W6-perf spikes + W7-prereq:

```
W11 (V2 carryforward audit) ──┐
                              │
W12 (CR61.1 disposition) ─────┤
                              ├─→ W10 (memory refresh — needs all docs landed)
                              │
W1 (HBC artifact pull) ─────┐ │
        │                   │ │
        ├─→ W9 (HBC patterns into Westlake tooling
        │       + Island borrows #4 v3-smoke.sh + #5 V3-REDLINES)
        │
        ├─→ W2 (HBC standalone on DAYU200)
        │       │  [hardened retry 2026-05-19, agent 58 sibling]
        │       │
        │       ├─→ W3 (replace OhosMvpLauncher) ─→ W5 (mock APK)
        │       │       │                              │
        │       │       └─→ W13 (archive V2-OHOS)     │
        │       │                                      │
        │       │                              ┌──────┘
        │       │                              ▼
        │       ├─→ W4-empty (pure-HBC spike) ─→ W4-engine (conditional scope)
        │       │       │                              │
        │       │                                      ├─→ W6 ─→ W7
        │       ├─→ W6-prep (peer-window validation) ──┘    ▲
        │       │                                            │
        │       └─→ W6-perf (FPS harness) ───────────────────┘
        │                                                    │
        │                          W7-prereq (camera2/location/keystore)
        │                          (sub-W of W7, may also seed a new W14)
        │
        └─→ (W9 + W10)

W8 (SceneBoard) — ENTIRELY INDEPENDENT — runs in parallel with all of the above.
                 BUT: if W6-prep fails due to SCB-off, W8 becomes a hard blocker for W6.
```

**Critical path (longest dependency chain, updated):**
W1 → W2 → {W4-empty, W6-prep, W6-perf} parallel → W4-engine → W6 → W7 (+ W7-prereq).
Roughly 2-3 + 3-5 + max(2-3, 3-5, 1-2) + 2-8 + 5-8 + 4-6 + 1-3 (W7-prereq) = **20-38 person-days** on critical path. The "max" term collapses 3 spikes into 1 wall-clock-week if 3 agents land in parallel.

**Total person-days estimated:** 38-60 (across all 13 workstreams + 3 new spikes + 1 W7-prereq). The W4-empty spike may *reduce* total by collapsing W4-engine to ~2 PD if both apps reach first frame on bare HBC.

---

## 2. Priority ordering

Ranks each workstream `P0` (must-do, blocks everything) → `P3` (nice-to-have, low urgency).

| W | Title | Priority | Why |
|---|---|---|---|
| W12 | CR61.1 amendment landed | P0 | Required for V3 internal consistency. **Mostly done today by the doc-landing commit.** Remaining work = archive moves. |
| W10 | Memory + handoff refresh | P0 | Next agent needs V3 START HERE pointer. Without this, the swarm regresses to V2 thinking. |
| W11 | V2 carryforward audit | P0 | Without this, W3/W13 can't safely archive V2 code (might miss something needed by Android-phone path). |
| W1 | HBC artifact pull | P0 | Blocks W2-W7, W9, W13. The "go/no-go" gate for the whole V3 path. |
| W2 | HBC standalone smoke on DAYU200 | P1 | Proves W1 was complete; foundation for all app-hosting work. Hardened retry in flight 2026-05-19. |
| W3 | Replace OhosMvpLauncher | P1 | Switches Westlake into HBC's launch model — the architectural pivot in code. |
| **W4-empty** | **Pure-HBC baseline spike** | **P1** | **Scopes W4-engine. Cheap (2-3 PD). Subtractive evidence before additive work.** |
| **W4-engine** | Adapter customization (scoped from W4-empty) | P1 | Required for W6/W7. Effort range 2-8 PD; W4-empty narrows. |
| W5 | Mock APK validation | P1 | Generic smoke before tackling Hilt (noice) or cross-pkg (McD). Cheap insurance. |
| **W6-prep** | **Composable peer-window validation** | **P1** | **Product goal #1 architectural check. If it fails, W6/W7 product shape changes.** |
| **W6-perf** | **FPS harness on HBC HelloWorld** | **P1** | **Product goal #2 architectural check. Independent of W4 path.** |
| W6 | noice via V3 | P2 | First "real app" demo via V3. Direct comparable to V2 in-process Option 3 result on phone. |
| W7 | McD via V3 | P2 | Second real app; harder due to cross-package intents. **W7-prereq (camera2/location/keystore coverage) gates acceptance.** |
| W9 | HBC Tier-1 patterns + Island borrows #4/#5 | P2 | Quality-of-life for V3 team. Can land in parallel with W2/W3/W4-empty. |
| W13 | Archive V2-OHOS substrate | P3 | Cleanup. Worth doing once W1-W3 are green; no rush. |
| W8 | SceneBoard board config | P2 (independent) | Required for the peer-window product goal but **not gating** the V3 stack — V3 runs single-app-foreground in DAYU200 stock config exactly as well as V2-OHOS would have. Schedule independently. **W6-prep may upgrade this to hard-blocker if SCB-off prevents 2 peer windows.** |

**Effective dispatch priority for the swarm (updated 2026-05-19):**

1. P0 docs (W10, W11, W12 cleanup) — small, parallelizable, agent-friendly.
2. W1 (HBC pull) — gate for everything else; assign a dedicated agent immediately.
3. Once W1 is green (estimated +2-3 days): W2 + W9 (in parallel).
4. **Once W2 is green** (estimated +3-5 days after W1, plus hardened-retry overhead per 2026-05-19 in-flight retry): **W3 + W4-empty + W6-prep + W6-perf** all in parallel (4 agents if available, all gate solely on W2).
5. Once W4-empty's findings land: scope + dispatch W4-engine.
6. Once W3 is green: W5 + W13 in parallel.
7. Once W5 + W4-engine + W6-prep + W6-perf are all green: W6.
8. Once W6 is green AND W7-prereq is green: W7.
9. W8 runs entirely in parallel from day 1 (dedicated agent if board / device profile decision lands). **If W6-prep fails due to SCB-off, W8 promotes to hard blocker for W6.**

---

## 3. Which Ws can run in parallel

| Phase | Parallel set |
|---|---|
| Day 1 (today) | W10, W11, W12 cleanup, W1 — all independent of each other. **W8 also parallelizable** if a separate engineer / agent owns the SceneBoard decision. |
| Day 2-3 | W1 still in flight; W10/W11/W12 wrapping. W8 continues. |
| Day 4-7 (after W1) | W2, W9 — parallel. W8 continues. (W4 deferred to post-W2 — was incorrectly listed parallel-to-W2 in original plan.) |
| **Day 8-12 (post-W2 fan-out)** | **W3 + W4-empty + W6-prep + W6-perf — 4-way parallel.** W9 finishing. W8 continues. |
| Day 13-18 (post-spike) | W4-engine (scope locked from W4-empty), W5 (post-W3), W13. W8 continues. |
| Day 19-26 (post-W4-engine, W5, W6-prep, W6-perf) | W6 + W7-prereq parallel. W13 finishing. |
| Day 27-32 (post-W6 + W7-prereq) | W7. W8 still continues (or has completed). |

Wall-clock: roughly **4-6 weeks** if the swarm is well-coordinated and HBC's runtime is as well-baked as CR-EE / CR-FF imply. Conservatively budget **6-8 weeks** for first noice + McD via V3 milestone. **W4-empty may compress this if pure HBC reaches first frame on both apps** (W4-engine drops from 5-8 PD to ~2 PD).

This is comparable to CR-DD's 4-5 week Candidate C MVP estimate but with a stronger architectural foundation (real HWUI, real Skia, real framework.jar) and a clearer path to the peer-window goal (via W8 SceneBoard work which is independent).

---

## 4. Suggested first-3-day swarm dispatch

Assume swarm size ~4 agents working in parallel.

| Agent | Days 1-3 task | Deliverable |
|---|---|---|
| Agent A | W1 — HBC artifact pull (full scope) | `third_party/hbc-runtime/` tree + MANIFEST.md + PROVENANCE.md + pull script. **Critical gate.** |
| Agent B | W11 — V2 carryforward audit (read-only) | `V3-V2-CARRYFORWARD-AUDIT.md` — informs W3/W13 archive safety. ~1-2 PD, can finish day 2 then start W9 prep work. |
| Agent C | W10 — Memory + handoff refresh + W12 — CR61.1 downstream disposition | Updated `MEMORY.md`, new `project_v3_direction.md`, new `handoff_2026-05-16.md`, archive moves under `archive/v2-ohos-substrate/`. ~2 PD. |
| Agent D | W8 — SceneBoard decision investigation | `W8-SCENEBOARD-DECISION.md` — does Westlake enable SCB on DAYU200, or target a different OHOS device profile? This is research / strategic, not coding — perfect for day 1-3 of an agent who doesn't have device access yet. |

End-of-day-3 expected state: W1 80%+ complete, W10/W11/W12 done, W8 decision drafted. Agent A continues W1 finalization on day 4; agents B/C pick up W4 / W9 in parallel on day 4 once W1 fully lands; agent D either continues W8 or rotates onto W2 if device access is available.

---

## 5. Acceptance gates (when can each W close?)

Each W has its own acceptance criteria in `V3-WORKSTREAMS.md` §<N>. The supervision-level "gates" below capture *cross-W* validation needs:

| Gate | Triggered when | Validates |
|---|---|---|
| **G1: V3 docs landed** | W10 + W12 docs side complete | Memory points at V3; CR61.1 amendment readable; handoff exists |
| **G2: HBC artifacts ingested** | W1 acceptance all green | Westlake has all binary + source needed to attempt boot |
| **G3: HBC standalone runs** | W2 acceptance all green | Pure HBC stack reaches MainActivity.onCreate on our DAYU200 — proves W1 wasn't missing anything load-bearing |
| **G3.5 (NEW): Pure-HBC reach measured** | W4-empty acceptance | We know how far noice / McD get on bare HBC; W4-engine scope locked |
| **G4: Westlake launches HBC-runtime apps** | W3 acceptance + W5 acceptance | V3 stack hosts a generic Android app end-to-end; OhosMvpLauncher gone |
| **G4.5a (NEW): Composable shape validated** | W6-prep acceptance | 2 Westlake apps + 1 ArkTS app peer-window simultaneously; product goal #1 architecturally proved |
| **G4.5b (NEW): Perf shape validated** | W6-perf acceptance | HBC HelloWorld holds stable FPS (or characterized shortfall); product goal #2 architecturally proved |
| **G5: Real-app parity with V2-on-phone** | W6 acceptance | noice via V3 reaches the same UI surface as V2 in-process Option 3 reached on Android phone |
| **G6: Hard app via V3** | W7 acceptance | McD via V3 reaches Wi-Fry offline screen with cross-pkg intent rewriting working **AND** camera2/location/keystore coverage (W7-prereq) closed |
| **G7: V2-OHOS substrate archived** | W13 acceptance | Default build doesn't touch V2-OHOS code; Android-phone V2 still 14/14 PASS |
| **G8: Peer-window UX (product)** | W8 acceptance | Two V3-hosted Android apps visible simultaneously (the product goal) — subsumes G4.5a if W6-prep PASSED |

G1-G7 are V3-internal gates. G8 is the Westlake product gate. **NEW 2026-05-19**:
G4.5a / G4.5b promote the architectural validations to first-class gates so the
product-shape risk surfaces before W6/W7 invest in real-app integration. G3.5 is
the scope-lock gate for W4-engine — without it, W4-engine is sized by assumption.

---

## 6. Reporting cadence

Per existing project discipline (memory + handoff system):

- **Daily:** swarm-status thread updated by each in-flight agent with their W and current sub-step
- **Per-W completion:** the completing agent produces an `artifacts/v3/w<N>-<short>/CHECKPOINT.md` matching the W's acceptance criteria, and runs the W's self-audit gate
- **Per-G gate completion:** a brief `docs/engine/V3-GATE-<G>.md` summary doc with screenshots / hilog cite / 14/14 status as applicable
- **Weekly handoff:** the supervising agent (or rotating role) writes `handoff_2026-05-<DD>.md` consolidating state, updating `MEMORY.md` "START HERE" pointer

If a W produces unexpected blockers (e.g., HBC artifact has missing symbol that breaks on our DAYU200), the agent reports back rather than improvising. Per CR-EE §11.7: "blame adapter first" RCA discipline — suspect Westlake adapter customization → cross-compile flags → HBC adapter → AOSP → OHOS, in that order. Never editor-edit HBC source.

---

## 7. Risks of the V3 pivot

Listed in agent 42's honest assessment for the supervision plan:

1. **HBC's HelloWorld result is a single-Activity APK with no cross-pkg intent, no Hilt, no Fragment nav. Westlake-side apps (noice, McD) exercise dramatically more of the framework surface than HBC has validated.** Adapter scope diffs (W4-engine) is the workstream where this risk materializes. Mitigation (updated 2026-05-19): **W4-empty measures the actual reach on bare HBC before W4-engine is sized**; W5 mock APK validates generic Android first; W6 (noice) is gated on W4-engine + W5 + W6-prep + W6-perf finishing.

2. **HBC's `liboh_android_runtime.so` register_* stubs (13/15 stubs per CR-EE §12-item-3).** Many native callbacks return safe defaults rather than doing real work. Apps relying on those will silently misbehave. V3 must validate every register_* HBC stubs that our hosted apps hit — likely surfaces during W6/W7 as "feature X doesn't work, why."

3. **HBC's adapter is moving target.** HBC is actively iterating (we audited 2026-05-15). Their build_patch_log.html has "ongoing, hundreds of entries" per CR-EE App. B. If we pull artifacts once at W1 and freeze, we miss their fixes. If we re-pull continuously, we may break our integration. **Mitigation:** W1 manifests HBC version; W4 documents diffs that need upstreaming; periodic re-pull cadence to be defined in W9's RCA-discipline doc. Suggested: re-pull on a fixed cadence (monthly?) or on demand when HBC fixes a bug we hit.

4. **W4 "shadow class" pattern (PathClassLoader-loaded `oh-adapter-runtime-westlake.jar`) is a class of work we don't have hands-on experience with on HBC's actual stack.** The dual-classloader pattern works in HBC's tests; we'll only learn its sharp edges by exercising it ourselves. Mitigation: W4 acceptance includes "at least one shadow class actually loads and gets invoked." Bias toward consume-as-is dispositions; only shadow when blocking.

5. **DAYU200 hardware bottleneck.** Per MEMORY.md, rk3568 is the only OHOS hardware Westlake has access to. SceneBoard off + single-foreground constraint applies equally to V3 as it did to V2-OHOS. **Mitigation:** W8 is independent and can run in parallel; if it identifies a different device profile as the right target, W6/W7 can be re-validated there.

6. **HBC's deploy is 94 files + 4 symlinks per push.** Iteration velocity is slow if we have to deploy that frequently. **Mitigation:** W9 ports HBC's restore_after_sync.sh + DEPLOY_SOP.md (incremental deploy + verify) into our tooling. CR-FF Pattern 2 (dual-classloader adapter in non-BCP jar) means Westlake adapter iteration costs 0 boot-image rebuild — a major velocity win we inherit.

7. **The "this is a pivot from a pivot" risk.** Westlake just yesterday committed to V2-OHOS direction (`project_v2_ohos_direction.md` 2026-05-15 morning), and is now flipping to V3 (2026-05-16). That's two strategic pivots in two days. **Mitigation:** Both pivots are well-justified (V2→V3 driven by CR-EE / CR-FF empirical evidence; V1→V2 driven by additive-shim audit). The `feedback_additive_shim_vs_architectural_pivot.md` rule explicitly encourages this when the previous direction's premise is falsified by new evidence. CR-DD itself proposed HYBRID for similar reasons. V3 is the resolution that earlier CRs were converging toward; not a new direction at the third level of recursion.

8. **The pivot abandons substantial V2-OHOS investment.** Per V3-ARCHITECTURE §4, ~70K+ LOC of substrate work + ~16K LOC of daemon work is archived (not deleted, but no longer the production path). Some of that was educational; some was throwaway per the additive-shim feedback. **Mitigation:** V3 explicitly preserves the V2 *learnings* (5-pillar pattern, CR59 lifecycle drive insight, macro-shim contract, CR60 bitness discipline) — only the *artifacts* are archived. The conceptual learnings carry forward as cited in V3-ARCHITECTURE.md §5.2.

9. **HBC isn't ours.** We coordinate via reading their work tree; we don't have engineering authority over their backlog. If they pivot away from this stack themselves, we inherit the orphan-fork problem. **Mitigation:** W9's RCA discipline + W4's upstream-request mechanism keep us in sync. Worst case: we end up maintaining HBC's artifacts ourselves, which is still less work than V2-OHOS would have been because the engineering is already done.

10. **APK transparency may not survive our app-hosting engine integration.** HBC enforces zero `import adapter.*` in APK source; we add a Westlake engine layer above. If our engine requires APK-side cooperation (manifest aliases, etc.), we violate the invariant. **Mitigation:** V3-ARCHITECTURE §3 footnote on "per-app diff = 4 constants + manifest aliases" preserves the V2 contract — APKs unmodified, manifest aliases handled in our packaging tooling. If a future app forces us to break this, that's a CR.

11. **McD-critical service coverage gap (NEW 2026-05-19).** Per `WESTLAKE-ISLAND-BORROW-MAP.md` §4 ("3-way service coverage table"), three McD-critical packages have **zero runtime evidence** across HBC, Island, and 03-Req: `android.location` (store-finder), `android.hardware.camera2` (QR scanner), `android.security.keystore` (biometric login). W7 acceptance cannot close without addressing all three. **Mitigation:** new **W7-prereq** sub-workstream (see V3-WORKSTREAMS.md) gates W7. Either safe-stub via `NeverDieAdapterDecorator` (cheap, may be insufficient) or JAVA_SYNTH implementations (engine-layer work) — disposition determined when W7-prereq starts. If neither path works for camera2, McD's QR-scanner-dependent flows are out of scope for W7 acceptance and become a follow-on CR.

12. **The "third pivot in two weeks" risk (NEW 2026-05-19).** Per `feedback_two_pivots_in_two_days.md`, a third strategic pivot inside 2 weeks needs evidence harder than V1→V2 or V2→V3 cleared. The Island borrow analysis (commit `9705487c`) **explicitly considered and rejected** a pivot to Island's process-isolation architecture; the verdict was "stay V3 + borrow 5 patterns" precisely because Island's architectural redlines are incompatible with V3's `libbionic_compat.so` premise. **Mitigation:** if a W4-empty / W6-prep / W6-perf failure tempts a 3rd pivot, the discipline bar is documented (≥3 W-level retries in the same V3 layer with same symptom-rotation pattern + independent empirical evidence of replacement + peer review). Do not trivial-flip-3.
