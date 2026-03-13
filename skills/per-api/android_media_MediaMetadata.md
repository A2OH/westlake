# SKILL: android.media.MediaMetadata

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaMetadata`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaMetadata` |
| **Package** | `android.media` |
| **Total Methods** | 10 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 6 (60%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `int size()` | 10 | direct | trivial | `size` | `readonly size: number` |
| `getLong` | `long getLong(String)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getText` | `CharSequence getText(String)` | 6 | near | moderate | `getNextObject` | `getNextObject(callback: AsyncCallback<FileAsset>): void` |
| `getRating` | `android.media.Rating getRating(String)` | 6 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getBitmap` | `android.graphics.Bitmap getBitmap(String)` | 6 | partial | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getString` | `String getString(String)` | 5 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `keySet` | 4 | partial | Log warning + no-op |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `containsKey` | 4 | composite | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaMetadata`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaMetadata` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
