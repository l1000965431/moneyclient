package com.dragoneye.money.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView mLoginTextView;
    TextView mRegisterTextView;

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
    }

    private void initData(){

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_login_button_login:
                onLogin();
                break;
            case R.id.fragment_login_button_Register:
                onRegister();
                break;
        }
    }

    private void onLogin(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        HttpParams params = new HttpParams();
        params.put("username", "test");
        params.put("userpwd", "123456");

        HttpClient.post("http://192.168.0.182:8080/ApkLongin/userlogin", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d(TAG, "login failure- " + s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "login success- " + s);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
