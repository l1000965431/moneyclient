package com.dragoneye.money.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.DotViewPagerActivity;
import com.dragoneye.money.config.HttpUrlConfig;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FeatureActivity extends DotViewPagerActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsUseImageLoader(false);
        setContentView(R.layout.activity_feature);
        setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        getSupportActionBar().hide();
    }

    @Override
    protected void initViewPager(){
        final ImageView imageView = (ImageView)findViewById(R.id.feature_iv_start);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeatureActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mDotViewPager = (DotViewPager)findViewById(R.id.feature_dot_viewpager);
        mDotViewPager.setAutoScroll(false);
        mDotViewPager.setPageChangeListener(new DotViewPager.OnViewPageChangeListener() {
            @Override
            public void onPageScrolled(int position) {
                if (position == mImageUrl.size() - 1) {
                    imageView.setVisibility(View.VISIBLE);
                }else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onViewClick(int position){

    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();

        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.characteristic_page_1).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.characteristic_page_2).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.characteristic_page_3).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.characteristic_page_4).toString());
    }
}
