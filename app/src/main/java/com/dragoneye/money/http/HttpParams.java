package com.dragoneye.money.http;

import com.dragoneye.money.tool.ToolMaster;
import com.loopj.android.http.RequestParams;

/**
 * Created by liumin on 15/3/30.
 */
public class HttpParams extends RequestParams {

    public void putGsonData( Object ObjectParams ){
        String ObjectJson = ToolMaster.gsonInstance().toJson(ObjectParams);
        this.put( "data",ObjectJson );
    }

    public void putJsonData( Object object ){
        this.put( "data", object );
    }
}

