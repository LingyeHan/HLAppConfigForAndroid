package com.hanly.app.config;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HanLingye on 2017/4/1.
 */

public abstract class AbstractConfigRequest implements ConfigRequest {

    private RequestResultHandler resultHandler;

    public void setResultHandler(RequestResultHandler handler) {
        this.resultHandler = handler;
    }

    protected void handleResult(String result) {
        if (result == null) {
            return;
        }
        Log.i("Response data result", result);

        Object object = null;
        try {
            object = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        resultHandler.handle(object);
    }
}
