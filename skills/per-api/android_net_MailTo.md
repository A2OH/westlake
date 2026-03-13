# SKILL: android.net.MailTo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.MailTo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.MailTo` |
| **Package** | `android.net` |
| **Total Methods** | 7 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (85%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getHeaders` | `java.util.Map<java.lang.String,java.lang.String> getHeaders()` | 6 | partial | moderate | `getSharableRegexes` | `getSharableRegexes(type: SharingIfaceType, callback: AsyncCallback<Array<string>>): void` |
| `getTo` | `String getTo()` | 6 | partial | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |
| `getBody` | `String getBody()` | 5 | partial | moderate | `getUidsByPolicy` | `getUidsByPolicy(policy: NetUidPolicy, callback: AsyncCallback<Array<number>>): void` |
| `getCc` | `String getCc()` | 5 | partial | moderate | `getIfaceConfig` | `getIfaceConfig(iface: string, callback: AsyncCallback<InterfaceConfiguration>): void` |
| `getSubject` | `String getSubject()` | 5 | partial | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isMailTo` | 5 | partial | Return safe default (null/false/0/empty) |
| `parse` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.MailTo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.MailTo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
