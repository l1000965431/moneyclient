package com.dragoneye.money.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.BaseActivity;
import com.dragoneye.money.config.PreferencesConfig;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

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
