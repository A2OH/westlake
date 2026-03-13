# SKILL: android.media.MicrophoneInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MicrophoneInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MicrophoneInfo` |
| **Package** | `android.media` |
| **Total Methods** | 14 |
| **Avg Score** | 6.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (50%) |
| **Partial/Composite** | 7 (50%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getOrientation` | `android.media.MicrophoneInfo.Coordinate3F getOrientation()` | 9 | direct | easy | `orientation` | `orientation: number` |
| `getPosition` | `android.media.MicrophoneInfo.Coordinate3F getPosition()` | 8 | near | easy | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getType` | `int getType()` | 8 | near | easy | `eventType` | `eventType: InterruptType` |
| `getDescription` | `String getDescription()` | 6 | near | moderate | `getAllSessionDescriptors` | `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `getDirectionality` | `int getDirectionality()` | 6 | near | moderate | `getPublicDirectory` | `getPublicDirectory(type: DirectoryType, callback: AsyncCallback<string>): void` |
| `getGroup` | `int getGroup()` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getLocation` | `int getLocation()` | 6 | near | moderate | `getAllObject` | `getAllObject(callback: AsyncCallback<Array<FileAsset>>): void` |
| `getId` | `int getId()` | 6 | partial | moderate | `id` | `readonly id: number` |
| `getMaxSpl` | `float getMaxSpl()` | 6 | partial | moderate | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getMinSpl` | `float getMinSpl()` | 6 | partial | moderate | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getIndexInTheGroup` | `int getIndexInTheGroup()` | 5 | partial | moderate | `getNextObject` | `getNextObject(callback: AsyncCallback<FileAsset>): void` |
| `getSensitivity` | `float getSensitivity()` | 5 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getFrequencyResponse` | 5 | partial | Return safe default (null/false/0/empty) |
| `getChannelMapping` | 5 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MicrophoneInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MicrophoneInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
