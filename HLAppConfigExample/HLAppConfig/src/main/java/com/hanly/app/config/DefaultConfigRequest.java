package com.hanly.app.config;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by HanLingye on 2017/3/30.
 */

public class DefaultConfigRequest extends AbstractConfigRequest {

    private static final String TAG = DefaultConfigRequest.class.getSimpleName();

    @Override
    public void fetch(String url) {
        new FetchConfigTask().execute(url);
    }

    @Override
    public void update(String url, JSONObject jsonObject) {
        new UpdateConfigTask().execute(url, jsonObject);
    }

    private class FetchConfigTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection httpConnection = null;
            BufferedReader reader = null;
            try {

                httpConnection = makeRequest(params[0], "GET", null, null);

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

    private class UpdateConfigTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {

            HttpURLConnection httpConnection = null;
            BufferedReader reader = null;
            try {
                httpConnection = makeRequest((String) params[0], "POST", null, (JSONObject) params[1]);

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
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                if (jsonObject.getInt("code") != 0) {
                    Log.e(TAG, "The server did not return to the configuration. Response: "+ jsonObject);
                } else {
                    Log.i(TAG, "Update config successfully");
                }
            } catch (Exception e) {
                Log.e(TAG, "JSON format invalid", e);
            }
        }

    }

    public HttpURLConnection makeRequest(String url, String method, String cookie, JSONObject body) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(!method.equals("GET"));
        urlConnection.setRequestMethod(method);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setUseCaches(false);
        urlConnection.setAllowUserInteraction(false);
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        // 设置 Cookie
        if (cookie == null) {
            String cookies = CookieManager.getInstance().getCookie(urlConnection.getURL().getHost());
            if (cookies != null)
                urlConnection.setRequestProperty("Cookie", cookies);
        } else {
            Log.w(TAG, "Cookie is null, so no set Cookie");
        }

        // Create Body
        if (body != null) {
            OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(buildPostParameters(body));
            writer.flush();
            writer.close();
            outputStream.close();
        }

        urlConnection.connect();

        return urlConnection;
    }

    public String buildPostParameters(Object content) {
        String output = null;
        if ((content instanceof String) ||
                (content instanceof JSONObject) ||
                (content instanceof JSONArray)) {
            output = content.toString();
        } else if (content instanceof Map) {
            Uri.Builder builder = new Uri.Builder();
            HashMap hashMap = (HashMap) content;
            if (hashMap != null) {
                Iterator entries = hashMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove(); // avoids a ConcurrentModificationException
                }
                output = builder.build().getEncodedQuery();
            }
        }

        return output;
    }
}
