# SKILL: android.media.VolumeShaper.Configuration

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.VolumeShaper.Configuration`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.VolumeShaper.Configuration` |
| **Package** | `android.media.VolumeShaper` |
| **Total Methods** | 7 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (42%) |
| **Partial/Composite** | 3 (42%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDuration` | `long getDuration()` | 8 | direct | easy | `duration` | `readonly duration: number` |
| `getVolumes` | `float[] getVolumes()` | 7 | near | easy | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getTimes` | `float[] getTimes()` | 6 | near | moderate | `getActivePeers` | `getActivePeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getInterpolatorType` | `int getInterpolatorType()` | 5 | partial | moderate | `hintType` | `hintType: InterruptHint` |
| `getMaximumCurvePoints` | `static int getMaximumCurvePoints()` | 5 | partial | moderate | `getActivePeers` | `getActivePeers(callback: AsyncCallback<Array<PeerInfo>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.VolumeShaper.Configuration`:


## Quality Gates

Before marking `android.media.VolumeShaper.Configuration` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
