package com.hanly.app.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * Created by HanLingye on 2017/3/29.
 */

public class ConfigFileStore implements ConfigStore {

    private static final String TAG = ConfigFileStore.class.getSimpleName();

    private String filename;
    private Context context;

    public ConfigFileStore(String filename, Context context) {
        this.filename = filename == null ? "HLAppConfig.json" : filename;
        this.context = context;
    }

    public Object readDefaultConfigs() {
        Object object = null;
        try {
            object = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            Log.e(TAG, "Error in Reading: " + e.getLocalizedMessage());
        }
        return object;
    }

    public Object readConfigs() {
        Object object = null;
        try {
            String content = null;

            StringBuilder sb = new StringBuilder();
            InputStream inputStream = context.openFileInput(filename);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                inputStream.close();
            }

            if (sb.length() > 0) {
                content = sb.toString();
            } else {
                content = loadJSONFromAsset();
            }
            object = new JSONObject(content);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "FileNotFound: " + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error in Reading: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error in Reading: " + e.getLocalizedMessage());
        }
        return object;
    }

    public void writeConfigs(Object configs) {
        try {
            Writer output = new BufferedWriter(new FileWriter(context.getFilesDir().getPath() + "/" + filename));
            output.write(configs.toString());
            output.flush();
            output.close();
        } catch (IOException e) {
            Log.e(TAG, "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "Open JSON file from assets: " + e.getLocalizedMessage());
        }
        return json;
    }


}
