# Westlake Platform-First Contract

Last updated: 2026-04-20

This document is the repo-pushed version of the active Westlake delivery
contract.

## Goal

Deliver Westlake as a stable standalone Android guest platform. McDonald's is
the proving app, not the primary object of patching.

Required outcome:
- guest execution in Westlake `dalvikvm`
- stable Java/framework bootstrap
- coherent generic activity/window/looper/input/surface host contract
- app-owned AndroidX/AppCompat running on that substrate
- only then real McDonald's UI

## Active Milestones

- `P0` Guest execution purity
- `P1` Standalone guest boot
- `P2` Core Java/framework bootstrap
- `P3` Generic app host contract
- `P4` AndroidX/AppCompat host contract
- `P5` McDonald's real launch
- `P6` McDonald's real dashboard body
- `P7` Stable interaction

## Immediate Critical Path

Current accepted recovery branch shows:
- guest reaches `WestlakeLauncher.main()`
- old pre-`main` argv/classloader crash is cleared

Current active blockers:
- `ThreadLocal` / `AtomicInteger` bootstrap
- `Looper.<clinit>`
- later `SIGBUS` before manual `ActivityThread` startup

So the current execution order is:
1. fix core bootstrap
2. reach stable manual `ActivityThread`
3. stabilize generic host contract
4. only then resume McDonald's launch and UI work

## Delivery Rule

Platform-first means:
- no more counting fallback UI polish as progress
- no more broad launcher-side AndroidX replay as the primary strategy
- no more app-specific surgery before the platform gates above are green

Program execution rule:
- the critical path stays local
- sidecar analysis and audits are delegated to the agent swarm
- issue order follows dependency order
- internal restore/classify/bookkeeping loops do not become user-facing stop
  points

The detailed local tracker remains in:
- `$HOME/openharmony/WESTLAKE-PLATFORM-FIRST-DELIVERY-CONTRACT.md`
- `$HOME/openharmony/WESTLAKE-PLATFORM-FIRST-OPEN-ISSUES.md`
