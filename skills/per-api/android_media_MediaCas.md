# SKILL: android.media.MediaCas

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCas`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCas` |
| **Package** | `android.media` |
| **Total Methods** | 14 |
| **Avg Score** | 4.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 6 (42%) |
| **Partial/Composite** | 4 (28%) |
| **No Mapping** | 4 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `isSystemIdSupported` | `static boolean isSystemIdSupported(int)` | 8 | near | easy | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `openSession` | `android.media.MediaCas.Session openSession() throws android.media.MediaCasException` | 7 | near | easy | `sessionId` | `readonly sessionId: string` |
| `sendEvent` | `void sendEvent(int, int, @Nullable byte[]) throws android.media.MediaCasException` | 6 | near | moderate | `sendSystemAVKeyEvent` | `sendSystemAVKeyEvent(event: KeyEvent, callback: AsyncCallback<void>): void` |
| `MediaCas` | `MediaCas(int) throws android.media.MediaCasException.UnsupportedCasException` | 6 | near | moderate | `storeMediaAsset` | `storeMediaAsset(option: MediaAssetOption, callback: AsyncCallback<string>): void` |
| `MediaCas` | `MediaCas(@NonNull android.content.Context, int, @Nullable String, int) throws android.media.MediaCasException.UnsupportedCasException` | 6 | near | moderate | `storeMediaAsset` | `storeMediaAsset(option: MediaAssetOption, callback: AsyncCallback<string>): void` |
| `setEventListener` | `void setEventListener(@Nullable android.media.MediaCas.EventListener, @Nullable android.os.Handler)` | 6 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `setPrivateData` | `void setPrivateData(@NonNull byte[]) throws android.media.MediaCasException` | 5 | partial | moderate | `OH_ImageSource_UpdateData` | `int32_t OH_ImageSource_UpdateData(const ImageSourceNative* native, struct OhosImageSourceUpdateData* data)` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `processEmm` | 3 | composite | throw UnsupportedOperationException |
| `processEmm` | 3 | composite | throw UnsupportedOperationException |
| `enumeratePlugins` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `provision` | 1 | none | Return safe default (null/false/0/empty) |
| `refreshEntitlements` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCas`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaCas` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
