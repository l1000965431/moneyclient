package com.dragoneye.money.http;


import android.content.Context;

import com.dragoneye.money.application.AppInfoManager;
import com.dragoneye.money.config.HttpProtocolConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by liumin on 15/3/29.
 */
public class HttpClient {

    private  static AsyncHttpClient client =new AsyncHttpClient();

    static
    {
        client.setTimeout( 3000 );
    }

    public static AsyncHttpClient getClient(){
        return client;
    }

    public static void initHttpClient(Context context){
        client.addHeader(HttpProtocolConfig.HEADER_CLIENT_TYPE, "MOBILE");
        client.addHeader(HttpProtocolConfig.HEADER_CLIENT_OS, "Android");
        client.addHeader(HttpProtocolConfig.HEADER_CLIENT_VERSION, String.valueOf(AppInfoManager.getAppVersionCode(context)));
        client.addHeader(HttpProtocolConfig.HEADER_CLIENT_TOKEN, "this is token");
        client.addHeader(HttpProtocolConfig.HEADER_CLIENT_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    }


    public static void get ( String strHttpUrl, HttpParams params, AsyncHttpResponseHandler res ){
        client.get(strHttpUrl, params, res);
    }

    public static void post( String strHttpUrl, HttpParams params, AsyncHttpResponseHandler res ){
        client.post(strHttpUrl, params, res);
    }

    public static String getValueFromHeader(Header[] headers, String key){
        for(Header header : headers){
            if(header.getName().compareTo(key) == 0){
                return header.getValue();
            }
        }
        return null;
    }

}
