# SKILL: android.media.MediaRoute2ProviderService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRoute2ProviderService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRoute2ProviderService` |
| **Package** | `android.media` |
| **Total Methods** | 14 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (21%) |
| **Partial/Composite** | 11 (78%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCreateSession` | `abstract void onCreateSession(long, @NonNull String, @NonNull String, @Nullable android.os.Bundle)` | 9 | direct | easy | `createAVSession` | `createAVSession(context: Context, tag: string, type: AVSessionType, callback: AsyncCallback<AVSession>): void` |
| `onReleaseSession` | `abstract void onReleaseSession(long, @NonNull String)` | 7 | near | easy | `createAVSession` | `createAVSession(context: Context, tag: string, type: AVSessionType, callback: AsyncCallback<AVSession>): void` |
| `notifySessionReleased` | `final void notifySessionReleased(@NonNull String)` | 7 | near | moderate | `OH_CaptureSession_Release` | `Camera_ErrorCode OH_CaptureSession_Release(Camera_CaptureSession* session)` |
| `notifySessionUpdated` | `final void notifySessionUpdated(@NonNull android.media.RoutingSessionInfo)` | 6 | partial | moderate | `sessionType` | `readonly sessionType: AVSessionType` |
| `notifySessionCreated` | `final void notifySessionCreated(long, @NonNull android.media.RoutingSessionInfo)` | 6 | partial | moderate | `OH_CaptureSession_Release` | `Camera_ErrorCode OH_CaptureSession_Release(Camera_CaptureSession* session)` |
| `onSetSessionVolume` | `abstract void onSetSessionVolume(long, @NonNull String, int)` | 6 | partial | moderate | `sessionType` | `readonly sessionType: AVSessionType` |
| `onSetRouteVolume` | `abstract void onSetRouteVolume(long, @NonNull String, int)` | 5 | partial | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `onSelectRoute` | `abstract void onSelectRoute(long, @NonNull String, @NonNull String)` | 5 | partial | moderate | `OH_AVPlayer_SelectBitRate` | `OH_AVErrCode OH_AVPlayer_SelectBitRate(OH_AVPlayer *player, uint32_t bitRate)` |
| `onDeselectRoute` | `abstract void onDeselectRoute(long, @NonNull String, @NonNull String)` | 5 | partial | moderate | `OH_AVPlayer_DeselectTrack` | `OH_AVErrCode OH_AVPlayer_DeselectTrack(OH_AVPlayer *player, int32_t index)` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `MediaRoute2ProviderService` | 4 | partial | throw UnsupportedOperationException |
| `onTransferToRoute` | 4 | partial | Store callback, never fire |
| `onDiscoveryPreferenceChanged` | 4 | composite | Return safe default (null/false/0/empty) |
| `notifyRequestFailed` | 4 | composite | throw UnsupportedOperationException |
| `notifyRoutes` | 4 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRoute2ProviderService`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaRoute2ProviderService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
