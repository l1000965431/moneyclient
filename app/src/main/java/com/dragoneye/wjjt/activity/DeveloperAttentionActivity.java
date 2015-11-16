package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.AppInfoManager;

/**
 * Created by happysky on 15-9-18.
 *
 */
public class DeveloperAttentionActivity extends BaseActivity {
    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, DeveloperAttentionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_attention);
    }
}
