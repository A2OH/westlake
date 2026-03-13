# SKILL: android.media.JetPlayer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.JetPlayer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.JetPlayer` |
| **Package** | `android.media` |
| **Total Methods** | 19 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (21%) |
| **Partial/Composite** | 13 (68%) |
| **No Mapping** | 2 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `pause` | `boolean pause()` | 7 | near | easy | `pause` | `pause(): void` |
| `play` | `boolean play()` | 7 | near | easy | `play` | `play(): void` |
| `getJetPlayer` | `static android.media.JetPlayer getJetPlayer()` | 7 | near | moderate | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `closeJetFile` | `boolean closeJetFile()` | 6 | partial | moderate | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `setEventListener` | `void setEventListener(android.media.JetPlayer.OnJetEventListener)` | 6 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `setEventListener` | `void setEventListener(android.media.JetPlayer.OnJetEventListener, android.os.Handler)` | 6 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `getMaxTracks` | `static int getMaxTracks()` | 5 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `loadJetFile` | `boolean loadJetFile(String)` | 5 | partial | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `loadJetFile` | `boolean loadJetFile(android.content.res.AssetFileDescriptor)` | 5 | partial | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `triggerClip` | `boolean triggerClip(int)` | 5 | partial | moderate | `OH_Image_ClipRect` | `int32_t OH_Image_ClipRect(const ImageNative* native, struct OhosImageRect* rect)` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clearQueue` | 5 | partial | throw UnsupportedOperationException |
| `setMuteArray` | 4 | partial | Log warning + no-op |
| `queueJetSegmentMuteArray` | 4 | composite | throw UnsupportedOperationException |
| `setMuteFlag` | 4 | composite | Log warning + no-op |
| `setMuteFlags` | 4 | composite | Log warning + no-op |
| `queueJetSegment` | 3 | composite | throw UnsupportedOperationException |
| `clone` | 1 | none | Store callback, never fire |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 11 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.JetPlayer`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.JetPlayer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
