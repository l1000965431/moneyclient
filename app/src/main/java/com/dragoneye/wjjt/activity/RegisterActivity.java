package com.dragoneye.wjjt.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.UIHelper;
import com.umeng.message.PushAgent;

import org.apache.http.Header;

import java.lang.ref.WeakReference;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final int USER_ID_LIMIT_MIN = 9;
    private static final int USER_ID_LIMIT_MAX = 16;

    private static final int SEND_CODE_INTERVAL = 60;

    private static final int MESSAGE_TICK = 1;

    TextView mConfirmRegisterTextView;

    EditText mUserIdTextField;
    EditText mUserPasswordTextField;
    EditText mUserPasswordConfirmTextFiled;

    RadioGroup mRBUserType;

    TextView mTVSendSecurityCode;

    TextView mTVAgreement;

    String UserID;

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
        setContentView(R.layout.fragment_register);
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

        mRBUserType = (RadioGroup)findViewById(R.id.fragment_register_Classification);

        mTVSendSecurityCode = (TextView)findViewById(R.id.fragment_register_Enter_SecurityCode_button);
        mTVSendSecurityCode.setOnClickListener(this);

        mTVAgreement = (TextView)findViewById(R.id.fragment_register_Agreement_text);
        mTVAgreement.setOnClickListener(this);
    }

    private void initData(){

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_register_buttonlogin:
                if( checkUserInput() ){
                    //onRegister();
                    SMSSDK.submitVerificationCode("86",mUserIdTextField.getText().toString(), "1");
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
        Uri uri = Uri.parse(HttpUrlConfig.URL_AGREEMENT);

//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Intent intent = new Intent(this, AgreementActivity.class);
        startActivity(intent);
    }

    private void onSendCode(){
        if( mUserIdTextField.getText().length() < 11){
            UIHelper.toast(this, "请输入一个正确的手机号码!");
            return;
        }
        onSendCodeSuccess();
        SMSSDK.getVerificationCode("86", mUserIdTextField.getText().toString());
        //handler.post(sendRequestCode_r);
    }

    /**
     * 向服务器发送请求
     */
    Runnable sendRequestCode_r = new Runnable() {
        @Override
        public void run() {
            String phoneNumber = mUserIdTextField.getText().toString();

            HttpParams params = new HttpParams();
            params.put(UserProtocol.SEND_CODE_PARAM_USER_ID, phoneNumber);

            HttpClient.atomicPost(RegisterActivity.this, UserProtocol.URL_SEND_CODE, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        UIHelper.toast(RegisterActivity.this, "服务器繁忙");
                        return;
                    }
                    onSendCodeResult(s);
                }
            });

        }
    };

    /**
     * 请求发送验证码结果
     * @param result
     */
    private void onSendCodeResult(String result){
        try{
            int resultCode = Integer.parseInt(result);
            switch (resultCode){
                case UserProtocol.SEND_CODE_RESULT_SUCCESS:
                    UIHelper.toast(this, "验证码发送成功!");
                    onSendCodeSuccess();
                    break;
                case UserProtocol.SEND_CODE_RESULT_FAILED:
                default:
                    throw new Exception();
            }
        }catch (Exception e){
            UIHelper.toast(this, "服务器繁忙");
        }
    }

    private void onSendCodeSuccess(){
        mTVSendSecurityCode.setOnClickListener(null);
        tick = SEND_CODE_INTERVAL;
        handler.sendMessage(handler.obtainMessage(MESSAGE_TICK));
    }

    private void onRegister(){
        HttpParams params = new HttpParams();
        params.put(UserProtocol.REGISTER_PARAM_USER_ID, mUserIdTextField.getText().toString());
        params.put(UserProtocol.REGISTER_PARAM_USER_PASSWORD, mUserPasswordTextField.getText().toString());
        params.put(UserProtocol.REGISTER_PARAM_USER_TYPE, getUserType());
        UserID = mUserIdTextField.getText().toString();
        HttpClient.atomicPost(this, UserProtocol.URL_REGISTER, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                UIHelper.toast(RegisterActivity.this, "网络异常");
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

    private void onRegisterResult(String result){
        switch (result){
            case UserProtocol.REGISTER_RESULT_SUCCESS:
                UIHelper.toast(this, "注册成功");

                //注册推送
                PushAgent mPushAgent = PushAgent.getInstance( this );
                try {
                    mPushAgent.addAlias(UserID, "1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SMSSDK.unregisterAllEventHandler();
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
        if( mUserIdTextField.getText().length() < USER_ID_LIMIT_MIN ){
            UIHelper.toast(this, String.format("用户名至少%d个字符", USER_ID_LIMIT_MIN));
            return false;
        }else if( mUserIdTextField.getText().length() > USER_ID_LIMIT_MAX ){
            UIHelper.toast(this, String.format("用户名最多%d个字符", USER_ID_LIMIT_MAX));
            return false;
        }

        if( mUserPasswordTextField.getText().toString().compareTo(
                mUserPasswordConfirmTextFiled.getText().toString() ) != 0 ){
            UIHelper.toast(this, "两次输入密码不一致");
            return false;
        }

        if( getUserType() == -1 ){
            UIHelper.toast(this, "请选择用户类别");
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
        SMSSDK.registerEventHandler( new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        onRegister();
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        });
    }

}
