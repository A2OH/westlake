# SKILL: android.provider.Settings.Secure

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.Settings.Secure`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.Settings.Secure` |
| **Package** | `android.provider.Settings` |
| **Total Methods** | 13 |
| **Avg Score** | 5.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 9 (69%) |
| **Partial/Composite** | 4 (30%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Secure` | `Settings.Secure()` | 8 | direct | easy | `secure` | `secure: boolean` |
| `getString` | `static String getString(android.content.ContentResolver, String)` | 7 | near | easy | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getInt` | `static int getInt(android.content.ContentResolver, String, int)` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getInt` | `static int getInt(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getLong` | `static long getLong(android.content.ContentResolver, String, long)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getLong` | `static long getLong(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `static float getFloat(android.content.ContentResolver, String, float)` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `static float getFloat(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getUriFor` | `static android.net.Uri getUriFor(String)` | 6 | near | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `putFloat` | 4 | composite | Log warning + no-op |
| `putString` | 4 | composite | Log warning + no-op |
| `putInt` | 3 | composite | Log warning + no-op |
| `putLong` | 2 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.Settings.Secure`:


## Quality Gates

Before marking `android.provider.Settings.Secure` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
