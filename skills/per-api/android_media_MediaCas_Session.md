# SKILL: android.media.MediaCas.Session

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCas.Session`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCas.Session` |
| **Package** | `android.media.MediaCas` |
| **Total Methods** | 5 |
| **Avg Score** | 5.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 2 (40%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `sendSessionEvent` | `void sendSessionEvent(int, int, @Nullable byte[]) throws android.media.MediaCasException` | 6 | near | moderate | `sendSystemAVKeyEvent` | `sendSystemAVKeyEvent(event: KeyEvent, callback: AsyncCallback<void>): void` |
| `setPrivateData` | `void setPrivateData(@NonNull byte[]) throws android.media.MediaCasException` | 5 | partial | moderate | `OH_ImageSource_UpdateData` | `int32_t OH_ImageSource_UpdateData(const ImageSourceNative* native, struct OhosImageSourceUpdateData* data)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `processEcm` | 3 | composite | throw UnsupportedOperationException |
| `processEcm` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCas.Session`:


## Quality Gates

Before marking `android.media.MediaCas.Session` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
