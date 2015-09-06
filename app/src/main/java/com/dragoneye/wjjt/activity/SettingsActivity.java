package com.dragoneye.wjjt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.AppInfoManager;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.user.CurrentUser;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_instal);
        initView();
        initData();
    }

    private void initView(){
        // 修改密码
        View changePassword = findViewById(R.id.home_self_group_instal_linearLayout7);
        changePassword.setOnClickListener(this);

        // 关于
        View about = findViewById(R.id.home_self_group_instal_linearLayout9);
        about.setOnClickListener(this);

        // 切换账号
        View changeAccount = findViewById(R.id.home_self_group_instal_linearLayout10);
        changeAccount.setOnClickListener(this);

        // 退出
        View exit = findViewById(R.id.linearLayout20);
        exit.setOnClickListener(this);
    }

    private void initData(){

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.home_self_group_instal_linearLayout7: // 修改密码
                onChangePassword();
                break;
            case R.id.home_self_group_instal_linearLayout9: // 关于
                onAbout();
                break;
            case R.id.home_self_group_instal_linearLayout10:// 切换账号
                onChangeAccount();
                break;
            case R.id.linearLayout20:   // 退出
                onExitApplication();
                break;
        }
    }

    private void onExitApplication(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.exit();
                    }
                }).setNegativeButton("取消", null).create();
        alertDialog.setMessage("确定要退出" + AppInfoManager.getApplicationName(this));
        alertDialog.show();
    }

    private void onChangeAccount(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MyApplication) getApplication()).reLogin(SettingsActivity.this);
                    }
                }).setNegativeButton("取消", null).create();
        alertDialog.setMessage("确定要切换账号？");
        alertDialog.show();
    }

    private void onChangePassword(){
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void onAbout(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
