package com.qinbi.exkeygenerator;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ExCookieGenerator extends Thread {
    private Connection.Response response;
    private String key;
    private String pandaUrl;
    private Map<String, String> cookies;
    private int status;
    public final static int FAIL = 2;
    public final static int SUCCESS = 1;
    public final static int NOT_FINISH = 0;
    public final static int URL_INCORRECT = 3;
    public final static int DATA_CAPACITY = 5;

    public ExCookieGenerator(String url) {
        super();
        response = null;
        key = null;
        cookies = new HashMap<>(DATA_CAPACITY);
        pandaUrl = url;
        status = 0;
    }

    @Override
    public void run() {
        String url = pandaUrl + "/panda.js?" + System.currentTimeMillis();
        try {
            response = Jsoup.connect(url.substring(0, url.lastIndexOf('/')) + "/exkey-public?" + System.currentTimeMillis())
                    .method(Connection.Method.GET)
                    .ignoreContentType(true).execute();
        } catch (IOException e) {
            status = FAIL;
            e.printStackTrace();
            Log.e("IOException", "Response Failure", e);
            return;
        } catch (IllegalArgumentException iae) {
            status = URL_INCORRECT;
            iae.printStackTrace();
            Log.e("IlleagalArgumentException", "URL Incorrect", iae);
            return;
        }
        if (response != null) {
            key = response.body().replaceAll("[\r\n]", "");
            Log.i("KeyGot", key);
            if (!(key.contains("<") || key.contains(">"))) {
                String[] result = key.split("x");
                cookies.put("URL", url);
                cookies.put("raw_key", key);
                cookies.put("ipb_member_id", result[0].substring(32));
                cookies.put("ipb_pass_hash", result[0].substring(0, 32));
                cookies.put("igenous", (result.length == 2) ? (result[1]) : (""));
                status = SUCCESS;
            } else {
                status = URL_INCORRECT;
            }
        } else {
            status = FAIL;
        }
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
