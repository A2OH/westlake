# SKILL: android.graphics.Interpolator

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Interpolator`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Interpolator` |
| **Package** | `android.graphics` |
| **Total Methods** | 11 |
| **Avg Score** | 4.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (18%) |
| **Partial/Composite** | 7 (63%) |
| **No Mapping** | 2 (18%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `reset` | `void reset(int)` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `reset` | `void reset(int, int)` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `getValueCount` | `final int getValueCount()` | 5 | partial | moderate | `napi_get_value_string_utf16` | `NAPI_EXTERN napi_status napi_get_value_string_utf16(napi_env env,
                                                    napi_value value,
                                                    char16_t* buf,
                                                    size_t bufsize,
                                                    size_t* result)` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getKeyFrameCount` | 4 | partial | Return safe default (null/false/0/empty) |
| `setKeyFrame` | 4 | partial | Log warning + no-op |
| `setKeyFrame` | 4 | partial | Log warning + no-op |
| `setRepeatMirror` | 3 | composite | Log warning + no-op |
| `timeToValues` | 3 | composite | throw UnsupportedOperationException |
| `timeToValues` | 3 | composite | throw UnsupportedOperationException |
| `Interpolator` | 1 | none | throw UnsupportedOperationException |
| `Interpolator` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Interpolator`:


## Quality Gates

Before marking `android.graphics.Interpolator` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
