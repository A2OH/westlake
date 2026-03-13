# SKILL: android.content.RestrictionsManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.RestrictionsManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.RestrictionsManager` |
| **Package** | `android.content` |
| **Total Methods** | 7 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (42%) |
| **Partial/Composite** | 4 (57%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `requestPermission` | `void requestPermission(String, String, android.os.PersistableBundle)` | 7 | near | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getApplicationRestrictions` | `android.os.Bundle getApplicationRestrictions()` | 7 | near | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `notifyPermissionResponse` | `void notifyPermissionResponse(String, android.os.PersistableBundle)` | 6 | near | moderate | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `getManifestRestrictions` | `java.util.List<android.content.RestrictionEntry> getManifestRestrictions(String)` | 6 | partial | moderate | `getForegroundApplications` | `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void` |
| `convertRestrictionsToBundle` | `static android.os.Bundle convertRestrictionsToBundle(java.util.List<android.content.RestrictionEntry>)` | 5 | partial | moderate | `moveMissionsToBackground` | `moveMissionsToBackground(missionIds: Array<number>, callback: AsyncCallback<Array<number>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `createLocalApprovalIntent` | 5 | partial | Return dummy instance / no-op |
| `hasRestrictionsProvider` | 3 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.RestrictionsManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.RestrictionsManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
