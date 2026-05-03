package com.mcdonalds.mcdcoreapp.listeners;

import com.mcdonalds.androidsdk.core.McDException;

public interface McDListener<T> {
    default void onErrorResponse(Object value, McDException error, PerfHttpErrorInfo info) {
    }

    void onResponse(Object value, McDException error, PerfHttpErrorInfo info);
}
