# SKILL: android.telephony.PhoneNumberFormattingTextWatcher

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.PhoneNumberFormattingTextWatcher`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.PhoneNumberFormattingTextWatcher` |
| **Package** | `android.telephony` |
| **Total Methods** | 5 |
| **Avg Score** | 3.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 2 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `PhoneNumberFormattingTextWatcher` | 5 | partial | Store callback, never fire |
| `PhoneNumberFormattingTextWatcher` | 5 | partial | Store callback, never fire |
| `onTextChanged` | 3 | composite | Store callback, never fire |
| `afterTextChanged` | 1 | none | throw UnsupportedOperationException |
| `beforeTextChanged` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.PhoneNumberFormattingTextWatcher`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.PhoneNumberFormattingTextWatcher` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
