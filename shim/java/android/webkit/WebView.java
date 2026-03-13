package android.webkit;

import java.util.ArrayList;
import java.util.List;

public class WebView {
    private final List<String> mHistory = new ArrayList<>();
    private int mHistoryIndex = -1;
    private String mTitle;
    private boolean mDestroyed;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;
    private final WebSettings mSettings = new WebSettings();

    public WebView() {}

    public WebSettings getSettings() { return mSettings; }

    public static final int RENDERER_PRIORITY_BOUND = 0;
    public static final int RENDERER_PRIORITY_IMPORTANT = 0;
    public static final int RENDERER_PRIORITY_WAIVED = 0;
    public static final int SCHEME_GEO = 0;
    public static final int SCHEME_MAILTO = 0;
    public static final int SCHEME_TEL = 0;

    public String getUrl() {
        if (mHistoryIndex >= 0 && mHistoryIndex < mHistory.size()) {
            return mHistory.get(mHistoryIndex);
        }
        return null;
    }

    public String getTitle() { return mTitle; }

    /** Test-support: push a title and fire the WebChromeClient callback. */
    public void pushTitle(String title) {
        mTitle = title;
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedTitle(this, title);
        }
    }

    public void loadUrl(String url) {
        if (mDestroyed) throw new IllegalStateException("WebView is destroyed");
        // Clear forward history
        while (mHistory.size() > mHistoryIndex + 1) {
            mHistory.remove(mHistory.size() - 1);
        }
        mHistory.add(url);
        mHistoryIndex = mHistory.size() - 1;
        if (mWebChromeClient != null) {
            mWebChromeClient.onProgressChanged(this, 100);
        }
        if (mWebViewClient != null) {
            mWebViewClient.onPageFinished(this, url);
        }
    }

    public void loadUrl(Object url, Object headers) {
        if (url instanceof String) loadUrl((String) url);
    }

    public boolean canGoBack() {
        return mHistoryIndex > 0;
    }

    public boolean canGoForward() {
        return mHistoryIndex < mHistory.size() - 1;
    }

    public void goBack() {
        if (canGoBack()) mHistoryIndex--;
    }

    public void goForward() {
        if (canGoForward()) mHistoryIndex++;
    }

    public void reload() {
        String url = getUrl();
        if (mWebViewClient != null && url != null) {
            mWebViewClient.onPageFinished(this, url);
        }
    }

    public void loadData(Object data, Object mimeType, Object encoding) {
        // stub
    }

    public void loadDataWithBaseURL(Object baseUrl, Object data, Object mimeType, Object encoding, Object historyUrl) {
        if (historyUrl instanceof String) {
            // Clear forward history
            while (mHistory.size() > mHistoryIndex + 1) {
                mHistory.remove(mHistory.size() - 1);
            }
            mHistory.add((String) historyUrl);
            mHistoryIndex = mHistory.size() - 1;
        }
    }

    public void destroy() {
        mDestroyed = true;
    }

    public void setWebViewClient(Object client) {
        if (client instanceof WebViewClient) mWebViewClient = (WebViewClient) client;
    }

    public void setWebChromeClient(Object client) {
        if (client instanceof WebChromeClient) mWebChromeClient = (WebChromeClient) client;
    }

    public void evaluateJavascript(Object script, Object callback) {
        if (callback instanceof ValueCallback) {
            ((ValueCallback<?>) callback).onReceiveValue(null);
        }
    }

    public void addJavascriptInterface(Object obj, Object name) {}
    public boolean canGoBackOrForward(Object steps) { return false; }
    public void clearCache(Object includeDiskFiles) {}
    public static void clearClientCertPreferences(Object callback) {}
    public void clearFormData() {}
    public void clearHistory() { mHistory.clear(); mHistoryIndex = -1; }
    public void clearMatches() {}
    public void clearSslPreferences() {}
    public static void disableWebView() {}
    public void documentHasImages(Object msg) {}
    public static void enableSlowWholeDocumentDraw() {}
    public void findAllAsync(Object find) {}
    public void findNext(Object forward) {}
    public void flingScroll(Object vx, Object vy) {}
    public int getProgress() { return 0; }
    public boolean getRendererPriorityWaivedWhenNotVisible() { return false; }
    public int getRendererRequestedPriority() { return 0; }
    public void goBackOrForward(Object steps) {}
    public void invokeZoomPicker() {}
    public boolean isPrivateBrowsingEnabled() { return false; }
    public void onPause() {}
    public void onResume() {}
    public boolean pageDown(Object bottom) { return false; }
    public boolean pageUp(Object top) { return false; }
    public void pauseTimers() {}
    public void postUrl(Object url, Object postData) {}
    public void postVisualStateCallback(Object requestId, Object callback) {}
    public void postWebMessage(Object message, Object targetOrigin) {}
    public void removeJavascriptInterface(Object name) {}
    public void requestFocusNodeHref(Object hrefMsg) {}
    public void requestImageRef(Object msg) {}
    public void resumeTimers() {}
    public void saveWebArchive(Object filename) {}
    public void saveWebArchive(Object basename, Object autoname, Object callback) {}
    public static void setDataDirectorySuffix(Object suffix) {}
    public void setDownloadListener(Object listener) {}
    public void setFindListener(Object listener) {}
    public void setInitialScale(Object scaleInPercent) {}
    public void setNetworkAvailable(Object networkUp) {}
    public void setRendererPriorityPolicy(Object rendererRequestedPriority, Object waivedWhenNotVisible) {}
    public static void setSafeBrowsingWhitelist(Object hosts, Object callback) {}
    public void setTextClassifier(Object textClassifier) {}
    public static void setWebContentsDebuggingEnabled(Object enabled) {}
    public void setWebViewRenderProcessClient(Object executor, Object client) {}
    public void setWebViewRenderProcessClient(Object client) {}
    public static void startSafeBrowsing(Object context, Object callback) {}
    public void stopLoading() {}
    public void zoomBy(Object zoomFactor) {}
    public boolean zoomIn() { return false; }
    public boolean zoomOut() { return false; }
}
