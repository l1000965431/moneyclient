package com.dragoneye.wjjt.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.dragoneye.wjjt.tool.Base32;
import com.dragoneye.wjjt.tool.Coder;
import com.dragoneye.wjjt.tool.DESCoder;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;
import com.dragoneye.wjjt.user.UserBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView mLoginTextView;
    TextView mRegisterTextView;
    TextView mTVForgotPassword;

    EditText mETUserId;
    EditText mETUserPassword;

    private SharedPreferences preferences;

    private String mLoginUserId;
    private String mLoginUserPassword;

    ProgressDialog mProgressDialog;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        initData();
        InitPush();
        UmengUpdateAgent.update(this);
    }

    private void initView(){
        mLoginTextView = (TextView)findViewById(R.id.fragment_login_button_login);
        mLoginTextView.setOnClickListener(this);
        mRegisterTextView = (TextView)findViewById(R.id.fragment_login_button_Register);
        mRegisterTextView.setOnClickListener(this);
        View agreementButton = findViewById(R.id.fragment_login_button_agreement);
        agreementButton.setOnClickListener(this);
        mTVForgotPassword = (TextView)findViewById(R.id.fragment_login_button_ForgotPassword);
        mTVForgotPassword.setOnClickListener(this);

        mETUserId = (EditText)findViewById(R.id.fragment_login_Enter_account);
        mETUserPassword = (EditText)findViewById(R.id.fragment_login_Enter_password);

        mETUserId.setText(getLastLoginUserId());
        mETUserPassword.setText(getLastLoginUserPassword());

        mProgressDialog = new ProgressDialog(this);
    }

    private void initData(){

    }

    private String getLastLoginUserId(){
        return preferences.getString(PreferencesConfig.LAST_LOGIN_USER_ID, "");
    }

    private String getLastLoginUserPassword(){
        return preferences.getString(PreferencesConfig.LAST_LOGIN_USER_PASSWORD, "");
    }

    private void setLastLoginUserId(String userId){
        preferences.edit().putString(PreferencesConfig.LAST_LOGIN_USER_ID, userId).apply();
    }

    private void setLastLoginUserPassword(String userPassword){
        preferences.edit().putString(PreferencesConfig.LAST_LOGIN_USER_PASSWORD, userPassword).apply();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_login_button_login:
                onLogin();
//                startMainActivity();
                break;
            case R.id.fragment_login_button_Register:
                onRegister();
                break;
            case R.id.fragment_login_button_agreement:
                AgreementActivity.OpenAgreement(this);
                break;
            case R.id.fragment_login_button_ForgotPassword:
                Intent intent = new Intent(this, RetrievePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void onLogin(){
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
        if(false){
            UserBase userBase = new UserBase();
            userBase.setUserId("18511583205");
            userBase.setUserType(UserProtocol.PROTOCOL_USER_TYPE_INVESTOR);
            ((MyApplication) getApplication()).setCurrentUser(this, userBase);
            MainActivity.CallMainActivity(this);
            return;
        }


        if( !checkUserInput() ){
            return;
        }

        mLoginUserId = mETUserId.getText().toString();
        mLoginUserPassword = mETUserPassword.getText().toString();

        mProgressDialog.setMessage("登录中");
        mProgressDialog.show();
        HttpParams params = new HttpParams();

        HashMap<String, String> dataMap = new HashMap<>();

        dataMap.put(UserProtocol.PASSWORD_LOGIN_PARAM_USER_ID, mLoginUserId);
        dataMap.put(UserProtocol.PASSWORD_LOGIN_PARAM_USER_PASSWORD, mLoginUserPassword);


        String ObjectJson = ToolMaster.gsonInstance().toJson(dataMap);
        try{
            byte[] data = DESCoder.encrypt(ObjectJson.getBytes(), ToolMaster.getCodeKey(mLoginUserId));
            params.put("data", DESCoder.encryptBASE64(data));


        }catch (Exception e){
            e.printStackTrace();
        }


        HttpClient.getClient().addHeader("userId", (mLoginUserId));

        HttpClient.atomicPost(this, UserProtocol.URL_LOGIN, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                mProgressDialog.dismiss();
                HttpClient.getClient().removeHeader("userId");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                mProgressDialog.dismiss();
//                String result = HttpClient.getValueFromHeader(headers, UserProtocol.PASSWORD_LOGIN_RESULT_KEY);
//                String response = HttpClient.getValueFromHeader(headers, UserProtocol.PASSWORD_LOGIN_RESULT_INFO_KEY);
//                String token = s;
                HttpClient.getClient().removeHeader("userId");
                if (s == null || s.isEmpty()) {
                    UIHelper.toast(LoginActivity.this, getString(R.string.http_server_exception));
                    return;
                }
                try {
                    JSONObject object = new JSONObject(s);
                    String result = object.getString("LoginResult");
                    String token = object.getString("token");
                    String response = object.getString("UserResponse");
                    onLoginResult(result, response, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                    UIHelper.toast(LoginActivity.this, "登录失败");
                    return;
                }

            }
        });
//        HttpClient.getClient().removeHeader("userId");
    }

    private void onLoginResult(String result, String response, String token){
        switch (result){
            case UserProtocol.PASSWORD_LOGIN_RESULT_SUCCESS:
                onLoginSuccess(response, token);
                break;
            case UserProtocol.PASSWORD_LOGIN_RESULT_ERROR:
                UIHelper.toast(this, "用户名或密码错误");
                break;
            case UserProtocol.PASSWORD_LOGIN_RESULT_CODE_ERROR:
                UIHelper.toast(this, "验证码错误");
                break;
            case UserProtocol.PASSWORD_LOGIN_RESULT_FAILED:
                default:
                UIHelper.toast(this, "服务器异常");
                break;
        }
    }

    private void onLoginSuccess(String response, String token){
        try{
            JSONObject jsonObject = new JSONObject(response);
            int userType = jsonObject.getInt("userType");
            boolean isPerfectInfo = jsonObject.getBoolean("IsPerfect");
            UserBase userBase = new UserBase();
            userBase.setUserType(userType);
            userBase.setUserId(jsonObject.getString("userId"));
            userBase.setIsPerfectInfo(isPerfectInfo);
            if( isPerfectInfo ){
                userBase.setUserName(jsonObject.getString("userName"));
                userBase.setEmail(jsonObject.getString("mail"));
                userBase.setAddress(jsonObject.getString("location"));
                userBase.setRealName(jsonObject.getString("realName"));
                userBase.setIdentityId(jsonObject.getString("identityId"));
                userBase.setUserHeadPortrait(jsonObject.getString("userHeadPortrait"));
                userBase.setIsInvited(jsonObject.getBoolean("IsInvited"));
                userBase.setUserInviteCode(jsonObject.getString("userInvitecode"));
                if(userType == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
                    userBase.setIntroduction(jsonObject.getString("introduction"));
                    userBase.setExpertise(jsonObject.getString("expertise"));
                    userBase.setEduInfo(jsonObject.getString("eduInfo"));
                    userBase.setCareer(jsonObject.getString("career"));
                }
            }
            ((MyApplication)getApplication()).setCurrentUser(this, userBase);
            ((MyApplication)getApplication()).setToken(this, token);
            setLastLoginUserId(mLoginUserId);
            setLastLoginUserPassword(mLoginUserPassword);

        }catch (JSONException e){
            e.printStackTrace();
            UIHelper.toast(this, "登录失败");
            return;
        }

        UIHelper.toast(this, "登录成功");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //注册推送
                PushAgent mPushAgent = PushAgent.getInstance( LoginActivity.this );
                String device_token = "";
                try {
                    mPushAgent.removeAlias(mLoginUserId, ALIAS_TYPE.SINA_WEIBO);
                    mPushAgent.addExclusiveAlias(mLoginUserId, ALIAS_TYPE.SINA_WEIBO);
                    device_token = UmengRegistrar.getRegistrationId(LoginActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("login", device_token);
            }
        };
        new Thread(runnable).start();
        ((MyApplication)getApplication()).setUserLoginSuccess(this);
        MainActivity.CallMainActivity(this);
        ImageLoader.getInstance().clearMemoryCache();
        finish();
    }

    private boolean checkUserInput(){
        if(mETUserId.getText().length() == 0
                || mETUserPassword.getText().length() == 0){
            UIHelper.toast(this, "请输入用户名或密码");
            return false;
        }

        return true;
    }

    private void onRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == 1 && resultCode == RESULT_OK ){
            String userId = data.getStringExtra("userId");
            String password = data.getStringExtra("userPassword");
            mETUserId.setText(userId);
            mETUserPassword.setText(password);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onLogin();
                }
            }, 300);
        }
    }

    private void InitPush(){
        String device_token = UmengRegistrar.getRegistrationId(getApplication());

        Log.d("InitPush", device_token);
    }

    public static void CallLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
