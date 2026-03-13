# SKILL: android.media.MediaDrm

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaDrm`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaDrm` |
| **Package** | `android.media` |
| **Total Methods** | 34 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (20%) |
| **Partial/Composite** | 27 (79%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `closeSession` | `void closeSession(@NonNull byte[])` | 7 | near | moderate | `sessionId` | `readonly sessionId: string` |
| `getCryptoSession` | `android.media.MediaDrm.CryptoSession getCryptoSession(@NonNull byte[], @NonNull String, @NonNull String)` | 6 | near | moderate | `createAVSession` | `createAVSession(context: Context, tag: string, type: AVSessionType, callback: AsyncCallback<AVSession>): void` |
| `isCryptoSchemeSupported` | `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `isCryptoSchemeSupported` | `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID, @NonNull String)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `isCryptoSchemeSupported` | `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID, @NonNull String, @android.media.MediaDrm.SecurityLevel int)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `getMaxSessionCount` | `int getMaxSessionCount()` | 6 | near | moderate | `getAllSessionDescriptors` | `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `getOpenSessionCount` | `int getOpenSessionCount()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `MediaDrm` | `MediaDrm(@NonNull java.util.UUID) throws android.media.UnsupportedSchemeException` | 6 | partial | moderate | `mediaType` | `readonly mediaType: MediaType` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `releaseSecureStops` | `void releaseSecureStops(@NonNull byte[])` | 6 | partial | moderate | `release` | `release(callback: AsyncCallback<void>): void` |
| `setOnEventListener` | `void setOnEventListener(@Nullable android.media.MediaDrm.OnEventListener)` | 5 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `setOnEventListener` | `void setOnEventListener(@Nullable android.media.MediaDrm.OnEventListener, @Nullable android.os.Handler)` | 5 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `setOnEventListener` | `void setOnEventListener(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaDrm.OnEventListener)` | 5 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `getOfflineLicenseState` | `int getOfflineLicenseState(@NonNull byte[])` | 5 | partial | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `getMaxSecurityLevel` | `static int getMaxSecurityLevel()` | 5 | partial | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `removeAllSecureStops` | `void removeAllSecureStops()` | 5 | partial | moderate | `getAllSessionDescriptors` | `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `removeSecureStop` | `void removeSecureStop(@NonNull byte[])` | 5 | partial | moderate | `OH_VideoDecoder_Stop` | `OH_AVErrCode OH_VideoDecoder_Stop(OH_AVCodec *codec)` |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clearOnEventListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `clearOnSessionLostStateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setOnSessionLostStateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setOnSessionLostStateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setPropertyString` | 5 | partial | Log warning + no-op |
| `setPropertyByteArray` | 5 | partial | Log warning + no-op |
| `provideProvisionResponse` | 5 | partial | Return safe default (null/false/0/empty) |
| `removeOfflineLicense` | 5 | partial | Log warning + no-op |
| `setOnExpirationUpdateListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setOnExpirationUpdateListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `clearOnKeyStatusChangeListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `restoreKeys` | 4 | partial | throw UnsupportedOperationException |
| `setOnKeyStatusChangeListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setOnKeyStatusChangeListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `clearOnExpirationUpdateListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeKeys` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 18 methods that have score >= 5
2. Stub 16 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaDrm`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaDrm` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 34 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
