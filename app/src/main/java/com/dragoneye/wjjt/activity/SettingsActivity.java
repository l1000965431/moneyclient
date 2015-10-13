package com.dragoneye.wjjt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.AppInfoManager;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.user.CurrentUser;

import org.apache.http.Header;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    View mLLWxBind;
    TextView mTVWxBind;

    Handler handler = new Handler();

    private boolean mIsWxBind = false;

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

        // 微信绑定
        mLLWxBind = findViewById(R.id.home_self_group_instal_wx);
        mLLWxBind.setVisibility(View.GONE);
        mLLWxBind.setOnClickListener(this);

        mTVWxBind = (TextView)findViewById(R.id.home_self_group_instal_tv_wx_bind);
    }

    private void initData(){

    }

    @Override
    public void onResume(){
        super.onResume();
//        handler.post(checkIsBindWeChat_r);
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
            case R.id.home_self_group_instal_wx:
                onWxBind();
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

    private void onWxBind(){
        if(mIsWxBind){

        }else {
            WxBindActivity.CallActivity(this);
        }
    }

    Runnable checkIsBindWeChat_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication)getApplication()).getCurrentUser(SettingsActivity.this).getUserId());

            HttpClient.atomicPost(SettingsActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/IsBinding", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        finishLoading(false);
                        return;
                    }

                    if (s.compareTo("true") != 0) {
                        mIsWxBind = false;
                        mLLWxBind.setVisibility(View.VISIBLE);
                        mTVWxBind.setText("绑定微信");
                    } else {
                        mIsWxBind = true;
                        mLLWxBind.setVisibility(View.VISIBLE);
                        mTVWxBind.setText("解除微信绑定");
                    }
                }
            });
        }
    };
}
