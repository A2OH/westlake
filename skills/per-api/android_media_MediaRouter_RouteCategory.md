# SKILL: android.media.MediaRouter.RouteCategory

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter.RouteCategory`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter.RouteCategory` |
| **Package** | `android.media.MediaRouter` |
| **Total Methods** | 5 |
| **Avg Score** | 6.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (60%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getRoutes` | `java.util.List<android.media.MediaRouter.RouteInfo> getRoutes(java.util.List<android.media.MediaRouter.RouteInfo>)` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getName` | `CharSequence getName()` | 6 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getName` | `CharSequence getName(android.content.Context)` | 6 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getSupportedTypes` | `int getSupportedTypes()` | 6 | partial | moderate | `OH_AVCapability_GetSupportedProfiles` | `OH_AVErrCode OH_AVCapability_GetSupportedProfiles(OH_AVCapability *capability, const int32_t **profiles,
                                                  uint32_t *profileNum)` |
| `isGroupable` | `boolean isGroupable()` | 5 | partial | moderate | `isOnline` | `readonly isOnline: boolean` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter.RouteCategory`:


## Quality Gates

Before marking `android.media.MediaRouter.RouteCategory` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
