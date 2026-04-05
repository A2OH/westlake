package com.mcdonalds.homedashboard.util;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeHelper {
    public static ArrayList<Object> appParameter = new ArrayList<>();
    public boolean isMobileOfferAvailable;
    private static HomeHelper sInstance;

    public static HomeHelper getInstance() {
        if (sInstance == null) sInstance = new HomeHelper();
        return sInstance;
    }

    public static void setAppParameter(String json) {
        System.err.println("[HomeHelper] setAppParameter json='" + json + "'");
        // Provide valid market config
        appParameter.clear();
        java.util.HashMap<String, Object> us = new java.util.HashMap<>();
        us.put("marketId", "US");
        us.put("country", "US");
        us.put("language", "en");
        us.put("currencyCode", "USD");
        us.put("currencySymbol", "$");
        us.put("baseUrl", "https://us-prod.api.mcd.com/exp/v1/");
        appParameter.add(us);

        // Also set on the REAL AppCoreUtils via reflection
        try {
            Class<?> acu = Class.forName("com.mcdonalds.mcdcoreapp.common.util.AppCoreUtils");
            // Find the static ArrayList field
            for (java.lang.reflect.Field f : acu.getDeclaredFields()) {
                if (f.getType() == ArrayList.class) {
                    f.setAccessible(true);
                    f.set(null, appParameter);
                    System.err.println("[HomeHelper] Set AppCoreUtils." + f.getName() + " = " + appParameter.size() + " entries");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[HomeHelper] AppCoreUtils set failed: " + e.getMessage());
        }
    }

    public static String getAppParameter(String key) {
        for (Object o : appParameter) {
            String s = o.toString();
            if (s.startsWith(key + "=")) return s.substring(key.length() + 1);
        }
        return null;
    }

    public Object getOrderType() { return null; }
    public boolean isDigitalOfferSupported() { return false; }
    public boolean isShouldCheckAppUpgrade() { return false; }
    public boolean isShowDriveAlertDialog() { return false; }
    public void saveStoreName(Object restaurant) {}
    public static void setOrderTypeFromConfig() {}
    
    // getCurrentMarketMap returns the parsed config as ArrayList
    public static ArrayList<Object> getCurrentMarketMap() { return appParameter; }
}
