package com.dragoneye.money.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.BaseActivity;
import com.dragoneye.money.application.AppInfoManager;
import com.dragoneye.money.application.MyApplication;

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
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        MyApplication.exit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
