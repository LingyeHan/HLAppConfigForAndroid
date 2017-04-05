package com.hanly.app.config;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HanLingye on 2017/3/30.
 */

public class DefaultConfigRequest extends AbstractConfigRequest {

    private static final String TAG = DefaultConfigRequest.class.getSimpleName();

    @Override
    public void execute(String url) {
        new RequestJsonConfigTask().execute(url);
    }

    private class RequestJsonConfigTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection httpConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);

                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpConnection.setRequestProperty("u", "1189008354778");
                // 设置 Cookie
                String cookie = CookieManager.getInstance().getCookie(httpConnection.getURL().getHost());
                if (cookie.length() > 0) {
                    httpConnection.setRequestProperty("Cookie", cookie);
                } else {
                    Log.w(TAG, "Cookie is null, so no set Cookie");
                }
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(10000);
                httpConnection.setReadTimeout(10000);
                httpConnection.connect();

                int statusCode = httpConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Request failed. statusCode: " + statusCode + ", message: " + httpConnection.getResponseMessage());
                    return null;
                }

                InputStream inputStream = httpConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                return buffer.toString();
            } catch (IOException ioe) {
                Log.e(TAG, "Request server config error", ioe);
            } catch (Exception e) {
                Log.e(TAG, "Request server config error", e);
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            handleResult(result);
        }
    }
}
