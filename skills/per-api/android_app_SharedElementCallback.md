# SKILL: android.app.SharedElementCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.SharedElementCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.SharedElementCallback` |
| **Package** | `android.app` |
| **Total Methods** | 8 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (12%) |
| **Partial/Composite** | 7 (87%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `SharedElementCallback` | `SharedElementCallback()` | 6 | near | moderate | `getRequestCallback` | `getRequestCallback(want: Want): RequestCallback` |
| `onSharedElementEnd` | `void onSharedElementEnd(java.util.List<java.lang.String>, java.util.List<android.view.View>, java.util.List<android.view.View>)` | 5 | partial | moderate | `isSharedBundleRunning` | `isSharedBundleRunning(bundleName: string, versionCode: number): Promise<boolean>` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onSharedElementsArrived` | 5 | partial | Store callback, never fire |
| `onRejectSharedElements` | 5 | partial | Store callback, never fire |
| `onMapSharedElements` | 4 | partial | Store callback, never fire |
| `onCreateSnapshotView` | 4 | partial | Return dummy instance / no-op |
| `onSharedElementStart` | 4 | partial | Return dummy instance / no-op |
| `onCaptureSharedElementSnapshot` | 4 | partial | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.SharedElementCallback`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.SharedElementCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
