package com.hanly.app.config;

/**
 * Created by HanLingye on 2017/3/30.
 */

public interface ConfigRequest {

    void setResultHandler(RequestResultHandler handler);

    void execute(String url);
}
