# SKILL: android.media.MediaSyncEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaSyncEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaSyncEvent` |
| **Package** | `android.media` |
| **Total Methods** | 4 |
| **Avg Score** | 7.2 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 4 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getType` | `int getType()` | 8 | near | easy | `eventType` | `eventType: InterruptType` |
| `createEvent` | `static android.media.MediaSyncEvent createEvent(int) throws java.lang.IllegalArgumentException` | 7 | near | easy | `createAsset` | `createAsset(mediaType: MediaType, displayName: string, relativePath: string, callback: AsyncCallback<FileAsset>): void` |
| `getAudioSessionId` | `int getAudioSessionId()` | 7 | near | moderate | `sessionId` | `readonly sessionId: string` |
| `setAudioSessionId` | `android.media.MediaSyncEvent setAudioSessionId(int) throws java.lang.IllegalArgumentException` | 7 | near | moderate | `sessionId` | `readonly sessionId: string` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaSyncEvent`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaSyncEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
