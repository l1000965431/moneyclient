package com.dragoneye.wjjt.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;

import org.apache.http.Header;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    View mLLWxBind;
    TextView mTVWxBind;
    View mLLAlipayBind;
    TextView mTVAlipayBind;

    Handler handler = new Handler();

    private boolean mIsWxBind = false;
    private boolean mIsAlipayBind = false;

    ProgressDialog progressDialog;

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
//        mLLWxBind.setVisibility(View.GONE);
        mLLWxBind.setOnClickListener(this);

        mTVWxBind = (TextView)findViewById(R.id.home_self_group_instal_tv_wx_bind);

        // 支付宝绑定
        mLLAlipayBind = findViewById(R.id.home_self_group_instal_alipay);
//        mLLAlipayBind.setVisibility(View.GONE);
        mLLAlipayBind.setOnClickListener(this);

        mTVAlipayBind = (TextView)findViewById(R.id.home_self_group_instal_tv_alipay_bind);

        if(((MyApplication)getApplication()).getCurrentUser(this).getUserType() == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR) {
            mLLWxBind.setVisibility(View.GONE);
            mTVAlipayBind.setVisibility(View.GONE);
        }
        progressDialog = new ProgressDialog(this);
    }

    private void initData(){

    }

    @Override
    public void onResume(){
        super.onResume();
        handler.post(checkIsBindWeChat_r);
        handler.post(checkIsBindAlipay_r);
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
            case R.id.home_self_group_instal_alipay:
                onAlipayUnbind();
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
            handler.post(unbindWx_r);
        }else {
            UIHelper.toast(this, "您尚未绑定微信支付");
        }
    }

    private void onAlipayUnbind(){
        if(mIsAlipayBind){
            handler.post(unbindAlipay_r);
        }else {
            UIHelper.toast(this, "您尚未绑定支付宝");
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
//                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
//                        finishLoading(false);
                        return;
                    }

                    if (s.compareTo("true") != 0) {
                        mIsWxBind = false;
                    } else {
                        mIsWxBind = true;
                    }
                }
            });
        }
    };

    Runnable checkIsBindAlipay_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication)getApplication()).getCurrentUser(SettingsActivity.this).getUserId());

            HttpClient.atomicPost(SettingsActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/IsalipayBinding", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
//                        finishLoading(false);
                        return;
                    }

                    if(s.compareTo("true") != 0){
                        mIsAlipayBind = false;
                    }else {
                        mIsAlipayBind = true;
//                        finishLoading(true);
                    }
                }
            });
        }
    };

    Runnable unbindAlipay_r = new Runnable() {
        @Override
        public void run() {
            progressDialog.show();

            HttpParams params = new HttpParams();

            String userId = ((MyApplication)getApplication()).getCurrentUser(SettingsActivity.this).getUserId();

            params.put("userId", userId);

            HttpClient.atomicPost(SettingsActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/ClearalipayId", params, new HttpClient.MyHttpHandler() {
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    progressDialog.dismiss();
                    UIHelper.toast(SettingsActivity.this, "连接服务器失败");
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    progressDialog.dismiss();
                    if(s != null && s.compareTo("SUCCESS") == 0){
                        UIHelper.toast(SettingsActivity.this, "解除支付宝绑定成功");
                        finish();
                    }else{
                        UIHelper.toast(SettingsActivity.this, "解除支付宝绑定失败");
                    }
                }
            });
        }
    };

    Runnable unbindWx_r = new Runnable() {
        @Override
        public void run() {
            progressDialog.show();

            HttpParams params = new HttpParams();

            String userId = ((MyApplication)getApplication()).getCurrentUser(SettingsActivity.this).getUserId();

            params.put("userId", userId);

            HttpClient.atomicPost(SettingsActivity.this, HttpUrlConfig.URL_ROOT + "User/ClearOpenId", params, new HttpClient.MyHttpHandler() {
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    progressDialog.dismiss();
                    UIHelper.toast(SettingsActivity.this, "连接服务器失败");
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    progressDialog.dismiss();
                    if(s != null && s.compareTo("SUCCESS") == 0){
                        UIHelper.toast(SettingsActivity.this, "解除微信绑定成功");
                        finish();
                    }else{
                        UIHelper.toast(SettingsActivity.this, "解除微信绑定失败");
                    }
                }
            });
        }
    };
}
