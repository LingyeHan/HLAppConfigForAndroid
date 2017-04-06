package com.hanly.app.config;

/**
 * Created by HanLingye on 2017/4/6.
 */

public class AppConfigSettings {

    private String baseURL;

    private String fetchPath;

    private String updatePath;

    private String localFile;

    private ConfigRequest configRequest;

    public AppConfigSettings(String baseURL, String fetchPath, String updatePath) {
        this.baseURL = baseURL;
        this.fetchPath = fetchPath;
        this.updatePath = updatePath;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getFetchPath() {
        return fetchPath;
    }

    public void setFetchPath(String fetchPath) {
        this.fetchPath = fetchPath;
    }

    public String getUpdatePath() {
        return updatePath;
    }

    public void setUpdatePath(String updatePath) {
        this.updatePath = updatePath;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public ConfigRequest getConfigRequest() {
        return configRequest;
    }

    public void setConfigRequest(ConfigRequest configRequest) {
        this.configRequest = configRequest;
    }
}
