package com.mcdonalds.mcdcoreapp.startup.model;

/**
 * Shim for McDonald's InitializationConfig — provides safe defaults
 * so BaseActivity.onCreate doesn't NPE.
 */
public class InitializationConfig {
    public boolean isInitializationFrameworkFlagEnabled() { return false; }
    public boolean isEnabled() { return false; }
    public String getConfig() { return "{}"; }
}
