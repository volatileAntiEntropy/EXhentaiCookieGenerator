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
    private Map<String,String> cookies;
    private int status;
    private boolean hasJoined;
    public final static int FAIL=2;
    public final static int SUCCESS=1;
    public final static int NOT_FINISH=0;

    public ExCookieGenerator(){
        super();
        response=null;
        key=null;
        cookies=new HashMap<>(3);
        status=0;
        hasJoined=false;
    }

    @Override
    public void run(){
        try {
            String url="https://panda.gxtel.com/panda.js?" + System.currentTimeMillis();
            response = Jsoup.connect(url.substring(0, url.lastIndexOf('/')) + "/exkey-public?" + System.currentTimeMillis()).method(Connection.Method.GET).ignoreContentType(true).execute();
        }catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException","response failure",e);
        }
        if(response!=null){
            key=response.body().replaceAll("[\r\n]","");
            Log.d("KeyGot",key);
            String[] result=key.split("x");
            cookies.put("ipb_member_id",result[0].substring(32));
            cookies.put("ipb_pass_hash",result[0].substring(0,32));
            cookies.put("igenous","");
            if(result.length==2){
                cookies.remove("igenous");
                cookies.put("igenous",result[1]);
            }
            status=1;
        }
        else{
            status=2;
        }
    }

    public int getStatus(){
        return status;
    }

    public String getExKey(){
        return key;
    }

    public Map<String,String> getCookies(){ hasJoined=true; return cookies; }

    public boolean hasJoined(){
        return hasJoined;
    }
}
