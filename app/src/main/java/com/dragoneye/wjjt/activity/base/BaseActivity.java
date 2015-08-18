package com.dragoneye.wjjt.activity.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.user.UserBase;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by happysky on 15-6-16.
 * Activity 基类
 */
public class BaseActivity extends ActionBarActivity {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentUser", ((MyApplication)getApplication()).getCurrentUser(this));
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
}
