package com.dragoneye.wjjt.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.AppInfoManager;

public class AboutActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_instal_zixin);
        TextView appVersion = (TextView)findViewById(R.id.home_self_group_instal_zixin_p_textViewname);
        appVersion.setText(AppInfoManager.getApplicationName(this) + " " + AppInfoManager.getAppVersionName(this));
        View agreement = findViewById(R.id.home_self_group_instal_zixin_Provision);
        agreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.home_self_group_instal_zixin_Provision:
                AgreementActivity.OpenAgreement(this);
                break;
        }
    }
}
