package com.dragoneye.wjjt.http;


import android.content.Context;
import android.os.Handler;

import com.dragoneye.wjjt.application.AppInfoManager;
import com.dragoneye.wjjt.config.HttpProtocolConfig;
import com.dragoneye.wjjt.tool.UIHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

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

        handler.postDelayed(clearTimeOutUrl_r, 0);
    }


    public static void get ( String strHttpUrl, HttpParams params, AsyncHttpResponseHandler res ){
        client.get(strHttpUrl, params, res);
    }

    public static void post( String strHttpUrl, HttpParams params, AsyncHttpResponseHandler res ){
        client.post(strHttpUrl, params, res);
    }

    private static ArrayList<String> mPostingUrl = new ArrayList<>();


    static Handler handler = new Handler();
    static Runnable clearTimeOutUrl_r = new Runnable() {
        @Override
        public void run() {
            if(mPostingUrl.size() > 0){
                mPostingUrl.remove(0);
            }
        }
    };

    public static void atomicPost( final Context context, final String strHttpUrl, HttpParams params, final MyHttpHandler myHttpHandler){
        if( mPostingUrl.contains(strHttpUrl) ){
            UIHelper.toast(context, "正在访问，请稍后");
            return;
        }

        mPostingUrl.add(strHttpUrl);
        client.post(strHttpUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                UIHelper.toast(context, "无法连接到服务器，请稍后再试");
                myHttpHandler.onFailure(i, headers, s, throwable);
                mPostingUrl.remove(strHttpUrl);
                return;
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                mPostingUrl.remove(strHttpUrl);
                myHttpHandler.onSuccess(i, headers, s);
            }
        });
        handler.postDelayed(clearTimeOutUrl_r, 100000);
    }

    public static abstract class MyHttpHandler{
        public void onFailure(int i, Header[] headers, String s, Throwable throwable){

        }

        public abstract void onSuccess(int i, Header[] headers, String s);
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
