# SKILL: android.database.ContentObserver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.ContentObserver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.ContentObserver` |
| **Package** | `android.database` |
| **Total Methods** | 9 |
| **Avg Score** | 3.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (77%) |
| **No Mapping** | 2 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onChange` | `void onChange(boolean)` | 5 | partial | moderate | `changePrivilege` | `changePrivilege(sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>): void` |
| `onChange` | `void onChange(boolean, @Nullable android.net.Uri)` | 5 | partial | moderate | `changePrivilege` | `changePrivilege(sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>): void` |
| `onChange` | `void onChange(boolean, @Nullable android.net.Uri, int)` | 5 | partial | moderate | `changePrivilege` | `changePrivilege(sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>): void` |
| `onChange` | `void onChange(boolean, @NonNull java.util.Collection<android.net.Uri>, int)` | 5 | partial | moderate | `changePrivilege` | `changePrivilege(sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `dispatchChange` | 4 | partial | Return safe default (null/false/0/empty) |
| `dispatchChange` | 4 | partial | Return safe default (null/false/0/empty) |
| `dispatchChange` | 4 | partial | Return safe default (null/false/0/empty) |
| `ContentObserver` | 1 | none | Store callback, never fire |
| `deliverSelfNotifications` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.ContentObserver`:


## Quality Gates

Before marking `android.database.ContentObserver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
