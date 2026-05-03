package com.mcdonalds.mcdcoreapp.network;

public interface RequestProvider<T, E> {
    enum MethodType {
        GET
    }

    enum RequestType {
        JSON
    }

    default int a() {
        return 0;
    }

    String b();

    Class c();
}
