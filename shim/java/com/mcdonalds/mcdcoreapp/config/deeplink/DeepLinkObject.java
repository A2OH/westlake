package com.mcdonalds.mcdcoreapp.config.deeplink;

import org.json.JSONObject;

/**
 * Stub DeepLinkObject — handles null JSONObject gracefully.
 * Shadows the McDonald's DEX version via boot classpath.
 */
public class DeepLinkObject {
    public JSONObject a;
    public JSONObject b;

    public DeepLinkObject(JSONObject json) {
        if (json != null) {
            try { this.a = json.optJSONObject("raw"); } catch (Exception e) {}
            try { this.b = json.optJSONObject("deferred_deeplink"); } catch (Exception e) {}
        }
        if (this.a == null) this.a = new JSONObject();
        if (this.b == null) this.b = new JSONObject();
    }

    public DeepLinkObject() {
        this.a = new JSONObject();
        this.b = new JSONObject();
    }

    // Common accessor methods
    public JSONObject getRawJson() { return a; }
    public JSONObject getDeferredDeeplink() { return b; }
    public boolean hasDeeplink() { return b != null && b.length() > 0; }
    public String getDeeplink() { return b != null ? b.optString("deeplink", "") : ""; }
}
