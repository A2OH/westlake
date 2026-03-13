# SKILL: android.media.RingtoneManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.RingtoneManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.RingtoneManager` |
| **Package** | `android.media` |
| **Total Methods** | 20 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (40%) |
| **Partial/Composite** | 12 (60%) |
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
| `setType` | `void setType(int)` | 8 | near | easy | `eventType` | `eventType: InterruptType` |
| `getRingtonePosition` | `int getRingtonePosition(android.net.Uri)` | 7 | near | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `hasHapticChannels` | `boolean hasHapticChannels(int)` | 6 | near | moderate | `channels` | `channels: AudioChannel` |
| `hasHapticChannels` | `static boolean hasHapticChannels(@NonNull android.net.Uri)` | 6 | near | moderate | `channels` | `channels: AudioChannel` |
| `getDefaultType` | `static int getDefaultType(android.net.Uri)` | 6 | near | moderate | `eventType` | `eventType: InterruptType` |
| `inferStreamType` | `int inferStreamType()` | 6 | near | moderate | `hintType` | `hintType: InterruptHint` |
| `RingtoneManager` | `RingtoneManager(android.app.Activity)` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `RingtoneManager` | `RingtoneManager(android.content.Context)` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getCursor` | `android.database.Cursor getCursor()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `getRingtone` | `android.media.Ringtone getRingtone(int)` | 6 | partial | moderate | `getNextObject` | `getNextObject(callback: AsyncCallback<FileAsset>): void` |
| `getRingtone` | `static android.media.Ringtone getRingtone(android.content.Context, android.net.Uri)` | 6 | partial | moderate | `getNextObject` | `getNextObject(callback: AsyncCallback<FileAsset>): void` |
| `getValidRingtoneUri` | `static android.net.Uri getValidRingtoneUri(android.content.Context)` | 6 | partial | moderate | `getAllSessionDescriptors` | `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `getStopPreviousRingtone` | `boolean getStopPreviousRingtone()` | 6 | partial | moderate | `getHistoricalSessionDescriptors` | `getHistoricalSessionDescriptors(maxSize: number, callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `stopPreviousRingtone` | `void stopPreviousRingtone()` | 5 | partial | moderate | `OH_PreviewOutput_Stop` | `Camera_ErrorCode OH_PreviewOutput_Stop(Camera_PreviewOutput* previewOutput)` |
| `getRingtoneUri` | `android.net.Uri getRingtoneUri(int)` | 5 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `isDefault` | `static boolean isDefault(android.net.Uri)` | 5 | partial | moderate | `isFavorite` | `isFavorite(callback: AsyncCallback<boolean>): void` |
| `getDefaultUri` | `static android.net.Uri getDefaultUri(int)` | 5 | partial | moderate | `getMediaLibrary` | `getMediaLibrary(): MediaLibrary` |
| `setStopPreviousRingtone` | `void setStopPreviousRingtone(boolean)` | 5 | partial | moderate | `OH_PreviewOutput_Stop` | `Camera_ErrorCode OH_PreviewOutput_Stop(Camera_PreviewOutput* previewOutput)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getActualDefaultRingtoneUri` | 5 | partial | Return safe default (null/false/0/empty) |
| `setActualDefaultRingtoneUri` | 4 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 18 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.RingtoneManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.RingtoneManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
