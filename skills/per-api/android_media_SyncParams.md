# SKILL: android.media.SyncParams

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.SyncParams`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.SyncParams` |
| **Package** | `android.media` |
| **Total Methods** | 10 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (20%) |
| **Partial/Composite** | 7 (70%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getFrameRate` | `float getFrameRate()` | 6 | near | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getAudioAdjustMode` | `int getAudioAdjustMode()` | 6 | near | moderate | `getAudioHapticManager` | `getAudioHapticManager(): AudioHapticManager` |
| `setTolerance` | `android.media.SyncParams setTolerance(float)` | 6 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setFrameRate` | `android.media.SyncParams setFrameRate(float)` | 6 | partial | moderate | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `getSyncSource` | `int getSyncSource()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `getTolerance` | `float getTolerance()` | 6 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `setAudioAdjustMode` | `android.media.SyncParams setAudioAdjustMode(int)` | 6 | partial | moderate | `audioAlbum` | `readonly audioAlbum: string` |
| `setSyncSource` | `android.media.SyncParams setSyncSource(int)` | 6 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `SyncParams` | 3 | composite | throw UnsupportedOperationException |
| `allowDefaults` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.SyncParams`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.SyncParams` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
