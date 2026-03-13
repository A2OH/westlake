# SKILL: android.telephony.MbmsDownloadSession

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.MbmsDownloadSession`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.MbmsDownloadSession` |
| **Package** | `android.telephony` |
| **Total Methods** | 12 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (33%) |
| **Partial/Composite** | 7 (58%) |
| **No Mapping** | 1 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `download` | `void download(@NonNull android.telephony.mbms.DownloadRequest)` | 8 | direct | easy | `downloadMms` | `downloadMms(context: Context, mmsParams: MmsParams, callback: AsyncCallback<void>): void` |
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `cancelDownload` | `void cancelDownload(@NonNull android.telephony.mbms.DownloadRequest)` | 6 | near | moderate | `downloadMms` | `downloadMms(context: Context, mmsParams: MmsParams, callback: AsyncCallback<void>): void` |
| `create` | `static android.telephony.MbmsDownloadSession create(@NonNull android.content.Context, @NonNull java.util.concurrent.Executor, @NonNull android.telephony.mbms.MbmsDownloadSessionCallback)` | 6 | near | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `requestDownloadState` | `void requestDownloadState(android.telephony.mbms.DownloadRequest, android.telephony.mbms.FileInfo)` | 6 | partial | moderate | `downloadMms` | `downloadMms(context: Context, mmsParams: MmsParams, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `requestUpdateFileServices` | 5 | partial | Log warning + no-op |
| `resetDownloadKnowledge` | 5 | partial | Log warning + no-op |
| `addProgressListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setTempFileRootDirectory` | 4 | partial | Log warning + no-op |
| `addStatusListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `removeStatusListener` | 4 | composite | Return safe default (null/false/0/empty) |
| `removeProgressListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.MbmsDownloadSession`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.MbmsDownloadSession` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
