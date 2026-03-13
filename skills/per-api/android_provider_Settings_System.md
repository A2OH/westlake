# SKILL: android.provider.Settings.System

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.Settings.System`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.Settings.System` |
| **Package** | `android.provider.Settings` |
| **Total Methods** | 16 |
| **Avg Score** | 5.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 10 (62%) |
| **Partial/Composite** | 5 (31%) |
| **No Mapping** | 1 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `System` | `Settings.System()` | 8 | direct | easy | `system` | `int system(const char *)` |
| `getString` | `static String getString(android.content.ContentResolver, String)` | 7 | near | easy | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getInt` | `static int getInt(android.content.ContentResolver, String, int)` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getInt` | `static int getInt(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getConfiguration` | `static void getConfiguration(android.content.ContentResolver, android.content.res.Configuration)` | 7 | near | moderate | `getPosition` | `getPosition(): number` |
| `getLong` | `static long getLong(android.content.ContentResolver, String, long)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getLong` | `static long getLong(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `static float getFloat(android.content.ContentResolver, String, float)` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `static float getFloat(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getUriFor` | `static android.net.Uri getUriFor(String)` | 6 | near | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `putFloat` | 4 | composite | Log warning + no-op |
| `putString` | 4 | composite | Log warning + no-op |
| `putInt` | 3 | composite | Log warning + no-op |
| `putConfiguration` | 3 | composite | Log warning + no-op |
| `putLong` | 2 | composite | Log warning + no-op |
| `canWrite` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.Settings.System`:


## Quality Gates

Before marking `android.provider.Settings.System` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
