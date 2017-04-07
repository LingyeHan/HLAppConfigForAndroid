package com.hanly.app.config;

/**
 * Created by HanLingye on 2017/3/29.
 */

public interface ConfigStore {

    Object readConfigs();

    Object readDefaultConfigs();

    void writeConfigs(Object configs);
}
