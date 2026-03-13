# SKILL: android.media.AudioDeviceInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioDeviceInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioDeviceInfo` |
| **Package** | `android.media` |
| **Total Methods** | 5 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (20%) |
| **Partial/Composite** | 4 (80%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getType` | `int getType()` | 8 | near | easy | `eventType` | `eventType: InterruptType` |
| `getId` | `int getId()` | 6 | partial | moderate | `id` | `readonly id: number` |
| `isSink` | `boolean isSink()` | 6 | partial | moderate | `isOnline` | `readonly isOnline: boolean` |
| `isSource` | `boolean isSource()` | 6 | partial | moderate | `isFavorite` | `isFavorite(callback: AsyncCallback<boolean>): void` |
| `getProductName` | `CharSequence getProductName()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioDeviceInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioDeviceInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
