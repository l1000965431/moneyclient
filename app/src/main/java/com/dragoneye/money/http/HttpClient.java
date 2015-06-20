package com.dragoneye.money.http;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by liumin on 15/3/29.
 */
public class HttpClient {

    private  static AsyncHttpClient client =new AsyncHttpClient();

    static
    {
        client.setTimeout( 3000 );
    }


    public static void get ( String strHttpUrl, HttpParams params, AsyncHttpResponseHandler res ){
        client.get(strHttpUrl, params, res);
    }

    public static void post( String strHttpUrl, HttpParams params, AsyncHttpResponseHandler res ){
        client.post(strHttpUrl, params, res);
    }


}
