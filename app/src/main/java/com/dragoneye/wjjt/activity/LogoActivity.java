package com.dragoneye.wjjt.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;

import cn.smssdk.SMSSDK;

public class LogoActivity extends BaseActivity {

    private SharedPreferences preferences;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        handler.postDelayed(startNextActivity_r, 3000);
        getSupportActionBar().hide();
        UmengUpdateAgent.update(this);
        SMSSDK.initSDK(this,PreferencesConfig.SHARESDKAPPKEY, PreferencesConfig.SHARESDKAPPSECRET );
    }

    Runnable startNextActivity_r = new Runnable() {
        @Override
        public void run() {
            Intent intent;
            if(preferences.getBoolean(PreferencesConfig.IS_SHOWED_FEATURE, false)){
                intent = new Intent(LogoActivity.this, LoginActivity.class);
            }else {
                intent = new Intent(LogoActivity.this, FeatureActivity.class);
            }
            startActivity(intent);
            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
