package com.dragoneye.wjjt.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.DotViewPagerActivity;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.view.DotViewPager;

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
                PreferenceManager.getDefaultSharedPreferences(FeatureActivity.this).edit().putBoolean(
                        PreferencesConfig.IS_SHOWED_FEATURE, true
                ).apply();
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
                } else {
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

        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.wjjt/" + R.mipmap.characteristic_page_1).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.wjjt/" + R.mipmap.characteristic_page_2).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.wjjt/" + R.mipmap.characteristic_page_3).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.wjjt/" + R.mipmap.characteristic_page_4).toString());
    }

    public static void CallFeatureActivity(Context context){
        Intent intent = new Intent(context, FeatureActivity.class);
        context.startActivity(intent);
    }
}
