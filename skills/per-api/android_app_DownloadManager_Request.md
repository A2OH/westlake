# SKILL: android.app.DownloadManager.Request

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.DownloadManager.Request`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.DownloadManager.Request` |
| **Package** | `android.app.DownloadManager` |
| **Total Methods** | 14 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (7%) |
| **Partial/Composite** | 13 (92%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Request` | `DownloadManager.Request(android.net.Uri)` | 8 | near | easy | `requestForm` | `requestForm(formId: string, callback: AsyncCallback<void>): void` |
| `addRequestHeader` | `android.app.DownloadManager.Request addRequestHeader(String, String)` | 6 | partial | moderate | `requestForm` | `requestForm(formId: string, callback: AsyncCallback<void>): void` |
| `setDescription` | `android.app.DownloadManager.Request setDescription(CharSequence)` | 6 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setRequiresCharging` | `android.app.DownloadManager.Request setRequiresCharging(boolean)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setDestinationUri` | `android.app.DownloadManager.Request setDestinationUri(android.net.Uri)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |
| `setMimeType` | `android.app.DownloadManager.Request setMimeType(String)` | 5 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `setAllowedNetworkTypes` | `android.app.DownloadManager.Request setAllowedNetworkTypes(int)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setDestinationInExternalFilesDir` | 5 | partial | Log warning + no-op |
| `setNotificationVisibility` | 5 | partial | Return safe default (null/false/0/empty) |
| `setAllowedOverMetered` | 5 | partial | Log warning + no-op |
| `setRequiresDeviceIdle` | 5 | partial | Log warning + no-op |
| `setAllowedOverRoaming` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `setDestinationInExternalPublicDir` | 4 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.DownloadManager.Request`:


## Quality Gates

Before marking `android.app.DownloadManager.Request` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
