package com.mcdonalds.androidsdk.core.network.model;

public class ServerError {
    private int code;
    private String message;
    private String path;
    private String property;
    private String service;
    private String type;

    public static ServerError fromDTO(
            com.mcdonalds.androidsdk.core.network.model.dto.ServerErrorDTO dto) {
        ServerError error = new ServerError();
        if (dto != null) {
            error.setCode(dto.getCode());
            error.setMessage(dto.getMessage());
            error.setPath(dto.getPath());
            error.setProperty(dto.getProperty());
            error.setService(dto.getService());
            error.setType(dto.getType());
        }
        return error;
    }

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

    public long getMaxAge(long now, long fallback) { return fallback; }
    public boolean isSecure() { return false; }
    public void setSecure(boolean value) {}

    public String toString() {
        return "ServerError{code=" + code + ", message=" + message + "}";
    }
}
