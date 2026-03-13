# SKILL: android.os.UserManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.UserManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.UserManager` |
| **Package** | `android.os` |
| **Total Methods** | 15 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 10 (66%) |
| **Partial/Composite** | 4 (26%) |
| **No Mapping** | 1 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isUserUnlocked` | `boolean isUserUnlocked()` | 8 | near | easy | `isScreenLocked` | `isScreenLocked(callback: AsyncCallback<boolean>): void` |
| `getUserProfiles` | `java.util.List<android.os.UserHandle> getUserProfiles()` | 8 | near | easy | `getUserFileMgr` | `getUserFileMgr(context: Context): UserFileManager` |
| `getUserRestrictions` | `android.os.Bundle getUserRestrictions()` | 7 | near | easy | `getCurrentFunctions` | `getCurrentFunctions(): FunctionType` |
| `getUserCreationTime` | `long getUserCreationTime(android.os.UserHandle)` | 7 | near | easy | `getCurrentTime` | `getCurrentTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `isQuietModeEnabled` | `boolean isQuietModeEnabled(android.os.UserHandle)` | 7 | near | moderate | `isPiPEnabled` | `isPiPEnabled(): boolean` |
| `isManagedProfile` | `boolean isManagedProfile()` | 6 | near | moderate | `isDLPFile` | `isDLPFile(fd: number): Promise<boolean>` |
| `isDemoUser` | `boolean isDemoUser()` | 6 | near | moderate | `setUserId` | `setUserId(name: string, value: string): void` |
| `getUserForSerialNumber` | `android.os.UserHandle getUserForSerialNumber(long)` | 6 | near | moderate | `getErrorString` | `getErrorString(errno: number): string` |
| `createUserCreationIntent` | `static android.content.Intent createUserCreationIntent(@Nullable String, @Nullable String, @Nullable String, @Nullable android.os.PersistableBundle)` | 6 | near | moderate | `createVirtualScreen` | `createVirtualScreen(options: VirtualScreenOption, callback: AsyncCallback<Screen>): void` |
| `requestQuietModeEnabled` | `boolean requestQuietModeEnabled(boolean, @NonNull android.os.UserHandle, int)` | 6 | near | moderate | `setGestureNavigationEnabled` | `setGestureNavigationEnabled(enable: boolean, callback: AsyncCallback<void>): void` |
| `isSystemUser` | `boolean isSystemUser()` | 6 | partial | moderate | `getSystemPasteboard` | `getSystemPasteboard(): SystemPasteboard` |
| `getSerialNumberForUser` | `long getSerialNumberForUser(android.os.UserHandle)` | 6 | partial | moderate | `getErrorString` | `getErrorString(errno: number): string` |
| `isUserAGoat` | `boolean isUserAGoat()` | 5 | partial | moderate | `setUserProperty` | `setUserProperty(name: string, value: string): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `hasUserRestriction` | 5 | partial | Return safe default (null/false/0/empty) |
| `supportsMultipleUsers` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.UserManager`:


## Quality Gates

Before marking `android.os.UserManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
