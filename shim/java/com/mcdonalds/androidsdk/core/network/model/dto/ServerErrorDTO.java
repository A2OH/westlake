package com.mcdonalds.androidsdk.core.network.model.dto;

public class ServerErrorDTO {
    private int code;
    private String message;
    private String path;
    private String property;
    private String service;
    private String type;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public String getProperty() { return property; }
    public String getService() { return service; }
    public String getType() { return type; }

    public void setCode(int value) { code = value; }
    public void setMessage(String value) { message = value; }
    public void setPath(String value) { path = value; }
    public void setProperty(String value) { property = value; }
    public void setService(String value) { service = value; }
    public void setType(String value) { type = value; }
}
