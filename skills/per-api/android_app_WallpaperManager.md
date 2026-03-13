# SKILL: android.app.WallpaperManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.WallpaperManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.WallpaperManager` |
| **Package** | `android.app` |
| **Total Methods** | 23 |
| **Avg Score** | 4.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (13%) |
| **Partial/Composite** | 17 (73%) |
| **No Mapping** | 3 (13%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getWallpaperInfo` | `android.app.WallpaperInfo getWallpaperInfo()` | 7 | near | easy | `getAllFormsInfo` | `getAllFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |
| `getWallpaperId` | `int getWallpaperId(int)` | 7 | near | moderate | `getId` | `getId(uri: string): number` |
| `getInstance` | `static android.app.WallpaperManager getInstance(android.content.Context)` | 6 | near | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getBuiltInDrawable` | `android.graphics.drawable.Drawable getBuiltInDrawable()` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getBuiltInDrawable` | `android.graphics.drawable.Drawable getBuiltInDrawable(int)` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getBuiltInDrawable` | `android.graphics.drawable.Drawable getBuiltInDrawable(int, int, boolean, float, float)` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getBuiltInDrawable` | `android.graphics.drawable.Drawable getBuiltInDrawable(int, int, boolean, float, float, int)` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getDrawable` | `android.graphics.drawable.Drawable getDrawable()` | 6 | partial | moderate | `getWant` | `getWant(callback: AsyncCallback<Want>): void` |
| `clearWallpaperOffsets` | `void clearWallpaperOffsets(android.os.IBinder)` | 5 | partial | moderate | `clearAllMissions` | `clearAllMissions(callback: AsyncCallback<void>): void` |
| `isWallpaperSupported` | `boolean isWallpaperSupported()` | 5 | partial | moderate | `isRequestPublishFormSupported` | `isRequestPublishFormSupported(callback: AsyncCallback<boolean>): void` |
| `getCropAndSetWallpaperIntent` | `android.content.Intent getCropAndSetWallpaperIntent(android.net.Uri)` | 5 | partial | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `removeOnColorsChangedListener` | `void removeOnColorsChangedListener(@NonNull android.app.WallpaperManager.OnColorsChangedListener)` | 5 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDesiredMinimumHeight` | 5 | partial | Return safe default (null/false/0/empty) |
| `getDesiredMinimumWidth` | 5 | partial | Return safe default (null/false/0/empty) |
| `setWallpaperOffsetSteps` | 4 | partial | Log warning + no-op |
| `addOnColorsChangedListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `isSetWallpaperAllowed` | 4 | partial | Return safe default (null/false/0/empty) |
| `setWallpaperOffsets` | 4 | composite | Log warning + no-op |
| `sendWallpaperCommand` | 4 | composite | throw UnsupportedOperationException |
| `hasResourceWallpaper` | 3 | composite | Return safe default (null/false/0/empty) |
| `forgetLoadedWallpaper` | 1 | none | Return safe default (null/false/0/empty) |
| `peekDrawable` | 1 | none | throw UnsupportedOperationException |
| `suggestDesiredDimensions` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.WallpaperManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.WallpaperManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
