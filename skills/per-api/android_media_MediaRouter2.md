# SKILL: android.media.MediaRouter2

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter2`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter2` |
| **Package** | `android.media` |
| **Total Methods** | 11 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (63%) |
| **Partial/Composite** | 4 (36%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `stop` | `void stop()` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `registerControllerCallback` | `void registerControllerCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaRouter2.ControllerCallback)` | 7 | near | moderate | `createController` | `createController(sessionId: string, callback: AsyncCallback<AVSessionController>): void` |
| `unregisterRouteCallback` | `void unregisterRouteCallback(@NonNull android.media.MediaRouter2.RouteCallback)` | 6 | near | moderate | `OH_CameraInput_UnregisterCallback` | `Camera_ErrorCode OH_CameraInput_UnregisterCallback(Camera_Input* cameraInput, CameraInput_Callbacks* callback)` |
| `unregisterControllerCallback` | `void unregisterControllerCallback(@NonNull android.media.MediaRouter2.ControllerCallback)` | 6 | near | moderate | `createController` | `createController(sessionId: string, callback: AsyncCallback<AVSessionController>): void` |
| `registerRouteCallback` | `void registerRouteCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaRouter2.RouteCallback, @NonNull android.media.RouteDiscoveryPreference)` | 6 | near | moderate | `OH_AudioCodec_RegisterCallback` | `OH_AVErrCode OH_AudioCodec_RegisterCallback(OH_AVCodec *codec, OH_AVCodecCallback callback, void *userData)` |
| `ControllerCallback` | `MediaRouter2.ControllerCallback()` | 6 | near | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `unregisterTransferCallback` | `void unregisterTransferCallback(@NonNull android.media.MediaRouter2.TransferCallback)` | 6 | near | moderate | `OH_CameraInput_UnregisterCallback` | `Camera_ErrorCode OH_CameraInput_UnregisterCallback(Camera_Input* cameraInput, CameraInput_Callbacks* callback)` |
| `registerTransferCallback` | `void registerTransferCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.MediaRouter2.TransferCallback)` | 6 | partial | moderate | `OH_AudioCodec_RegisterCallback` | `OH_AVErrCode OH_AudioCodec_RegisterCallback(OH_AVCodec *codec, OH_AVCodecCallback callback, void *userData)` |
| `onControllerUpdated` | `void onControllerUpdated(@NonNull android.media.MediaRouter2.RoutingController)` | 6 | partial | moderate | `createController` | `createController(sessionId: string, callback: AsyncCallback<AVSessionController>): void` |
| `setOnGetControllerHintsListener` | `void setOnGetControllerHintsListener(@Nullable android.media.MediaRouter2.OnGetControllerHintsListener)` | 6 | partial | moderate | `createController` | `createController(sessionId: string, callback: AsyncCallback<AVSessionController>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `transferTo` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter2`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaRouter2` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
