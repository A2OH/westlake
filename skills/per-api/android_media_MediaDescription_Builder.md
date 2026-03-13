# SKILL: android.media.MediaDescription.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaDescription.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaDescription.Builder` |
| **Package** | `android.media.MediaDescription` |
| **Total Methods** | 10 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 6 (60%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setTitle` | `android.media.MediaDescription.Builder setTitle(@Nullable CharSequence)` | 8 | near | easy | `title` | `title: string` |
| `setMediaId` | `android.media.MediaDescription.Builder setMediaId(@Nullable String)` | 6 | near | moderate | `getMediaLibrary` | `getMediaLibrary(): MediaLibrary` |
| `setMediaUri` | `android.media.MediaDescription.Builder setMediaUri(@Nullable android.net.Uri)` | 6 | near | moderate | `getMediaLibrary` | `getMediaLibrary(): MediaLibrary` |
| `setIconUri` | `android.media.MediaDescription.Builder setIconUri(@Nullable android.net.Uri)` | 6 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setSubtitle` | `android.media.MediaDescription.Builder setSubtitle(@Nullable CharSequence)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setDescription` | `android.media.MediaDescription.Builder setDescription(@Nullable CharSequence)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setExtras` | `android.media.MediaDescription.Builder setExtras(@Nullable android.os.Bundle)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setIconBitmap` | `android.media.MediaDescription.Builder setIconBitmap(@Nullable android.graphics.Bitmap)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Builder` | 4 | partial | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaDescription.Builder`:


## Quality Gates

Before marking `android.media.MediaDescription.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
