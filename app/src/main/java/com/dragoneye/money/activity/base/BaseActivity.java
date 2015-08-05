package com.dragoneye.money.activity.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.dragoneye.money.application.MyApplication;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
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
