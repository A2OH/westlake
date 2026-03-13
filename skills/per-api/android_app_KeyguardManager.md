# SKILL: android.app.KeyguardManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.KeyguardManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.KeyguardManager` |
| **Package** | `android.app` |
| **Total Methods** | 9 |
| **Avg Score** | 7.5 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 8 (88%) |
| **Partial/Composite** | 1 (11%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isDeviceLocked` | `boolean isDeviceLocked()` | 9 | direct | trivial | `isLocked` | `isLocked(): boolean` |
| `isDeviceSecure` | `boolean isDeviceSecure()` | 9 | direct | easy | `isSecureMode` | `isSecureMode(callback: AsyncCallback<boolean>): void` |
| `isKeyguardLocked` | `boolean isKeyguardLocked()` | 9 | direct | easy | `isScreenLocked` | `isScreenLocked(callback: AsyncCallback<boolean>): void` |
| `isKeyguardSecure` | `boolean isKeyguardSecure()` | 9 | direct | easy | `isSecureMode` | `isSecureMode(callback: AsyncCallback<boolean>): void` |
| `onDismissCancelled` | `void onDismissCancelled()` | 7 | near | easy | `isScreenLocked` | `isScreenLocked(callback: AsyncCallback<boolean>): void` |
| `onDismissSucceeded` | `void onDismissSucceeded()` | 7 | near | easy | `isScreenLocked` | `isScreenLocked(callback: AsyncCallback<boolean>): void` |
| `onDismissError` | `void onDismissError()` | 7 | near | moderate | `isSecureMode` | `isSecureMode(callback: AsyncCallback<boolean>): void` |
| `requestDismissKeyguard` | `void requestDismissKeyguard(@NonNull android.app.Activity, @Nullable android.app.KeyguardManager.KeyguardDismissCallback)` | 6 | near | moderate | `isSecureMode` | `isSecureMode(callback: AsyncCallback<boolean>): void` |
| `KeyguardDismissCallback` | `KeyguardManager.KeyguardDismissCallback()` | 6 | partial | moderate | `isScreenLocked` | `isScreenLocked(callback: AsyncCallback<boolean>): void` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.app.KeyguardManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.KeyguardManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
