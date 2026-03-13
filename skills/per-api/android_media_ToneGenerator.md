# SKILL: android.media.ToneGenerator

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ToneGenerator`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ToneGenerator` |
| **Package** | `android.media` |
| **Total Methods** | 7 |
| **Avg Score** | 6.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (85%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `getAudioSessionId` | `final int getAudioSessionId()` | 7 | near | moderate | `sessionId` | `readonly sessionId: string` |
| `startTone` | `boolean startTone(int)` | 7 | near | moderate | `startCasting` | `startCasting(session: SessionToken, device: OutputDeviceInfo, callback: AsyncCallback<void>): void` |
| `startTone` | `boolean startTone(int, int)` | 7 | near | moderate | `startCasting` | `startCasting(session: SessionToken, device: OutputDeviceInfo, callback: AsyncCallback<void>): void` |
| `stopTone` | `void stopTone()` | 6 | near | moderate | `stopCasting` | `stopCasting(session: SessionToken, callback: AsyncCallback<void>): void` |
| `ToneGenerator` | `ToneGenerator(int, int)` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ToneGenerator`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ToneGenerator` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
