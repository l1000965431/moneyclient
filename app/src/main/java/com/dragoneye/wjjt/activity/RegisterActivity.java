package com.dragoneye.wjjt.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.TimeCounterActivity;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.DESCoder;
import com.dragoneye.wjjt.tool.InputChecker;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;

import org.apache.http.Header;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends TimeCounterActivity implements View.OnClickListener{

    private static final int SEND_CODE_INTERVAL = 60000;

    private static final int MESSAGE_TICK = 1;

    TextView mConfirmRegisterTextView;

    EditText mUserIdTextField;
    EditText mUserPasswordTextField;
    EditText mUserPasswordConfirmTextFiled;
    EditText mETCode;
    EditText mETInvitationCode;
    CheckBox mCKAgreement;

    RadioGroup mRBUserType;

    TextView mTVSendSecurityCode;

    TextView mTVAgreement;

    String UserID;

    RegisterActivity registerActivity;
    ProgressDialog progressDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.fragment_register);
        registerActivity = this;
        initView();
        initData();
        initSMS();
        initCounter(PreferencesConfig.TIME_COUNTER_SEND_CODE_ACTIVITY, 1000);
        continueCount(SEND_CODE_INTERVAL);
        if(getMillisLeft() > 0){
            setSendCodeButtonEnabled(false, getMillisLeft() / 1000);
        }
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
        mRBUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.fragment_register_Classification_Fundraising) {
                    mETInvitationCode.setVisibility(View.VISIBLE);
                } else {
                    mETInvitationCode.setVisibility(View.GONE);
                }
            }
        });

        mTVSendSecurityCode = (TextView)findViewById(R.id.fragment_register_Enter_SecurityCode_button);
        mTVSendSecurityCode.setOnClickListener(this);

        mTVAgreement = (TextView)findViewById(R.id.fragment_register_Agreement_text);
        mTVAgreement.setOnClickListener(this);

        mETInvitationCode = (EditText)findViewById(R.id.fragment_register_et_invitation_code);

        progressDialog = new ProgressDialog(this);
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
//                    handler.post(onRegisterButton_r);
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
            mTVSendSecurityCode.setText("发送验证码");
            mTVSendSecurityCode.setOnClickListener(this);
            mTVSendSecurityCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded10blue));
        }else {
            setSendCodeButtonTickText(sec);
            mTVSendSecurityCode.setOnClickListener(null);
            mTVSendSecurityCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded12));
        }
    }

    private void setSendCodeButtonTickText(long sec){
        mTVSendSecurityCode.setText(sec + "秒后再次发送");
    }

    private void onAgreement(){
        AgreementActivity.OpenAgreement(this);
    }

    private void onSendCode(){
        if( !InputChecker.isPhoneNumber(mUserIdTextField.getText().toString()) ){
            UIHelper.toast(this, "请输入一个正确的手机号码!");
            return;
        }
        startCount(SEND_CODE_INTERVAL);
        setSendCodeButtonEnabled(false, SEND_CODE_INTERVAL / 1000);
        SMSSDK.getVerificationCode("86", mUserIdTextField.getText().toString());
    }

    Runnable onRegisterButton_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            UserID = mUserIdTextField.getText().toString();

            HashMap<String, String> dataMap = new HashMap<>();

            dataMap.put(UserProtocol.REGISTER_PARAM_USER_ID, mUserIdTextField.getText().toString());
            dataMap.put(UserProtocol.REGISTER_PARAM_USER_PASSWORD, mUserPasswordTextField.getText().toString());
            dataMap.put(UserProtocol.REGISTER_PARAM_USER_TYPE, String.valueOf(getUserType()));
            dataMap.put("inviteCode", mETInvitationCode.getText().toString());

            String ObjectJson = ToolMaster.gsonInstance().toJson(dataMap);
            try{
                byte[] data = DESCoder.encrypt(ObjectJson.getBytes(), ToolMaster.getCodeKey(UserID));
                params.put("data", DESCoder.encryptBASE64(data));
            }catch (Exception e){
                e.printStackTrace();
            }

            progressDialog.show();

            HttpClient.getClient().addHeader("userId", UserID);
            HttpClient.atomicPost(RegisterActivity.this, UserProtocol.URL_REGISTER, params, new HttpClient.MyHttpHandler() {
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
            case UserProtocol.REGISTER_RESULT_INVITATION_CODE_ERROR:
                UIHelper.toast(this, "邀请码错误");
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

        int userType = getUserType();
        if (userType == -1 ){
            UIHelper.toast(this, "请选择用户类别!");
            return false;
        }

        if( userType == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR ){
            if(mETInvitationCode.getText().length() == 0){
                UIHelper.toast(this, "请输入邀请码");
                return false;
            }
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
