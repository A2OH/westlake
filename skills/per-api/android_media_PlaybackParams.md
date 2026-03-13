# SKILL: android.media.PlaybackParams

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.PlaybackParams`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.PlaybackParams` |
| **Package** | `android.media` |
| **Total Methods** | 10 |
| **Avg Score** | 4.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 5 (50%) |
| **No Mapping** | 2 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getSpeed` | `float getSpeed()` | 6 | near | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getAudioFallbackMode` | `int getAudioFallbackMode()` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `setAudioFallbackMode` | `android.media.PlaybackParams setAudioFallbackMode(int)` | 6 | near | moderate | `audioAlbum` | `readonly audioAlbum: string` |
| `getPitch` | `float getPitch()` | 6 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `PlaybackParams` | `PlaybackParams()` | 6 | partial | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `setSpeed` | `android.media.PlaybackParams setSpeed(float)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setPitch` | `android.media.PlaybackParams setPitch(float)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `allowDefaults` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.PlaybackParams`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.PlaybackParams` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
