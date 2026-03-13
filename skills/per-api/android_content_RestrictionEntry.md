# SKILL: android.content.RestrictionEntry

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.RestrictionEntry`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.RestrictionEntry` |
| **Package** | `android.content` |
| **Total Methods** | 33 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (39%) |
| **Partial/Composite** | 19 (57%) |
| **No Mapping** | 1 (3%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 27 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setType` | `void setType(int)` | 8 | direct | easy | `userType` | `userType?: UserType` |
| `getDescription` | `String getDescription()` | 8 | near | easy | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `getType` | `int getType()` | 7 | near | easy | `type` | `type: ValueType` |
| `getKey` | `String getKey()` | 7 | near | easy | `getEntry` | `getEntry(): Entry` |
| `getRestrictions` | `android.content.RestrictionEntry[] getRestrictions()` | 7 | near | moderate | `getPosition` | `getPosition(): number` |
| `getChoiceEntries` | `String[] getChoiceEntries()` | 7 | near | moderate | `deleteEntries` | `deleteEntries: Entry[]` |
| `setChoiceEntries` | `void setChoiceEntries(String[])` | 7 | near | moderate | `insertEntries` | `insertEntries: Entry[]` |
| `setChoiceEntries` | `void setChoiceEntries(android.content.Context, @ArrayRes int)` | 7 | near | moderate | `insertEntries` | `insertEntries: Entry[]` |
| `setRestrictions` | `void setRestrictions(android.content.RestrictionEntry[])` | 7 | near | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getIntValue` | `int getIntValue()` | 6 | near | moderate | `value` | `value: Value` |
| `setIntValue` | `void setIntValue(int)` | 6 | near | moderate | `value` | `value: Value` |
| `getTitle` | `String getTitle()` | 6 | near | moderate | `getId` | `getId(uri: string): number` |
| `createBundleEntry` | `static android.content.RestrictionEntry createBundleEntry(String, android.content.RestrictionEntry[])` | 6 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `RestrictionEntry` | `RestrictionEntry(int, String)` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `RestrictionEntry` | `RestrictionEntry(String, String)` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `RestrictionEntry` | `RestrictionEntry(String, boolean)` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `RestrictionEntry` | `RestrictionEntry(String, String[])` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `RestrictionEntry` | `RestrictionEntry(String, int)` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `RestrictionEntry` | `RestrictionEntry(android.os.Parcel)` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `getSelectedString` | `String getSelectedString()` | 6 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `createBundleArrayEntry` | `static android.content.RestrictionEntry createBundleArrayEntry(String, android.content.RestrictionEntry[])` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `setDescription` | `void setDescription(String)` | 6 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getSelectedState` | `boolean getSelectedState()` | 5 | partial | moderate | `getContext` | `getContext(): Context` |
| `getAllSelectedStrings` | `String[] getAllSelectedStrings()` | 5 | partial | moderate | `getExtensionRunningInfos` | `getExtensionRunningInfos(upperLimit: number): Promise<Array<ExtensionRunningInfo>>` |
| `setSelectedState` | `void setSelectedState(boolean)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getChoiceValues` | `String[] getChoiceValues()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `setSelectedString` | `void setSelectedString(String)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setAllSelectedStrings` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `setChoiceValues` | 4 | partial | Log warning + no-op |
| `setChoiceValues` | 4 | partial | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 27 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.RestrictionEntry`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.RestrictionEntry` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 33 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 27 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
