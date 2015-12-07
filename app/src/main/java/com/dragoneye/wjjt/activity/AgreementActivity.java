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
        intent.putExtra("title", activity.getString(R.string.title_activity_agreement));
        activity.startActivity(intent);
    }

    public static void OpenDevStuff(Activity activity){
        Intent intent = new Intent(activity, AgreementActivity.class);
        intent.putExtra("url", HttpUrlConfig.URL_DEV_STUFF);
        intent.putExtra("title", activity.getString(R.string.title_activity_developer_agreement));
        activity.startActivity(intent);
    }

    public static void OpenUrl(Activity activity, String url, String title){
        Intent intent = new Intent(activity, AgreementActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        mWebView = (WebView)findViewById(R.id.agreement_wv_main);
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        setTitle(title);
        mWebView.loadUrl(url);
    }
}
