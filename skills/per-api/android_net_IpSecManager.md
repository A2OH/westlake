# SKILL: android.net.IpSecManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.IpSecManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.IpSecManager` |
| **Package** | `android.net` |
| **Total Methods** | 6 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `removeTransportModeTransforms` | 5 | partial | Log warning + no-op |
| `removeTransportModeTransforms` | 5 | partial | Log warning + no-op |
| `removeTransportModeTransforms` | 5 | partial | Log warning + no-op |
| `applyTransportModeTransform` | 4 | partial | throw UnsupportedOperationException |
| `applyTransportModeTransform` | 4 | partial | throw UnsupportedOperationException |
| `applyTransportModeTransform` | 4 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.IpSecManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.IpSecManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
