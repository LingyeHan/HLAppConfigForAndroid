package com.hanly.app.config;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by HanLingye on 2017/3/29.
 */

public class AppConfigModel {

    private static final String TAG = AppConfigModel.class.getSimpleName();
    private static final String META_KEY = "meta";
    private static final String CONFIG_KEY = "configs";

    private String version;
    private JSONObject meta;
    private JSONObject configs;

    public AppConfigModel(Object object) {
        if (object == null || !(object instanceof JSONObject)) {
            return;
        }
        try {
            JSONObject jsonObject = (JSONObject) object;
            this.meta = jsonObject.getJSONObject(META_KEY);
            this.setConfigs(jsonObject.get(CONFIG_KEY));

            this.version = this.meta != null ? this.meta.getString("version") : "";
        } catch (JSONException e) {
            Log.e(TAG, "HLAppConfigModel init JSON Exception jsonObject: " + object, e);
        }
    }

    public String getVersion() {
        return this.version;
    }

    public Object valueObjectForKey(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        if (this.configs == null) {
            return null;
        }

        Object result = null;
//        try {
            if (!key.contains(".")) {
                result = this.configs.opt(key);
            } else {

                // key 支持 "." 语法处理
                Object next = this.configs;
                String[] keys = key.split("\\.");
                for (String k : keys) {
                    if (!(next instanceof JSONObject)) {
                        break;
                    }

                    next = ((JSONObject) next).opt(k);
                    if (next == null) {
                        break;
                    }

                    result = next;
                }
            }
//        } catch (JSONException e) {
//            Log.e(TAG, "JSON Exception. key: " + key, e);
//        }

        return result;
    }

//    public Object JSONObjectForKey(String key) {
//        try {
//            return valueObjectForKey(key).get(key);
//        } catch (JSONException e) {
//            Log.e(TAG, "SON Exception. jsonObject: " + key, e);
//        }
//        return null;
//    }

//    public List arrayForKey(String key) {
//        try {
//        return valueObjectForKey(key).getJSONArray(key);
//    } catch (JSONException e) {
//        Log.e(TAG, "HLAppConfigModel get configs JSON Exception. jsonObject: " + key, e);
//    }
//        return null;
//    }

//    public URL URLForKey(String key) {
//        NSURL * url = [self valueObjectForKey:key class:[NSURL class]];
//        if (!url) {
//            NSString * stringValue = [self stringForKey:key];
//            url = [NSURL URLWithString:[stringValue stringByAddingPercentEscapesUsingEncoding:
//            NSUTF8StringEncoding]];
//        }
//
//        return url;
//    }

    public String stringForKey(String key) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof String)? (String) value : null;
    }

    public Integer integerForKey(String key) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof Integer)? (Integer) value : null;
    }

    public Double doubleForKey(String key) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof Double)? (Double) value : null;
    }

    public Boolean boolForKey(String key) {
        Object value = valueObjectForKey(key);
        return value != null && (value instanceof Boolean)? (Boolean) value : null;
    }

    @Override
    public String toString() {
        return "<HLAppConfigModel> [ meta: " + this.meta + ", configs: " + this.configs + " ]";
    }

    @Override
    public int hashCode() {
        return this.configs.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AppConfigModel)) {
            return false;
        }

        AppConfigModel other = (AppConfigModel) obj;
        if (this.configs != null && this.configs.equals(other.configs)) {
            return true;
        }

        return false;
    }


    public void setConfigs(Object configObj) {
        if (configObj instanceof JSONObject) {
            this.configs = (JSONObject) configObj;
        } else if (configObj instanceof JSONArray) {
            JSONObject configResult = new JSONObject();
            try {
                JSONArray jsonArray = (JSONArray) configObj;
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object obj = jsonArray.get(i);
                    if (obj instanceof JSONObject) {
                        JSONObject jo = (JSONObject) obj;
                        Iterator<String> keys = jo.keys();
                        while (keys.hasNext()) {
                            String name = keys.next();
                            configResult.put(name, jo.get(name));
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "HLAppConfigModel set configs JSON Exception. jsonObject: " + configObj, e);
            }

            this.configs = configResult;
        }
    }

}
