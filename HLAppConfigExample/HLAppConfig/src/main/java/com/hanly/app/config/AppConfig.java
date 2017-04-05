package com.hanly.app.config;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by HanLingye on 2017/3/29.
 */
public class AppConfig {

    private static final String TAG = AppConfig.class.getSimpleName();

    private static AppConfigManager configManager;

    private static AppConfigManager getConfigManager() {
        if (configManager == null) {
            throw new RuntimeException("Not started configuration.");
        }
        return configManager;
    }

    public static void start(String url, Context context) {
        AppConfig.start(url, null, context);
    }

    public static void start(String url, String localFile, Context context) {
        AppConfig.start(url, localFile, null, context);
    }

    public static void start(String url, String localFile, ConfigRequest configRequest, Context context) {
        if (configManager != null) {
            return;
        }

        configManager = new AppConfigManager(url, localFile, context);
        configManager.setConfigRequest(configRequest);
        configManager.loadLocalConfigs();
    }

    public static void reload() {
        getConfigManager().loadRemoteConfigs();
    }

    public static void userUpdate(String url, JSONObject jsonObject) {
        getConfigManager().userUpdate(url, jsonObject);
    }

    public static Object getObject(String key) {
        return getObject(key, null);
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    ///

    public static Object getObject(String key, Object defaultValue) {
        Object value = valueObjectForKey(key);
        return value != null ? value : defaultValue;
    }

    public static String getString(String key, String defaultValue) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof String) ? (String) value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof Integer) ? ((Integer) value).intValue() : defaultValue;
    }

    public static double getDouble(String key, double defaultValue) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof Double) ? ((Double) value).doubleValue() : defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof Boolean) ? ((Boolean) value).booleanValue() : defaultValue;
    }

    /// Private Method
    private static Object valueObjectForKey(String key) {
        Object value = getConfigManager().getConfigModel().valueObjectForKey(key);
        if (value == null) {
            value = getConfigManager().getDefaultConfigModel().valueObjectForKey(key);
        }
        return value;
    }
}
