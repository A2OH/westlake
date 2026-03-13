# SKILL: android.app.WallpaperInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.WallpaperInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.WallpaperInfo` |
| **Package** | `android.app` |
| **Total Methods** | 18 |
| **Avg Score** | 3.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 4 (22%) |
| **Partial/Composite** | 6 (33%) |
| **No Mapping** | 8 (44%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPackageName` | `String getPackageName()` | 7 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getServiceInfo` | `android.content.pm.ServiceInfo getServiceInfo()` | 7 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getServiceName` | `String getServiceName()` | 7 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getComponent` | `android.content.ComponentName getComponent()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `loadContextUri` | `android.net.Uri loadContextUri(android.content.pm.PackageManager) throws android.content.res.Resources.NotFoundException` | 6 | partial | moderate | `getContext` | `getContext(): Context` |
| `WallpaperInfo` | `WallpaperInfo(android.content.Context, android.content.pm.ResolveInfo) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` | 6 | partial | moderate | `getAllFormsInfo` | `getAllFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |
| `getShowMetadataInPreview` | `boolean getShowMetadataInPreview()` | 6 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getSettingsActivity` | `String getSettingsActivity()` | 5 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `loadContextDescription` | 4 | partial | Store callback, never fire |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `loadAuthor` | 1 | none | throw UnsupportedOperationException |
| `loadDescription` | 1 | none | Log warning + no-op |
| `loadIcon` | 1 | none | Store callback, never fire |
| `loadLabel` | 1 | none | throw UnsupportedOperationException |
| `loadThumbnail` | 1 | none | throw UnsupportedOperationException |
| `supportsMultipleDisplays` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.WallpaperInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.WallpaperInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
