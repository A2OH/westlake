# SKILL: android.media.MediaCas.EventListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCas.EventListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCas.EventListener` |
| **Package** | `android.media.MediaCas` |
| **Total Methods** | 4 |
| **Avg Score** | 4.9 |
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
| `onSessionEvent` | `default void onSessionEvent(@NonNull android.media.MediaCas, @NonNull android.media.MediaCas.Session, int, int, @Nullable byte[])` | 6 | near | moderate | `sessionType` | `readonly sessionType: AVSessionType` |
| `onEvent` | `void onEvent(@NonNull android.media.MediaCas, int, int, @Nullable byte[])` | 6 | near | moderate | `eventType` | `eventType: InterruptType` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onPluginStatusUpdate` | 4 | composite | Log warning + no-op |
| `onResourceLost` | 3 | composite | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCas.EventListener`:


## Quality Gates

Before marking `android.media.MediaCas.EventListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
