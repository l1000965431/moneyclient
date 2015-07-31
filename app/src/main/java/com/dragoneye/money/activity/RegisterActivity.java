package com.dragoneye.money.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.UserProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final int USER_ID_LIMIT_MIN = 9;
    private static final int USER_ID_LIMIT_MAX = 16;

    TextView mConfirmRegisterTextView;

    EditText mUserIdTextField;
    EditText mUserPasswordTextField;
    EditText mUserPasswordConfirmTextFiled;

    RadioGroup mRBUserType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        initView();
        initData();
    }

    private void initView(){
        mConfirmRegisterTextView = (TextView)findViewById(R.id.fragment_register_buttonlogin);
        mConfirmRegisterTextView.setOnClickListener(this);

        mUserIdTextField = (EditText)findViewById(R.id.fragment_register_account);
        mUserPasswordTextField = (EditText)findViewById(R.id.fragment_register_Enter_password);
        mUserPasswordConfirmTextFiled = (EditText)findViewById(R.id.fragment_register_Enter_password_again);

        mRBUserType = (RadioGroup)findViewById(R.id.fragment_register_Classification);
    }

    private void initData(){

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_register_buttonlogin:
                if( checkUserInput() ){
                    onRegister();
                }
                break;
        }
    }

    private void onRegister(){
        HttpParams params = new HttpParams();
        params.put(UserProtocol.REGISTER_PARAM_USER_ID, mUserIdTextField.getText().toString());
        params.put(UserProtocol.REGISTER_PARAM_USER_PASSWORD, mUserPasswordTextField.getText().toString());
        params.put(UserProtocol.REGISTER_PARAM_USER_TYPE, getUserType());

        HttpClient.post(UserProtocol.URL_REGISTER, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                UIHelper.toast(RegisterActivity.this, "网络异常");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if( s == null ){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
