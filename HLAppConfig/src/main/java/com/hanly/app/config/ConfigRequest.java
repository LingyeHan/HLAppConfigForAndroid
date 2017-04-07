package com.hanly.app.config;

import org.json.JSONObject;

/**
 * Created by HanLingye on 2017/3/30.
 */

public interface ConfigRequest {

    void setResultHandler(RequestResultHandler handler);

    void fetch(String url);

    void update(String url, JSONObject jsonObject);
}
