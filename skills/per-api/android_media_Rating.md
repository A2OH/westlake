# SKILL: android.media.Rating

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.Rating`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.Rating` |
| **Package** | `android.media` |
| **Total Methods** | 13 |
| **Avg Score** | 3.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (53%) |
| **No Mapping** | 6 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isRated` | `boolean isRated()` | 6 | partial | moderate | `isFavorite` | `isFavorite(callback: AsyncCallback<boolean>): void` |
| `getStarRating` | `float getStarRating()` | 6 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getRatingStyle` | `int getRatingStyle()` | 5 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getPercentRating` | `float getPercentRating()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `isThumbUp` | `boolean isThumbUp()` | 5 | partial | moderate | `isTrash` | `isTrash(callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `hasHeart` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `newHeartRating` | 1 | none | throw UnsupportedOperationException |
| `newPercentageRating` | 1 | none | throw UnsupportedOperationException |
| `newStarRating` | 1 | none | throw UnsupportedOperationException |
| `newThumbRating` | 1 | none | throw UnsupportedOperationException |
| `newUnratedRating` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.Rating`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.Rating` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
