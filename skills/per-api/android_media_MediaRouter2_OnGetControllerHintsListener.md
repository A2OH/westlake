# SKILL: android.media.MediaRouter2.OnGetControllerHintsListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter2.OnGetControllerHintsListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter2.OnGetControllerHintsListener` |
| **Package** | `android.media.MediaRouter2` |
| **Total Methods** | 4 |
| **Avg Score** | 5.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 2 (50%) |
| **Partial/Composite** | 2 (50%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `RouteCallback` | `MediaRouter2.RouteCallback()` | 8 | near | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `onRoutesAdded` | `void onRoutesAdded(@NonNull java.util.List<android.media.MediaRoute2Info>)` | 6 | near | moderate | `dateAdded` | `readonly dateAdded: number` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onRoutesChanged` | 3 | composite | Store callback, never fire |
| `onRoutesRemoved` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter2.OnGetControllerHintsListener`:


## Quality Gates

Before marking `android.media.MediaRouter2.OnGetControllerHintsListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
