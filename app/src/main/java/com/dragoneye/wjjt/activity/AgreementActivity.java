package com.dragoneye.wjjt.activity;

import android.app.Activity;
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

    public static void OpenAgreement(Activity activity){
        Intent intent = new Intent(activity, AgreementActivity.class);
        intent.putExtra("url", HttpUrlConfig.URL_AGREEMENT);
        activity.startActivity(intent);
    }

    public static void OpenDevStuff(Activity activity){
        Intent intent = new Intent(activity, AgreementActivity.class);
        intent.putExtra("url", HttpUrlConfig.URL_DEV_STUFF);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        mWebView = (WebView)findViewById(R.id.agreement_wv_main);
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }
}
