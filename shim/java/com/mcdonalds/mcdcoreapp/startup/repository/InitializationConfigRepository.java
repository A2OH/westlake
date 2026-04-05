package com.mcdonalds.mcdcoreapp.startup.repository;

import com.mcdonalds.mcdcoreapp.startup.model.InitializationConfig;

/**
 * Shim — returns a default InitializationConfig so BaseActivity.onCreate doesn't NPE.
 */
public class InitializationConfigRepository {
    private static final InitializationConfig DEFAULT = new InitializationConfig();
    public InitializationConfig getConfig() { return DEFAULT; }
    public InitializationConfig s() { return DEFAULT; }
}
