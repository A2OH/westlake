# Fix 4 Pre-existing Test Failures

## Mission

Fix all 4 remaining pre-existing test failures in the headless test suite. After your changes: **836+ pass, 0 fail**.

Run in danger/autonomous mode — no confirmation needed. Just fix, test, done.

## Current State

```
Passed: 836
Failed: 4
```

Failing tests:
1. `row count = 2` — SQLiteDatabase aggregate COUNT(*) not computed
2. `setUserAgentString / getUserAgentString` — WebSettings doesn't store user agent
3. `onPageStarted callback fired` — WebView.loadUrl() never fires onPageStarted
4. `onPageStarted URL matches` — consequence of #3

## Bug 1: SQLiteDatabase `SELECT COUNT(*) FROM notes` returns 0 instead of 2

**Test location:** `test-apps/02-headless-cli/src/HeadlessTest.java` ~line 280

**Test code:**
```java
// After inserting 2 rows into "notes" table
cursor = db.rawQuery("SELECT COUNT(*) FROM notes", null);
cursor.moveToFirst();
int count = cursor.getInt(0);  // expects 2, gets 0
check("row count = 2", count == 2);
```

**Bug location:** `shim/java/android/database/sqlite/SQLiteDatabase.java` — `rawQueryInternal()` method

**Root cause:** The SQL parser treats `COUNT(*)` as a literal column name and tries to fetch it from table rows. It never actually **computes** the aggregate. The code path:
1. Extracts `colPart = "COUNT(*)"`
2. Calls `stripQuotes("COUNT(*)")` → returns unchanged
3. Passes `"COUNT(*)"` as a column name to `queryInternal()`
4. `queryInternal()` can't find a column called `COUNT(*)` in the table → returns 0/null

**Fix:** In `rawQueryInternal()`, detect `SELECT COUNT(*) FROM <table>` (and optionally `WHERE`) patterns. When detected:
1. Get the table's row list from `mTables`
2. Apply the WHERE filter if present
3. Return a single-row MatrixCursor with column `"COUNT(*)"` and value = filtered row count

The simplest approach: add a check near the top of `rawQueryInternal()` after parsing the SELECT:

```java
// After extracting colPart and tableName:
if (colPart.toUpperCase(Locale.US).equals("COUNT(*)")) {
    // Compute row count
    List<Map<String, String>> rows = mTables.get(tableName);
    int rowCount = 0;
    if (rows != null) {
        if (whereClause != null && !whereClause.isEmpty()) {
            // Apply WHERE filter
            for (Map<String, String> row : rows) {
                if (matchesWhere(row, whereClause, selectionArgs)) {
                    rowCount++;
                }
            }
        } else {
            rowCount = rows.size();
        }
    }
    MatrixCursor mc = new MatrixCursor(new String[]{"COUNT(*)"});
    mc.addRow(new Object[]{rowCount});
    return mc;
}
```

Make sure you read the full `rawQueryInternal()` method to understand the existing parsing flow and where to insert this. The key variables you need (`tableName`, `whereClause`, `selectionArgs`) are already parsed later in the method, so you may need to either:
- Move the COUNT detection after the parsing, or
- Do a separate lightweight parse for COUNT queries before the main parse

**Important:** Don't break any existing SQLite tests. There are many passing tests for insert, query, rawQuery, transaction, etc.

## Bug 2: WebSettings `setUserAgentString` / `getUserAgentString`

**Test location:** `test-apps/02-headless-cli/src/HeadlessTest.java` ~line 1244

**Test code:**
```java
ws.setUserAgentString("TestAgent/1.0");
check("setUserAgentString / getUserAgentString",
      "TestAgent/1.0".equals(ws.getUserAgentString()));
```

**Bug location:** `shim/java/android/webkit/WebSettings.java` ~lines 95-101

**Current broken code:**
```java
private String userAgent = "Mozilla/5.0 (Linux; Android)";

public void setUserAgentString(String ua) {
//     this.userAgentString = (ua != null) ? ua : "";  // commented out + wrong field name
}

public String getUserAgentString() {
    return null;  // always returns null
}
```

**Fix:**
```java
public void setUserAgentString(String ua) {
    this.userAgent = (ua != null) ? ua : "";
}

public String getUserAgentString() {
    return userAgent;
}
```

That's it. The field `userAgent` already exists. Just uncomment/fix the setter and fix the getter return.

## Bug 3 & 4: WebView `onPageStarted` never fired

**Test location:** `test-apps/02-headless-cli/src/HeadlessTest.java` ~line 1256-1280

**Test code:**
```java
final boolean[] pageStarted = {false};
final String[] callbackUrl = {null};

wv2.setWebViewClient(new android.webkit.WebViewClient() {
    @Override
    public void onPageStarted(android.webkit.WebView view, String url,
                              android.graphics.Bitmap favicon) {
        pageStarted[0] = true;
        callbackUrl[0] = url;
    }
});

wv2.loadUrl("https://test.example");
check("onPageStarted callback fired", pageStarted[0]);     // FAIL
check("onPageStarted URL matches", "https://test.example".equals(callbackUrl[0]));  // FAIL
```

**Bug location:** `shim/java/android/webkit/WebView.java` — `loadUrl()` method ~line 43-57

**Current broken code:**
```java
public void loadUrl(String url) {
    // ... history management ...
    if (mWebChromeClient != null) {
        mWebChromeClient.onProgressChanged(this, 100);
    }
    if (mWebViewClient != null) {
        mWebViewClient.onPageFinished(this, url);  // ← only fires onPageFinished!
    }
}
```

**Fix:** Add `onPageStarted` call BEFORE `onPageFinished`:
```java
if (mWebViewClient != null) {
    mWebViewClient.onPageStarted(this, url, null);  // ADD THIS LINE
    mWebViewClient.onPageFinished(this, url);
}
```

The `null` bitmap parameter is fine — the shim has no favicon support. This single line addition fixes both test 3 and test 4.

## Execution Steps

1. **Read each file before editing** — understand the surrounding code
2. **Make the minimal fix** — don't refactor, don't add features, just fix the bug
3. **Run the full test suite** after all fixes:
   ```bash
   cd test-apps && ./run-local-tests.sh headless
   ```
4. **Verify: 0 failures, 836+ passes**
5. If any test regresses, investigate and fix before finishing

## Files to Modify

| File | Fix |
|------|-----|
| `shim/java/android/database/sqlite/SQLiteDatabase.java` | Handle `SELECT COUNT(*) FROM` in `rawQueryInternal()` |
| `shim/java/android/webkit/WebSettings.java` | Fix `setUserAgentString()` and `getUserAgentString()` |
| `shim/java/android/webkit/WebView.java` | Add `onPageStarted()` call in `loadUrl()` |

## Key Rules

- **Don't break existing tests** — 836 currently pass, no regressions allowed
- **Minimal changes only** — fix the bug, nothing more
- **Read before editing** — always read the file first
- **Run full suite** — verify with `cd test-apps && ./run-local-tests.sh headless`
- **Target: 0 failures**
