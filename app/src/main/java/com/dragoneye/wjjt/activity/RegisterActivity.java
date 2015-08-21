package com.dragoneye.wjjt.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.InputChecker;
import com.dragoneye.wjjt.tool.UIHelper;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final int SEND_CODE_INTERVAL = 60;

    private static final int MESSAGE_TICK = 1;

    TextView mConfirmRegisterTextView;

    EditText mUserIdTextField;
    EditText mUserPasswordTextField;
    EditText mUserPasswordConfirmTextFiled;
    EditText mETCode;
    CheckBox mCKAgreement;

    RadioGroup mRBUserType;

    TextView mTVSendSecurityCode;

    TextView mTVAgreement;

    String UserID;

    RegisterActivity registerActivity;

    private static class MyHandler extends Handler{
        private final WeakReference<RegisterActivity> mRef;

        public MyHandler(RegisterActivity ref){
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_TICK:
                    mRef.get().tick--;
                    if( mRef.get().tick < 0 ){
                        mRef.get().mTVSendSecurityCode.setText("发送验证码");
                        mRef.get().mTVSendSecurityCode.setOnClickListener(mRef.get());
                        mRef.get().mTVSendSecurityCode.setBackgroundDrawable(mRef.get().getResources().getDrawable(R.drawable.bg_rounded10blue));
                    }else {
                        mRef.get().mTVSendSecurityCode.setText(mRef.get().tick + "秒后再次发送");
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.fragment_register);
        registerActivity = this;
        initView();
        initData();
        initSMS();
    }

    private void initView(){
        mConfirmRegisterTextView = (TextView)findViewById(R.id.fragment_register_buttonlogin);
        mConfirmRegisterTextView.setOnClickListener(this);

        mUserIdTextField = (EditText)findViewById(R.id.fragment_register_account);
        mUserPasswordTextField = (EditText)findViewById(R.id.fragment_register_Enter_password);
        mUserPasswordConfirmTextFiled = (EditText)findViewById(R.id.change_password_et_newPassword);
        mETCode = (EditText)findViewById(R.id.fragment_register_Enter_SecurityCode_Text);
        mCKAgreement = (CheckBox)findViewById(R.id.fragment_register_Agreement_checkBox);
        mCKAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mConfirmRegisterTextView.setEnabled(true);
                    mConfirmRegisterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded10blue));
                }else {
                    mConfirmRegisterTextView.setEnabled(false);
                    mConfirmRegisterTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded12));
                }
            }
        });
        mCKAgreement.setChecked(true);
        mCKAgreement.setChecked(false);

        mRBUserType = (RadioGroup)findViewById(R.id.fragment_register_Classification);

        mTVSendSecurityCode = (TextView)findViewById(R.id.fragment_register_Enter_SecurityCode_button);
        mTVSendSecurityCode.setOnClickListener(this);

        mTVAgreement = (TextView)findViewById(R.id.fragment_register_Agreement_text);
        mTVAgreement.setOnClickListener(this);
    }

    private void initData(){

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_register_buttonlogin:
                if( checkUserInput() ){
                    //onRegister();
                    SMSSDK.submitVerificationCode("86",mUserIdTextField.getText().toString(), mETCode.getText().toString());
                }
                break;
            case R.id.fragment_register_Enter_SecurityCode_button:
                onSendCode();
                break;
            case R.id.fragment_register_Agreement_text:
                onAgreement();
                break;
        }
    }

    private void onAgreement(){
        Intent intent = new Intent(this, AgreementActivity.class);
        startActivity(intent);
    }

    private void onSendCode(){
        if( !InputChecker.isPhoneNumber(mUserIdTextField.getText().toString()) ){
            UIHelper.toast(this, "请输入一个正确的手机号码!");
            return;
        }
        onSendCodeSuccess();
        SMSSDK.getVerificationCode("86", mUserIdTextField.getText().toString());
    }

    private void onSendCodeSuccess(){
        mTVSendSecurityCode.setOnClickListener(null);
        mTVSendSecurityCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded12));
        tick = SEND_CODE_INTERVAL;
        handler.sendMessage(handler.obtainMessage(MESSAGE_TICK));
    }

    Runnable onRegisterButton_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();
            params.put(UserProtocol.REGISTER_PARAM_USER_ID, mUserIdTextField.getText().toString());
            params.put(UserProtocol.REGISTER_PARAM_USER_PASSWORD, mUserPasswordTextField.getText().toString());
            params.put(UserProtocol.REGISTER_PARAM_USER_TYPE, getUserType());
            UserID = mUserIdTextField.getText().toString();
            HttpClient.atomicPost(RegisterActivity.this, UserProtocol.URL_REGISTER, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        UIHelper.toast(RegisterActivity.this, "服务器异常，请稍后再试");
                        return;
                    }
                    onRegisterResult(s);
                }
            });
        }
    };

    Runnable SendCode_r = new Runnable() {
        @Override
        public void run() {
            UIHelper.toast(registerActivity, "短信已经发送,请注意查收");
        }
    };

    Runnable SendCodeError_r = new Runnable() {
        @Override
        public void run() {
            UIHelper.toast(registerActivity, "短信验证失败");
        }
    };


    private void onRegisterResult(String result){
        switch (result){
            case UserProtocol.REGISTER_RESULT_SUCCESS:
                UIHelper.toast(this, "注册成功");

                //注册推送
                PushAgent mPushAgent = PushAgent.getInstance( this );
                try {
                    mPushAgent.addAlias(UserID, ALIAS_TYPE.SINA_WEIBO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("userId", mUserIdTextField.getText().toString());
                intent.putExtra("userPassword", mUserPasswordConfirmTextFiled.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case UserProtocol.REGISTER_RESULT_OCCUPIED:
                UIHelper.toast(this, "用户名已被占用");
                break;
            case UserProtocol.REGISTER_RESULT_FORMAT_INCORRECT:
                UIHelper.toast(this, "用户名格式错误");
                break;
            case UserProtocol.REGISTER_RESULT_CLOSED:
                UIHelper.toast(this, "已关闭注册");
                break;
            case UserProtocol.REGISTER_RESULT_SECURITY_CODE_ERROR:
                UIHelper.toast(this, "验证码错误");
                break;
            case UserProtocol.REGISTER_RESULT_FAILED:
            default:
                UIHelper.toast(this, "注册失败");
                break;
        }
    }

    private boolean checkUserInput(){
        if( !InputChecker.isPhoneNumber(mUserIdTextField.getText().toString()) ){
            UIHelper.toast(this, "请输入一个正确的手机号码!");
            return false;
        }

        if( !InputChecker.checkPassword(mUserPasswordTextField.getText().toString()) ){
            UIHelper.toast(this, "密码格式错误，请输入6-16个英文字符或数字!");
            return false;
        }

        if( mUserPasswordTextField.getText().toString().compareTo(
                mUserPasswordConfirmTextFiled.getText().toString() ) != 0 ){
            UIHelper.toast(this, "两次输入密码不一致!");
            return false;
        }

        if( mETCode.getText().length() == 0 ){
            UIHelper.toast(this, "请输入验证码!");
            return false;
        }

        if( getUserType() == -1 ){
            UIHelper.toast(this, "请选择用户类别!");
            return false;
        }
        return true;
    }

    private int getUserType(){
        int id = mRBUserType.getCheckedRadioButtonId();
        if( id == R.id.fragment_register_Classification_Investment ){
            return UserProtocol.PROTOCOL_USER_TYPE_INVESTOR;
        }else if( id == R.id.fragment_register_Classification_Fundraising ){
            return UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR;
        }
        return -1;
    }

    private void initSMS(){
        SMSSDK.initSDK(this, PreferencesConfig.SHARESDKAPPKEY, PreferencesConfig.SHARESDKAPPSECRET);
        SMSSDK.registerEventHandler( new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                        //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                         handler.post(onRegisterButton_r);
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