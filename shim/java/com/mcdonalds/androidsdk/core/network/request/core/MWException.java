package com.mcdonalds.androidsdk.core.network.request.core;

import com.mcdonalds.androidsdk.core.McDException;
import com.mcdonalds.androidsdk.core.network.model.GraphQLExtension;
import java.util.List;

public class MWException extends Exception {
    public final int a;
    public final int b;
    public final String c;
    public final List d;
    public final String e;
    public final String f;
    public List g;
    public GraphQLExtension h;

    public MWException(int status, int code, String message, List errors, String response) {
        this(status, code, message, errors, response, null);
    }

    public MWException(int status, int code, String message, List errors,
            String response, String resultType) {
        super(message);
        a = status;
        b = code;
        c = message;
        d = errors;
        e = response;
        f = resultType;
    }

    public MWException(int status, int code, String message, List errors,
            List locations, GraphQLExtension extension, String response,
            String resultType) {
        this(status, code, message, errors, response, resultType);
        g = locations;
        h = extension;
    }

    public MWException(McDException exception) {
        this(exception != null ? exception.getHttpStatusCode() : 0,
                exception != null ? exception.getErrorCode() : 0,
                exception != null ? exception.getMessage() : null,
                exception != null ? exception.getErrors() : null,
                exception != null ? exception.getResponse() : null,
                exception != null ? exception.getResultType() : null);
        h = exception != null ? exception.getGraphQLExtension() : null;
        g = exception != null ? exception.getGraphQLErrorLocations() : null;
    }

    public int getErrorCode() { return b; }
    public List getErrors() { return d; }
    public List getGraphQLErrorLocations() { return g; }
    public GraphQLExtension getGraphQLExtension() { return h; }
    public int getHttpStatusCode() { return a; }
    public String getMessage() { return c != null ? c : super.getMessage(); }
    public String getResponse() { return e; }
    public String getResultType() { return f; }
    public void setGraphQLErrorLocations(List value) { g = value; }
    public void setGraphQLExtension(GraphQLExtension value) { h = value; }
}
