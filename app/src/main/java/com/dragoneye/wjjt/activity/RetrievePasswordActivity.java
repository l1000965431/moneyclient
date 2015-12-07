package com.dragoneye.wjjt.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.TimeCounterActivity;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.tool.DESCoder;
import com.dragoneye.wjjt.tool.InputChecker;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;

import org.apache.http.Header;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RetrievePasswordActivity extends TimeCounterActivity implements View.OnClickListener{

    private EditText mETUserId;
    private EditText mETNewPassword;
    private EditText mETRepeatPassword;
    private EditText mETCode;
    private TextView mTVSendCode;
    private TextView mTVConfirm;

    private String mPhoneNumber;
    ProgressDialog progressDialog;

    private static final int SEND_CODE_INTERVAL = 60000;

    private Handler handler = new Handler();
    private int tick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_password_back);

        mETUserId = (EditText)findViewById(R.id.retrieve_password_et_userId);
        mETNewPassword = (EditText)findViewById(R.id.retrieve_password_et_newPassword);
        mETRepeatPassword = (EditText)findViewById(R.id.retrieve_password_et_repeatPassword);
        mETCode = (EditText)findViewById(R.id.retrieve_password_et_code);
        mTVSendCode = (TextView)findViewById(R.id.retrieve_password_tv_sendCode);
        mTVSendCode.setOnClickListener(this);
        mTVConfirm = (TextView)findViewById(R.id.retrieve_password_tv_confirm);
        mTVConfirm.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        initSMS();

        initCounter(PreferencesConfig.TIME_COUNTER_SEND_CODE_ACTIVITY, 1000);
        continueCount(SEND_CODE_INTERVAL);
        if(getMillisLeft() > 0){
            setSendCodeButtonEnabled(false, getMillisLeft() / 1000);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.retrieve_password_tv_sendCode:
                onSendCode();
                break;
            case R.id.retrieve_password_tv_confirm:
                onConfirmChange();
                break;
        }
    }

    private void onSendCode(){
        if(!InputChecker.isPhoneNumber(mETUserId.getText().toString())){
            UIHelper.toast(this, "请输入一个正确的手机号码！");
            return;
        }
        startCount(SEND_CODE_INTERVAL);
        setSendCodeButtonEnabled(false, SEND_CODE_INTERVAL / 1000);
        SMSSDK.getVerificationCode("86", mETUserId.getText().toString());
    }

    @Override
    protected void onTick(long millisLeft){
        super.onTick(millisLeft);
        setSendCodeButtonTickText(millisLeft / 1000);
    }

    @Override
    protected void onCountFinished(){
        super.onCountFinished();
        setSendCodeButtonEnabled(true, 0);
    }

    private void setSendCodeButtonEnabled(boolean enabled, long sec){
        if(enabled){
            mTVSendCode.setText("发送验证码");
            mTVSendCode.setOnClickListener(this);
            mTVSendCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded10blue));
        }else {
            setSendCodeButtonTickText(sec);
            mTVSendCode.setOnClickListener(null);
            mTVSendCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded12));
        }
    }

    private void setSendCodeButtonTickText(long sec){
        mTVSendCode.setText(sec + "秒后再次发送");
    }

    private void onConfirmChange(){
        if( !checkUserInput() ){
            return;
        }

        SMSSDK.submitVerificationCode("86", mETUserId.getText().toString(), mETCode.getText().toString());
    }

    Runnable confirmChange_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            HashMap<String, String> dataMap = new HashMap<>();
            String userId = mETUserId.getText().toString();

            dataMap.put("userId", userId);
            dataMap.put("newPassword", mETNewPassword.getText().toString());

            String ObjectJson = ToolMaster.gsonInstance().toJson(dataMap);
            try{
                byte[] data = DESCoder.encrypt(ObjectJson.getBytes(), ToolMaster.getCodeKey(userId));
                params.put("data", DESCoder.encryptBASE64(data));
            }catch (Exception e){
                e.printStackTrace();
            }

            progressDialog.show();
            HttpClient.getClient().addHeader("userId", userId);
            HttpClient.atomicPost(RetrievePasswordActivity.this, HttpUrlConfig.URL_ROOT + "User/RetrievePassword", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    progressDialog.dismiss();
                    HttpClient.getClient().removeHeader("userId");
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    progressDialog.dismiss();
                    HttpClient.getClient().removeHeader("userId");
                    if (s == null) {
                        UIHelper.toast(RetrievePasswordActivity.this, getString(R.string.http_server_exception));
                        return;
                    }
                    onChangeResult(s);
                }
            });
        }
    };

    private void onChangeResult(String result){
        try{
            int resultCode = Integer.parseInt(result);
            switch (resultCode){
                case 0:
                    UIHelper.toast(this, "修改失败");
                    break;
                case 1:
                    UIHelper.toast(this, "找回密码成功");
                    finish();
            }
        }catch (Exception e){
            e.printStackTrace();
            UIHelper.toast(this, "服务器繁忙，请稍后再试");
        }
    }

    private boolean checkUserInput(){
        if(mETUserId.getText().length() == 0){
            UIHelper.toast(this, "请输入账号");
            return false;
        }
        if(mETNewPassword.getText().length() == 0 || mETRepeatPassword.getText().length() == 0){
            UIHelper.toast(this, "请输入新密码");
            return false;
        }
        if(mETNewPassword.getText().toString().compareTo(mETRepeatPassword.getText().toString()) != 0){
            UIHelper.toast(this, "两次输入密码不一致");
            return false;
        }
        if(mETCode.getText().length() ==0){
            UIHelper.toast(this, "请输入验证码");
            return false;
        }

        return true;
    }

    Runnable SendCode_r = new Runnable() {
        @Override
        public void run() {
            UIHelper.toast(RetrievePasswordActivity.this, "短信已经发送,请注意查收");
        }
    };

    Runnable SendCodeError_r = new Runnable() {
        @Override
        public void run() {
            UIHelper.toast(RetrievePasswordActivity.this, "短信验证失败");
        }
    };

    private void initSMS(){
        SMSSDK.initSDK(this, PreferencesConfig.SHARESDKAPPKEY, PreferencesConfig.SHARESDKAPPSECRET);
        SMSSDK.registerEventHandler( new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        handler.post(confirmChange_r);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        handler.post(SendCode_r);
                        //UIHelper.toast(registerActivity, "短信已经发送,请注意查收");
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    handler.post(SendCodeError_r);
                    //((Throwable)data).printStackTrace();
                }
            }
        });
    }
}
