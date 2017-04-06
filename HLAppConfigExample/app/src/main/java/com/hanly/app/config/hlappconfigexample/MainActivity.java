package com.hanly.app.config.hlappconfigexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;

import com.hanly.app.config.AppConfig;
import com.hanly.app.config.AppConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }).start();

        AppConfigSettings configSettings = new AppConfigSettings("https://test.zuifuli.io/api/duncan/v1/app/config", "/fetch?v=1.0.0", "/userUpdate");
        AppConfig.start(configSettings, getApplicationContext());

        final Button reloadBtn = (Button) findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppConfig.reload();
                try {
                    Thread.currentThread().sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JSONObject body = null;
                try {
                    body = new JSONObject("{\"version\":\"1.0.0\",\"ckey\":\"user_order\",\"value\":\"1,4,3,2\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppConfig.userUpdate("https://test.zuifuli.io/api/duncan/v1/app/config/userUpdate", body);
            }
        });

        final Button testGetCfgBtn = (Button) findViewById(R.id.testGetCfgBtn);
        testGetCfgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Object obj1 = AppConfig.getObject("app_ui2");
                Log.d(TAG, "value: " + obj1);
                String str1 = AppConfig.getString("app_ui2.theme.colors.text");
                Log.d(TAG, "value: " + str1);
                String str2 = AppConfig.getString("all_text_content.sodis_prepaid_tips");
                Log.d(TAG, "value: " + str2);
                String str3 = AppConfig.getString("user_text");
                Log.d(TAG, "user_text value: " + str3);
                int int1 = AppConfig.getInt("all_text_content.red_list_max_year");
                Log.d(TAG, "value: " + int1);
                double d1 = AppConfig.getDouble("app_company.doubleKey");
                Log.d(TAG, "value: " + d1);
                boolean b1 = AppConfig.getBoolean("app_company.boolKey");
                Log.d(TAG, "value: " + b1);

                String s4 = AppConfig.getString("company_text");
                Log.d(TAG, "jsonKey value: " + s4);
            }
        });
    }

    public void login() {
        HttpURLConnection httpConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://test.zuifuli.io/api/customer/v1/account/login");

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            httpConnection.setUseCaches(false);
//            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(10000);
            httpConnection.connect();

            String jsonStr = "{\"phone\":\"15027877580\",\"passwd\":\"123456\"}";
            byte[] outputInBytes = jsonStr.getBytes("UTF-8");
            OutputStream os = httpConnection.getOutputStream();
            os.write(outputInBytes);
            os.flush();
            os.close();

//                    if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                        throw new RuntimeException("Failed : HTTP error code : "
//                                + httpConnection.getResponseCode());
//                    }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            // Save Cookie
            CookieManager msCookieManager = CookieManager.getInstance();
            msCookieManager.setAcceptCookie(true);

            Map<String, List<String>> headerFields = httpConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
//                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    msCookieManager.setCookie(httpConnection.getURL().getHost(), cookie);
                }
            }
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
    }
}
