package com.dragoneye.wjjt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.config.HttpUrlConfig;

public class AgreementActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        mWebView = (WebView)findViewById(R.id.agreement_wv_main);
        mWebView.loadUrl(HttpUrlConfig.URL_AGREEMENT);
    }

    public static void CallThisActivity(Context context){
        Intent intent = new Intent(context, AgreementActivity.class);
        context.startActivity(intent);
    }
}
