# SKILL: android.app.PictureInPictureParams.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.PictureInPictureParams.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.PictureInPictureParams.Builder` |
| **Package** | `android.app.PictureInPictureParams` |
| **Total Methods** | 5 |
| **Avg Score** | 3.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 2 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setActions` | `android.app.PictureInPictureParams.Builder setActions(java.util.List<android.app.RemoteAction>)` | 6 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |
| `setSourceRectHint` | `android.app.PictureInPictureParams.Builder setSourceRectHint(android.graphics.Rect)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setAspectRatio` | `android.app.PictureInPictureParams.Builder setAspectRatio(android.util.Rational)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Builder` | 1 | none | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.PictureInPictureParams.Builder`:


## Quality Gates

Before marking `android.app.PictureInPictureParams.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
