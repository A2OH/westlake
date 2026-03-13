# SKILL: android.os.StatFs

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.StatFs`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.StatFs` |
| **Package** | `android.os` |
| **Total Methods** | 9 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (55%) |
| **Partial/Composite** | 3 (33%) |
| **No Mapping** | 1 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getFreeBytes` | `long getFreeBytes()` | 10 | direct | trivial | `getFreeBytes` | `getFreeBytes(path: string, callback: AsyncCallback<number>): void` |
| `getTotalBytes` | `long getTotalBytes()` | 10 | direct | trivial | `getTotalBytes` | `getTotalBytes(path: string, callback: AsyncCallback<number>): void` |
| `StatFs` | `StatFs(String)` | 8 | direct | easy | `stat` | `stat(path: string): Promise<Stat>` |
| `getAvailableBytes` | `long getAvailableBytes()` | 7 | near | moderate | `available` | `available: POST and PUT. The default value is POST.
   *
   * @syscap SystemCapability.MiscServices.Upload
   * @since 3
   * @deprecated since 9
   */
  method?: string` |
| `getFreeBlocksLong` | `long getFreeBlocksLong()` | 6 | near | moderate | `getFreeBytes` | `getFreeBytes(path: string, callback: AsyncCallback<number>): void` |
| `getAvailableBlocksLong` | `long getAvailableBlocksLong()` | 6 | partial | moderate | `available` | `available: POST and PUT. The default value is POST.
   *
   * @syscap SystemCapability.MiscServices.Upload
   * @since 3
   * @deprecated since 9
   */
  method?: string` |
| `getBlockCountLong` | `long getBlockCountLong()` | 6 | partial | moderate | `getCountryCode` | `getCountryCode(callback: AsyncCallback<CountryCode>): void` |
| `getBlockSizeLong` | `long getBlockSizeLong()` | 6 | partial | moderate | `getLastLocation` | `getLastLocation(): Location` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `restat` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.StatFs`:


## Quality Gates

Before marking `android.os.StatFs` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
