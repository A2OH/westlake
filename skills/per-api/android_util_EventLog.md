# SKILL: android.util.EventLog

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.EventLog`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.EventLog` |
| **Package** | `android.util` |
| **Total Methods** | 8 |
| **Avg Score** | 6.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTagName` | `static String getTagName(int)` | 7 | near | easy | `getLocalName` | `getLocalName(): string` |
| `getTagCode` | `static int getTagCode(String)` | 7 | near | moderate | `getState` | `getState(): BluetoothState` |
| `readEvents` | `static void readEvents(int[], java.util.Collection<android.util.EventLog.Event>) throws java.io.IOException` | 7 | near | moderate | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `writeEvent` | `static int writeEvent(int, int)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeEvent` | `static int writeEvent(int, long)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeEvent` | `static int writeEvent(int, float)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeEvent` | `static int writeEvent(int, String)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeEvent` | `static int writeEvent(int, java.lang.Object...)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.EventLog`:


## Quality Gates

Before marking `android.util.EventLog` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
