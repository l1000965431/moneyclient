package com.dragoneye.wjjt.activity.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.user.UserBase;
import com.dragoneye.wjjt.view.ActivityLoadingProxy;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by happysky on 15-6-16.
 * Activity 基类
 */
public class BaseActivity extends ActionBarActivity {

    private ActivityLoadingProxy loadingProxy;
    private View mRootView;
    private boolean mIsNeedLoadingFeature = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MyApplication.addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if( savedInstanceState != null ){
            UserBase userBase = (UserBase)savedInstanceState.getSerializable("currentUser");
            ((MyApplication)getApplication()).setCurrentUser(this, userBase);
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.home_red)));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.home_red));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentUser", ((MyApplication) getApplication()).getCurrentUser(this));
    }

    @Override
    public void onResume(){
        super.onResume();

        // 友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause(){
        super.onPause();

        // 友盟统计
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int resId){
        if( mIsNeedLoadingFeature ){
            LinearLayout rootLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            rootLayout.setLayoutParams(layoutParams);
            rootLayout.setOrientation(LinearLayout.VERTICAL);

            loadingProxy = new ActivityLoadingProxy();
            loadingProxy.createView(this, null);
            rootLayout.addView(loadingProxy.getView(), layoutParams);
            loadingProxy.getView().setVisibility(View.GONE);
            loadingProxy.setOnRetryListener(new ActivityLoadingProxy.OnRetryListener() {
                @Override
                public void onRetry() {
                    onRetryLoading();
                }
            });

            mRootView = LayoutInflater.from(this).inflate(resId, null);
            rootLayout.addView(mRootView, layoutParams);
            super.setContentView(rootLayout);
        }else {
            super.setContentView(resId);
        }
    }

    public void setIsNeedLoadingFeature(boolean b){
        mIsNeedLoadingFeature = b;
    }

    public void setStartLoading(){
        loadingProxy.getView().setVisibility(View.VISIBLE);
        mRootView.setVisibility(View.GONE);
        loadingProxy.setLoading();
    }

    public void finishLoading(boolean isLoadingSuccess){
        if(isLoadingSuccess){
            loadingProxy.getView().setVisibility(View.GONE);
            mRootView.setVisibility(View.VISIBLE);
            AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
            aa.setDuration(700);
            mRootView.startAnimation(aa);
        }else {
            loadingProxy.getView().setVisibility(View.VISIBLE);
            loadingProxy.setLoadingFailed();
        }
    }

    protected void onRetryLoading(){

    }

}
