package com.dragoneye.wjjt.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.DESCoder;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText mETOldPassword;
    private EditText mETNewPassword;
    private EditText mETRepeatPassword;
    private EditText mETCode;
    private TextView mTVSendCode;
    private TextView mTVConfirmChange;
    private ChangePasswordActivity changePasswordActivity;
    ProgressDialog progressDialog;

    private static final int MESSAGE_TICK = 1;
    private static final int SEND_CODE_INTERVAL = 60;

    private static class MyHandler extends Handler {
        private final WeakReference<ChangePasswordActivity> mRef;

        public MyHandler(ChangePasswordActivity ref){
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_TICK:
                    mRef.get().tick--;
                    if( mRef.get().tick < 0 ){
                        mRef.get().mTVSendCode.setText("发送验证码");
                        mRef.get().mTVSendCode.setOnClickListener(mRef.get());
                        mRef.get().mTVSendCode.setBackgroundDrawable(mRef.get().getResources().getDrawable(R.drawable.bg_rounded10blue));
                    }else {
                        mRef.get().mTVSendCode.setText(mRef.get().tick + "秒后再次发送");
                        mRef.get().handler.sendMessageDelayed(mRef.get().handler.obtainMessage(MESSAGE_TICK), 1000);
                    }
                    break;
            }
        }
    }

    private MyHandler handler = new MyHandler(this);
    private int tick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_password);
        changePasswordActivity = this;
        initView();
        initData();
        initSMS();
    }

    private void initView(){
        mETOldPassword = (EditText)findViewById(R.id.change_password_et_oldPassword);
        mETNewPassword = (EditText)findViewById(R.id.change_password_et_newPassword);
        mETRepeatPassword = (EditText)findViewById(R.id.change_password_et_repeatPassword);
        mETCode = (EditText)findViewById(R.id.change_password_et_code);

        mTVSendCode = (TextView)findViewById(R.id.change_password_tv_sendCode);
        mTVSendCode.setOnClickListener(this);

        mTVConfirmChange = (TextView)findViewById(R.id.change_password_tv_confirmChange);
        mTVConfirmChange.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    private void initData(){

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.change_password_tv_sendCode:
                onSendCode();
                break;
            case R.id.change_password_tv_confirmChange:
                onConfirmChange();
                break;
        }
    }

    private void onSendCode(){
        onSendCodeSuccess();
        SMSSDK.getVerificationCode("86", ((MyApplication) getApplication()).getCurrentUser(this).getUserId());
    }

    private void onSendCodeSuccess(){
        mTVSendCode.setOnClickListener(null);
        mTVSendCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded12));
        tick = SEND_CODE_INTERVAL;
        handler.sendMessage(handler.obtainMessage(MESSAGE_TICK));
    }

    private void onConfirmChange(){
        if( !checkUserInput() ){
            return;
        }

        SMSSDK.submitVerificationCode("86", ((MyApplication) getApplication()).getCurrentUser(this).getUserId(), mETCode.getText().toString());
//        handler.post(confirmChange_r);
    }

    Runnable confirmChange_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            HashMap<String, String> dataMap = new HashMap<>();
            String userId = ((MyApplication)getApplication()).getCurrentUser(ChangePasswordActivity.this).getUserId();

            dataMap.put(UserProtocol.CHANGE_PASSWORD_PARAM_USER_ID, userId);
            dataMap.put(UserProtocol.CHANGE_PASSWORD_PARAM_OLD_PASSWORD, mETOldPassword.getText().toString());
            dataMap.put(UserProtocol.CHANGE_PASSWORD_PARAM_NEW_PASSWORD, mETNewPassword.getText().toString());
            dataMap.put(UserProtocol.CHANGE_PASSWORD_PARAM_CODE, mETCode.getText().toString());

            String ObjectJson = ToolMaster.gsonInstance().toJson(dataMap);
            try{
                byte[] data = DESCoder.encrypt(ObjectJson.getBytes(), ToolMaster.getCodeKey(userId));
                params.put("data", DESCoder.encryptBASE64(data));
            }catch (Exception e){
                e.printStackTrace();
            }

            progressDialog.show();
            HttpClient.getClient().addHeader("userId", userId);
            HttpClient.atomicPost(ChangePasswordActivity.this, UserProtocol.URL_CHANGE_PASSWORD, params, new HttpClient.MyHttpHandler() {
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
                        UIHelper.toast(ChangePasswordActivity.this, "服务器繁忙，请稍后再试");
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
                case UserProtocol.CHANGE_PASSWORD_RESULT_CODE_INCORRECT:
                    UIHelper.toast(this, "验证码错误");
                    break;
                case UserProtocol.CHANGE_PASSWORD_RESULT_SUCCESS:
                    UIHelper.toast(this, "修改成功");
                    finish();
                    break;
                case UserProtocol.CHANGE_PASSWORD_RESULT_FAILED:
                    UIHelper.toast(this, "修改失败");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            UIHelper.toast(this, "服务器繁忙，请稍后再试");
        }
    }

    private boolean checkUserInput(){
        if(mETOldPassword.getText().length() == 0){
            UIHelper.toast(this, "请输入旧密码");
            return false;
        }
        if(mETNewPassword.getText().length() == 0 || mETOldPassword.getText().length() == 0){
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
            UIHelper.toast(changePasswordActivity, "短信已经发送,请注意查收");
        }
    };

    Runnable SendCodeError_r = new Runnable() {
        @Override
        public void run() {
            UIHelper.toast(changePasswordActivity, "短信验证失败");
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
