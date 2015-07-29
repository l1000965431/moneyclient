package com.dragoneye.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.UserProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.user.CurrentUser;
import com.dragoneye.money.user.UserInvestor;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView mLoginTextView;
    TextView mRegisterTextView;

    EditText mETUserId;
    EditText mETUserPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
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
    }

    private void initData(){

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

        HttpParams params = new HttpParams();
        params.put(UserProtocol.PASSWORD_LOGIN_PARAM_USER_ID, mETUserId.getText());
        params.put(UserProtocol.PASSWORD_LOGIN_PARAM_USER_PASSWORD, mETUserPassword.getText());

        HttpClient.post(UserProtocol.URL_LOGIN, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                UIHelper.toast(LoginActivity.this, "网络异常");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
//                Log.d(TAG, "login success- " + headers[0].getName());
                String result = HttpClient.getValueFromHeader(headers, UserProtocol.PASSWORD_LOGIN_RESULT_KEY);
                String response = HttpClient.getValueFromHeader(headers, UserProtocol.PASSWORD_LOGIN_RESULT_INFO_KEY);
                String token = s;
                if( result == null || s == null ){
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
            if(userType == UserProtocol.PROTOCOL_USER_TYPE_INVESTOR){
                UserInvestor userInvestor = new UserInvestor();
                userInvestor.setUserId(jsonObject.getString("userId"));
                userInvestor.setIsPerfectInfo(isPerfectInfo);
                if( isPerfectInfo ){
                    userInvestor.setUserName(jsonObject.getString("userName"));
                    userInvestor.setEmail(jsonObject.getString("mail"));
                    userInvestor.setAddress(jsonObject.getString("location"));
                }
                CurrentUser.setCurrentUser(userInvestor);
                CurrentUser.setToken(token);
            }else {

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

//        UserEntrepreneur userEntrepreneur = new UserEntrepreneur();
//        userEntrepreneur.setUserId("test");
//        CurrentUser.setCurrentUser(userEntrepreneur);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
