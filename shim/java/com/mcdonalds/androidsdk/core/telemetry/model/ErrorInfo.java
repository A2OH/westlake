package com.mcdonalds.androidsdk.core.telemetry.model;

public class ErrorInfo {
    private int httpStatusCode;
    private String mcdUuid;
    private int networkErrorCode;
    private String requestMethod;
    private String requestUrl;
    private String response;
    private String resultType;
    private int serviceErrorCode;

    public int getHttpStatusCode() { return httpStatusCode; }
    public String getMcdUuid() { return mcdUuid; }
    public int getNetworkErrorCode() { return networkErrorCode; }
    public String getRequestMethod() { return requestMethod; }
    public String getRequestUrl() { return requestUrl; }
    public String getResponse() { return response; }
    public String getResultType() { return resultType; }
    public int getServiceErrorCode() { return serviceErrorCode; }

    public void setHttpStatusCode(int value) { httpStatusCode = value; }
    public void setMcdUuid(String value) { mcdUuid = value; }
    public void setNetworkErrorCode(int value) { networkErrorCode = value; }
    public void setRequestMethod(String value) { requestMethod = value; }
    public void setRequestUrl(String value) { requestUrl = value; }
    public void setResponse(String value) { response = value; }
    public void setResultType(String value) { resultType = value; }
    public void setServiceErrorCode(int value) { serviceErrorCode = value; }

    public long getMaxAge(long now, long fallback) { return fallback; }
    public boolean isSecure() { return false; }
    public void setSecure(boolean value) {}
}
