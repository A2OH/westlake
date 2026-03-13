# SKILL: android.media.ExifInterface

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ExifInterface`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ExifInterface` |
| **Package** | `android.media` |
| **Total Methods** | 18 |
| **Avg Score** | 4.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 6 (33%) |
| **Partial/Composite** | 6 (33%) |
| **No Mapping** | 6 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getThumbnail` | `byte[] getThumbnail()` | 10 | direct | trivial | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getThumbnailBytes` | `byte[] getThumbnailBytes()` | 8 | direct | easy | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getThumbnailBitmap` | `android.graphics.Bitmap getThumbnailBitmap()` | 8 | direct | easy | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `hasThumbnail` | `boolean hasThumbnail()` | 8 | near | easy | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getLatLong` | `boolean getLatLong(float[])` | 6 | near | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `getAltitude` | `double getAltitude(double)` | 6 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `isSupportedMimeType` | `static boolean isSupportedMimeType(@NonNull String)` | 6 | partial | moderate | `mimeType` | `mimeType: string` |
| `isThumbnailCompressed` | `boolean isThumbnailCompressed()` | 5 | partial | moderate | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getAttributeDouble` | `double getAttributeDouble(@NonNull String, double)` | 5 | partial | moderate | `getAVCastController` | `getAVCastController(sessionId: string, callback: AsyncCallback<AVCastController>): void` |
| `getAttributeInt` | `int getAttributeInt(@NonNull String, int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `setAttribute` | `void setAttribute(@NonNull String, @Nullable String)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `hasAttribute` | 2 | composite | Return safe default (null/false/0/empty) |
| `ExifInterface` | 1 | none | throw UnsupportedOperationException |
| `ExifInterface` | 1 | none | throw UnsupportedOperationException |
| `ExifInterface` | 1 | none | throw UnsupportedOperationException |
| `ExifInterface` | 1 | none | throw UnsupportedOperationException |
| `ExifInterface` | 1 | none | throw UnsupportedOperationException |
| `saveAttributes` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ExifInterface`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ExifInterface` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
