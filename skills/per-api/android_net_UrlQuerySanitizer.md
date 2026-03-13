# SKILL: android.net.UrlQuerySanitizer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.UrlQuerySanitizer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.UrlQuerySanitizer` |
| **Package** | `android.net` |
| **Total Methods** | 33 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (18%) |
| **Partial/Composite** | 20 (60%) |
| **No Mapping** | 7 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 14 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 8 | direct | easy | `clear` | `clear(): void` |
| `getAllIllegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllIllegal()` | 7 | near | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `getValue` | `String getValue(String)` | 7 | near | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `getParameterList` | `java.util.List<android.net.UrlQuerySanitizer.ParameterValuePair> getParameterList()` | 6 | near | moderate | `getPowerSaveTrustlist` | `getPowerSaveTrustlist(callback: AsyncCallback<Array<number>>): void` |
| `getAllButNulLegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllButNulLegal()` | 6 | near | moderate | `getAllRxBytes` | `getAllRxBytes(callback: AsyncCallback<number>): void` |
| `getAmpLegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getAmpLegal()` | 6 | near | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |
| `getAllButWhitespaceLegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getAllButWhitespaceLegal()` | 6 | partial | moderate | `getAllActiveIfaces` | `getAllActiveIfaces(callback: AsyncCallback<Array<string>>): void` |
| `getParameterSet` | `java.util.Set<java.lang.String> getParameterSet()` | 6 | partial | moderate | `getAllRxBytes` | `getAllRxBytes(callback: AsyncCallback<number>): void` |
| `getUrlLegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getUrlLegal()` | 6 | partial | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `isHexDigit` | `boolean isHexDigit(char)` | 5 | partial | moderate | `isSharing` | `isSharing(callback: AsyncCallback<boolean>): void` |
| `getSpaceLegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getSpaceLegal()` | 5 | partial | moderate | `getIfaceConfig` | `getIfaceConfig(iface: string, callback: AsyncCallback<InterfaceConfiguration>): void` |
| `getValueSanitizer` | `android.net.UrlQuerySanitizer.ValueSanitizer getValueSanitizer(String)` | 5 | partial | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `getAmpAndSpaceLegal` | `static final android.net.UrlQuerySanitizer.ValueSanitizer getAmpAndSpaceLegal()` | 5 | partial | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |
| `getEffectiveValueSanitizer` | `android.net.UrlQuerySanitizer.ValueSanitizer getEffectiveValueSanitizer(String)` | 5 | partial | moderate | `getNetCapabilities` | `getNetCapabilities(netHandle: NetHandle, callback: AsyncCallback<NetCapabilities>): void` |

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `registerParameter` | 5 | partial | Return safe default (null/false/0/empty) |
| `getAllowUnregisteredParamaters` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasParameter` | 5 | partial | Return safe default (null/false/0/empty) |
| `registerParameters` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPreferFirstRepeatedParameter` | 5 | partial | Return safe default (null/false/0/empty) |
| `getUrlAndSpaceLegal` | 5 | partial | Return safe default (null/false/0/empty) |
| `getAllButNulAndAngleBracketsLegal` | 5 | partial | Return safe default (null/false/0/empty) |
| `getUnregisteredParameterValueSanitizer` | 5 | partial | Return safe default (null/false/0/empty) |
| `setAllowUnregisteredParamaters` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPreferFirstRepeatedParameter` | 4 | composite | Log warning + no-op |
| `addSanitizedEntry` | 4 | composite | Log warning + no-op |
| `setUnregisteredParameterValueSanitizer` | 4 | composite | Return safe default (null/false/0/empty) |
| `UrlQuerySanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `UrlQuerySanitizer` | 1 | none | Return safe default (null/false/0/empty) |
| `decodeHexDigit` | 1 | none | throw UnsupportedOperationException |
| `parseEntry` | 1 | none | throw UnsupportedOperationException |
| `parseQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `parseUrl` | 1 | none | throw UnsupportedOperationException |
| `unescape` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 14 methods that have score >= 5
2. Stub 19 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.UrlQuerySanitizer`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.UrlQuerySanitizer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 33 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 14 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
