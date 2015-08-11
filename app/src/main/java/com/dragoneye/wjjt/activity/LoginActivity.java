package com.dragoneye.wjjt.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.UserBase;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView mLoginTextView;
    TextView mRegisterTextView;

    EditText mETUserId;
    EditText mETUserPassword;

    private SharedPreferences preferences;

    private String mLoginUserId;
    private String mLoginUserPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        initData();
    }

    private void initView(){
        mLoginTextView = (TextView)findViewById(R.id.fragment_login_button_login);
        mLoginTextView.setOnClickListener(this);
        mRegisterTextView = (TextView)findViewById(R.id.fragment_login_button_Register);
        mRegisterTextView.setOnClickListener(this);

        mETUserId = (EditText)findViewById(R.id.fragment_login_Enter_account);
        mETUserPassword = (EditText)findViewById(R.id.fragment_login_Enter_password);

        mETUserId.setText(getLastLoginUserId());
        mETUserPassword.setText(getLastLoginUserPassword());
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
        }
    }

    private void onLogin(){
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
        if( !checkUserInput() ){
            return;
        }

        mLoginUserId = mETUserId.getText().toString();
        mLoginUserPassword = mETUserPassword.getText().toString();

        HttpParams params = new HttpParams();
        params.put(UserProtocol.PASSWORD_LOGIN_PARAM_USER_ID, mLoginUserId);
        params.put(UserProtocol.PASSWORD_LOGIN_PARAM_USER_PASSWORD, mLoginUserPassword);

        HttpClient.atomicPost(this, UserProtocol.URL_LOGIN, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                String result = HttpClient.getValueFromHeader(headers, UserProtocol.PASSWORD_LOGIN_RESULT_KEY);
                String response = HttpClient.getValueFromHeader(headers, UserProtocol.PASSWORD_LOGIN_RESULT_INFO_KEY);
                String token = s;
                if (result == null || s == null) {
                    UIHelper.toast(LoginActivity.this, "服务器异常");
                    return;
                }
                onLoginResult(result, response, token);
            }
        });
    }

    private void onLoginResult(String result, String response, String token){
        switch (result){
            case UserProtocol.PASSWORD_LOGIN_RESULT_SUCCESS:
                UIHelper.toast(this, "登录成功");
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
                if(userType == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
                    userBase.setIntroduction(jsonObject.getString("introduction"));
                    userBase.setExpertise(jsonObject.getString("expertise"));
                    userBase.setEduInfo(jsonObject.getString("eduInfo"));
                    userBase.setCareer(jsonObject.getString("career"));
                }
            }
            ((MyApplication) getApplication()).setCurrentUser(userBase);
            ((MyApplication)getApplication()).setToken(token);
            setLastLoginUserId(mLoginUserId);
            setLastLoginUserPassword(mLoginUserPassword);

        }catch (JSONException e){
            e.printStackTrace();
            UIHelper.toast(this, "登录失败");
            return;
        }

        startMainActivity();
    }

    private void startMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
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
        startActivity(intent);
    }
}
