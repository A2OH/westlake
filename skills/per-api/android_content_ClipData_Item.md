# SKILL: android.content.ClipData.Item

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ClipData.Item`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ClipData.Item` |
| **Package** | `android.content.ClipData` |
| **Total Methods** | 13 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 11 (84%) |
| **Partial/Composite** | 2 (15%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getUri` | `android.net.Uri getUri()` | 8 | direct | easy | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getText` | `CharSequence getText()` | 8 | direct | easy | `getContext` | `getContext(): Context` |
| `Item` | `ClipData.Item(CharSequence)` | 8 | direct | easy | `item` | `item: application event logging switch.
     *
     * @syscap SystemCapability.HiviewDFX.HiAppEvent
     * @since 7
     * @deprecated since 9
     */
    disable?: boolean` |
| `Item` | `ClipData.Item(CharSequence, String)` | 8 | direct | easy | `item` | `item: application event logging switch.
     *
     * @syscap SystemCapability.HiviewDFX.HiAppEvent
     * @since 7
     * @deprecated since 9
     */
    disable?: boolean` |
| `Item` | `ClipData.Item(android.content.Intent)` | 8 | direct | easy | `item` | `item: application event logging switch.
     *
     * @syscap SystemCapability.HiviewDFX.HiAppEvent
     * @since 7
     * @deprecated since 9
     */
    disable?: boolean` |
| `Item` | `ClipData.Item(android.net.Uri)` | 8 | direct | easy | `item` | `item: application event logging switch.
     *
     * @syscap SystemCapability.HiviewDFX.HiAppEvent
     * @since 7
     * @deprecated since 9
     */
    disable?: boolean` |
| `Item` | `ClipData.Item(CharSequence, android.content.Intent, android.net.Uri)` | 8 | direct | easy | `item` | `item: application event logging switch.
     *
     * @syscap SystemCapability.HiviewDFX.HiAppEvent
     * @since 7
     * @deprecated since 9
     */
    disable?: boolean` |
| `Item` | `ClipData.Item(CharSequence, String, android.content.Intent, android.net.Uri)` | 8 | direct | easy | `item` | `item: application event logging switch.
     *
     * @syscap SystemCapability.HiviewDFX.HiAppEvent
     * @since 7
     * @deprecated since 9
     */
    disable?: boolean` |
| `getIntent` | `android.content.Intent getIntent()` | 8 | near | easy | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getHtmlText` | `String getHtmlText()` | 7 | near | moderate | `getContext` | `getContext(): Context` |
| `coerceToText` | `CharSequence coerceToText(android.content.Context)` | 6 | near | moderate | `moveToNext` | `moveToNext(): boolean` |
| `coerceToHtmlText` | `String coerceToHtmlText(android.content.Context)` | 5 | partial | moderate | `moveToNext` | `moveToNext(): boolean` |
| `coerceToStyledText` | `CharSequence coerceToStyledText(android.content.Context)` | 5 | partial | moderate | `moveToNext` | `moveToNext(): boolean` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ClipData.Item`:


## Quality Gates

Before marking `android.content.ClipData.Item` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
