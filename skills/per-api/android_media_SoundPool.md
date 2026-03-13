# SKILL: android.media.SoundPool

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.SoundPool`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.SoundPool` |
| **Package** | `android.media` |
| **Total Methods** | 18 |
| **Avg Score** | 4.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 6 (33%) |
| **Partial/Composite** | 5 (27%) |
| **No Mapping** | 7 (38%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `final void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `resume` | `final void resume(int)` | 8 | direct | easy | `resume` | `resume(): void` |
| `pause` | `final void pause(int)` | 7 | near | easy | `pause` | `pause(): void` |
| `play` | `final int play(int, float, float, int, int, float)` | 7 | near | easy | `play` | `play(): void` |
| `stop` | `final void stop(int)` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `setVolume` | `final void setVolume(int, float, float)` | 6 | near | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `setRate` | `final void setRate(int, float)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `autoPause` | `final void autoPause()` | 5 | partial | moderate | `OH_AudioCapturer_Pause` | `OH_AudioStream_Result OH_AudioCapturer_Pause(OH_AudioCapturer* capturer)` |
| `setLoop` | `final void setLoop(int, int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setPriority` | `final void setPriority(int, int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setOnLoadCompleteListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `autoResume` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `load` | 1 | none | throw UnsupportedOperationException |
| `load` | 1 | none | throw UnsupportedOperationException |
| `load` | 1 | none | throw UnsupportedOperationException |
| `load` | 1 | none | throw UnsupportedOperationException |
| `unload` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.SoundPool`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.SoundPool` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
