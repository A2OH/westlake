# SKILL: android.app.VoiceInteractor.PickOptionRequest.Option

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.VoiceInteractor.PickOptionRequest.Option`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.VoiceInteractor.PickOptionRequest.Option` |
| **Package** | `android.app.VoiceInteractor.PickOptionRequest` |
| **Total Methods** | 10 |
| **Avg Score** | 4.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 4 (40%) |
| **Partial/Composite** | 3 (30%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Option` | `VoiceInteractor.PickOptionRequest.Option(CharSequence, int)` | 8 | direct | easy | `option` | `option: MediaAssetOption, callback: AsyncCallback<string>): void` |
| `getIndex` | `int getIndex()` | 8 | near | easy | `getId` | `getId(uri: string): number` |
| `getExtras` | `android.os.Bundle getExtras()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `setExtras` | `void setExtras(android.os.Bundle)` | 6 | near | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getLabel` | `CharSequence getLabel()` | 6 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getSynonymAt` | `CharSequence getSynonymAt(int)` | 5 | partial | moderate | `getContext` | `getContext(): Context` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `addSynonym` | 1 | none | Log warning + no-op |
| `countSynonyms` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.VoiceInteractor.PickOptionRequest.Option`:


## Quality Gates

Before marking `android.app.VoiceInteractor.PickOptionRequest.Option` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
