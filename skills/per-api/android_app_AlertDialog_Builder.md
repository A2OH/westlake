# SKILL: android.app.AlertDialog.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AlertDialog.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AlertDialog.Builder` |
| **Package** | `android.app.AlertDialog` |
| **Total Methods** | 37 |
| **Avg Score** | 4.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (13%) |
| **Partial/Composite** | 30 (81%) |
| **No Mapping** | 2 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getContext` | `android.content.Context getContext()` | 10 | direct | trivial | `getContext` | `getContext(): Context` |
| `create` | `android.app.AlertDialog create()` | 8 | direct | easy | `create` | `create(config: PiPConfiguration): Promise<PiPController>` |
| `show` | `android.app.AlertDialog show()` | 8 | direct | easy | `show` | `show(uri: string, type: string): Promise<void>` |
| `setOnDismissListener` | `android.app.AlertDialog.Builder setOnDismissListener(android.content.DialogInterface.OnDismissListener)` | 7 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `setOnKeyListener` | `android.app.AlertDialog.Builder setOnKeyListener(android.content.DialogInterface.OnKeyListener)` | 6 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `setOnCancelListener` | `android.app.AlertDialog.Builder setOnCancelListener(android.content.DialogInterface.OnCancelListener)` | 6 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `setCursor` | `android.app.AlertDialog.Builder setCursor(android.database.Cursor, android.content.DialogInterface.OnClickListener, String)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setAdapter` | `android.app.AlertDialog.Builder setAdapter(android.widget.ListAdapter, android.content.DialogInterface.OnClickListener)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setCustomTitle` | `android.app.AlertDialog.Builder setCustomTitle(android.view.View)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setMessage` | `android.app.AlertDialog.Builder setMessage(@StringRes int)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setMessage` | `android.app.AlertDialog.Builder setMessage(CharSequence)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setOnItemSelectedListener` | `android.app.AlertDialog.Builder setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)` | 5 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |

## Stub APIs (score < 5): 25 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setIconAttribute` | 5 | partial | Log warning + no-op |
| `setView` | 5 | partial | Log warning + no-op |
| `setView` | 5 | partial | Log warning + no-op |
| `setNeutralButton` | 5 | partial | Log warning + no-op |
| `setNeutralButton` | 5 | partial | Log warning + no-op |
| `setItems` | 5 | partial | Log warning + no-op |
| `setItems` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `setPositiveButton` | 5 | partial | Log warning + no-op |
| `setPositiveButton` | 5 | partial | Log warning + no-op |
| `setIcon` | 4 | partial | Log warning + no-op |
| `setIcon` | 4 | partial | Log warning + no-op |
| `setNegativeButton` | 4 | partial | Log warning + no-op |
| `setNegativeButton` | 4 | partial | Log warning + no-op |
| `setMultiChoiceItems` | 4 | partial | Log warning + no-op |
| `setMultiChoiceItems` | 4 | partial | Log warning + no-op |
| `setMultiChoiceItems` | 4 | partial | Log warning + no-op |
| `setSingleChoiceItems` | 4 | partial | Log warning + no-op |
| `setSingleChoiceItems` | 4 | partial | Log warning + no-op |
| `setSingleChoiceItems` | 4 | partial | Log warning + no-op |
| `setSingleChoiceItems` | 4 | partial | Log warning + no-op |
| `setCancelable` | 4 | composite | Return safe default (null/false/0/empty) |
| `Builder` | 1 | none | throw UnsupportedOperationException |
| `Builder` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 25 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AlertDialog.Builder`:

- `android.content.Context` (already shimmed)
- `android.app.Dialog` (not yet shimmed)

## Quality Gates

Before marking `android.app.AlertDialog.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 37 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
