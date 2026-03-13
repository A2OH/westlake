# SKILL: android.content.ClipData

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ClipData`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ClipData` |
| **Package** | `android.content` |
| **Total Methods** | 15 |
| **Avg Score** | 4.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 7 (46%) |
| **Partial/Composite** | 2 (13%) |
| **No Mapping** | 6 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getItemCount` | `int getItemCount()` | 8 | direct | easy | `getCount` | `getCount(): number` |
| `getDescription` | `android.content.ClipDescription getDescription()` | 8 | near | easy | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `ClipData` | `ClipData(CharSequence, String[], android.content.ClipData.Item)` | 7 | near | moderate | `data` | `data: string | ArrayBuffer` |
| `ClipData` | `ClipData(android.content.ClipDescription, android.content.ClipData.Item)` | 7 | near | moderate | `data` | `data: string | ArrayBuffer` |
| `ClipData` | `ClipData(android.content.ClipData)` | 7 | near | moderate | `data` | `data: string | ArrayBuffer` |
| `newUri` | `static android.content.ClipData newUri(android.content.ContentResolver, CharSequence, android.net.Uri)` | 7 | near | moderate | `uri` | `uri: string` |
| `getItemAt` | `android.content.ClipData.Item getItemAt(int)` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `newRawUri` | `static android.content.ClipData newRawUri(CharSequence, android.net.Uri)` | 5 | partial | moderate | `uri` | `uri: string` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `addItem` | 1 | none | Log warning + no-op |
| `addItem` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `newHtmlText` | 1 | none | throw UnsupportedOperationException |
| `newIntent` | 1 | none | throw UnsupportedOperationException |
| `newPlainText` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ClipData`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ClipData` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
