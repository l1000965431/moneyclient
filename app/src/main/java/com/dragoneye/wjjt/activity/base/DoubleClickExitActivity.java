package com.dragoneye.wjjt.activity.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.dragoneye.wjjt.application.AppInfoManager;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;

/**
 * Created by happysky on 15-8-13.
 */
public class DoubleClickExitActivity extends BaseActivity {
    protected Handler handler = new Handler();

    private boolean readyToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setTitle(AppInfoManager.getApplicationName(this));
                //+ " - " + ((MyApplication)getApplication()).getCurrentUser.getCurrentUser(this).getUserTypeString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if( readyToExit ){
                finish();
            }else {
                readyToExit = true;
                UIHelper.toast(this, "再按一次退出" + AppInfoManager.getApplicationName(this));
                handler.postDelayed(cancelExit, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Runnable cancelExit = new Runnable() {
        @Override
        public void run() {
            readyToExit = false;
        }
    };
}
