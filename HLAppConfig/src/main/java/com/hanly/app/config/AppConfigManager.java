package com.hanly.app.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by HanLingye on 2017/3/29.
 */

public class AppConfigManager {

    private static final String TAG = AppConfigManager.class.getSimpleName();

    private final Object lock = new Object();

    private final AppConfigSettings configSettings;
    private final ConfigRequest configRequest;
    private final ConfigStore configStore;

    private AppConfigModel configModel;
    private AppConfigModel defaultConfigModel;


    public AppConfigManager(AppConfigSettings configSettings, Context context) {
        this.configSettings = configSettings;
        if (configSettings.getConfigRequest() == null) {
            this.configRequest = new DefaultConfigRequest();
        } else {
            this.configRequest = configSettings.getConfigRequest();
        }
        this.configStore = new ConfigFileStore(configSettings.getLocalFile(), context);
    }

    public void loadLocalConfigs() {
        Object configs = configStore.readConfigs();
        synchronized (lock) {
            configModel = new AppConfigModel(configs);
            Log.d(TAG, "Local ConfigModel: " + configModel);
        }
    }

    public void loadRemoteConfigs() {
        configRequest.setResultHandler(new RequestResultHandler() {

            @Override
            public void handle(Object data) {
                if (data == null) {
                    return;
                }

                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) data;
                    if (jsonObject.getInt("code") != 0) {
                        Log.e(TAG, "The server did not return to the configuration. Response: " + jsonObject);
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

        configRequest.fetch(configSettings.getBaseURL() + configSettings.getFetchPath());
    }

    public void userUpdate(JSONObject jsonObject) {
        configRequest.update(configSettings.getBaseURL() + configSettings.getUpdatePath(), jsonObject);
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
