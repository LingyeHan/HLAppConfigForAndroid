package com.hanly.app.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by HanLingye on 2017/3/29.
 */

public class AppConfigManager {

    private static final String TAG = AppConfigManager.class.getSimpleName();

    private Object lock = new Object();

    private String baseURL;

    private AppConfigModel configModel;
    private AppConfigModel defaultConfigModel;

    private ConfigRequest configRequest;
    private ConfigStore configStore;

    public void setConfigRequest(ConfigRequest configRequest) {
        this.configRequest = configRequest;
    }

    public ConfigRequest getConfigRequest() {
        return configRequest = configRequest != null ? configRequest : new DefaultConfigRequest();
    }

    public AppConfigManager(String baseURL, String localFile, Context context) {
        this.baseURL = baseURL;
        configStore = new ConfigFileStore(localFile, context);
    }

    public void loadLocalConfigs() {
        Object configs = configStore.readConfigs();
        synchronized (lock) {
            configModel = new AppConfigModel(configs);
            Log.d(TAG, "Local ConfigModel: " + configModel);
        }
    }

    public void loadRemoteConfigs() {
        getConfigRequest().setResultHandler(new RequestResultHandler() {

            @Override
            public void handle(Object data) {
                if (data == null) {
                    return;
                }

                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) data;
                    if (jsonObject.getInt("code") != 0) {
                        Log.e(TAG, "The server did not return to the configuration. Response: "+ jsonObject);
                        return;
                    }
                    synchronized (lock) {
                        configModel = new AppConfigModel(jsonObject.getJSONObject("result"));
                        Log.d(TAG, "Remote ConfigModel: " + configModel);
                    }
                    configStore.writeConfigs(jsonObject.getJSONObject("result"));
                } catch (Exception e) {
                    Log.e(TAG, "JSON format invalid", e);
                }
            }
        });

        configRequest.execute(this.baseURL);
    }

    public AppConfigModel getConfigModel() {
        synchronized (lock) {
            return configModel;
        }
    }

    public AppConfigModel getDefaultConfigModel() {
        if (defaultConfigModel == null) {
            defaultConfigModel = new AppConfigModel(configStore.readDefaultConfigs());
        }
        return defaultConfigModel;
    }
}
