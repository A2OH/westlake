package com.mcdonalds.androidsdk.core.network.model;

public class GraphQLExtension {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        code = value;
    }

    public long getMaxAge(long now, long fallback) {
        return fallback;
    }

    public boolean isSecure() {
        return false;
    }

    public void setSecure(boolean value) {
    }
}
