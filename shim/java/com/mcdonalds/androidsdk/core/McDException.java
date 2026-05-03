package com.mcdonalds.androidsdk.core;

import com.mcdonalds.androidsdk.core.network.model.GraphQLExtension;
import com.mcdonalds.androidsdk.core.network.model.ServerError;
import com.mcdonalds.androidsdk.core.network.request.core.MWException;
import com.mcdonalds.androidsdk.core.telemetry.model.ErrorInfo;
import java.util.List;

public final class McDException extends RuntimeException {
    public final int a;
    public final int b;
    public final String c;
    public final String d;
    public final transient List e;
    public final transient List f;
    public final transient GraphQLExtension g;
    public ErrorInfo h;
    private final String mResponse;
    private final String mResultType;

    public McDException(int code) {
        this(code, "sdk_error_" + Math.abs(code));
    }

    public McDException(int code, int stringId) {
        this(code, "sdk_error_" + Math.abs(code));
    }

    public McDException(int code, int stringId, String... args) {
        this(code, "sdk_error_" + Math.abs(code));
    }

    public McDException(int code, String message) {
        this(code, message, 200, null, null, null, null);
    }

    public McDException(int code, String message, int httpStatus, List errors,
            GraphQLExtension extension, List details, String serverCode) {
        this(code, message, httpStatus, errors, extension, details, serverCode, null);
    }

    public McDException(int code, String message, int httpStatus, List errors,
            GraphQLExtension extension, List details, String serverCode,
            String response) {
        super(message != null ? message : "Unknown Error");
        a = code != 0 ? code : -10000;
        b = httpStatus;
        c = serverCode;
        d = "";
        e = errors;
        f = details;
        g = extension;
        mResponse = response;
        mResultType = null;
    }

    public McDException(int code, Throwable cause) {
        super(cause != null ? cause.getMessage() : "Unknown Error", cause);
        a = code != 0 ? code : -10000;
        b = 0;
        c = null;
        d = "";
        e = null;
        f = null;
        g = null;
        mResponse = null;
        mResultType = null;
    }

    public McDException(int code, String... args) {
        this(code, args != null && args.length > 0 ? args[0] : null);
    }

    public McDException(MWException exception) {
        this(exception != null ? exception.getErrorCode() : -10000,
                exception != null ? exception.getMessage() : "Unknown Error",
                exception != null ? exception.getHttpStatusCode() : 0,
                exception != null ? exception.getErrors() : null,
                exception != null ? exception.getGraphQLExtension() : null,
                exception != null ? exception.getGraphQLErrorLocations() : null,
                null,
                exception != null ? exception.getResponse() : null);
    }

    public ServerError getError() {
        ServerError error = new ServerError();
        error.setCode(a);
        error.setMessage(getMessage());
        error.setService(c);
        return error;
    }

    public int getErrorCode() {
        return a;
    }

    public ErrorInfo getErrorInfo() {
        return h;
    }

    public List getErrors() {
        return e;
    }

    public int getGenericErrorCode() {
        return a;
    }

    public String getGenericMessage() {
        return getMessage();
    }

    public List getGraphQLErrorLocations() {
        return f;
    }

    public GraphQLExtension getGraphQLExtension() {
        return g;
    }

    public int getHttpStatusCode() {
        return b;
    }

    public String getLocalizedMessage() {
        return getMessage();
    }

    public String getMessage() {
        String message = super.getMessage();
        return message != null ? message : "Unknown Error";
    }

    public String getResponse() {
        return mResponse;
    }

    public String getResultType() {
        return mResultType;
    }

    public void setErrorInfo(ErrorInfo value) {
        h = value;
    }

    public String toString() {
        return "McDException{code=" + a + ", status=" + b
                + ", message=" + getMessage() + "}";
    }
}
