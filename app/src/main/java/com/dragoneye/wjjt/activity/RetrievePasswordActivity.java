package com.dragoneye.wjjt.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.UIHelper;

import org.apache.http.Header;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;

public class RetrievePasswordActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText mETUserId;
    private EditText mETNewPassword;
    private EditText mETRepeatPassword;
    private EditText mETCode;
    private TextView mTVSendCode;
    private TextView mTVConfirm;

    private static final int MESSAGE_TICK = 1;
    private static final int SEND_CODE_INTERVAL = 60;

    private static class MyHandler extends Handler {
        private final WeakReference<RetrievePasswordActivity> mRef;

        public MyHandler(RetrievePasswordActivity ref){
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
        setContentView(R.layout.home_self_group_password_back);

        mETUserId = (EditText)findViewById(R.id.retrieve_password_et_userId);
        mETNewPassword = (EditText)findViewById(R.id.retrieve_password_et_newPassword);
        mETRepeatPassword = (EditText)findViewById(R.id.retrieve_password_et_repeatPassword);
        mETCode = (EditText)findViewById(R.id.retrieve_password_et_code);
        mTVSendCode = (TextView)findViewById(R.id.retrieve_password_tv_sendCode);
        mTVConfirm = (TextView)findViewById(R.id.retrieve_password_tv_confirm);
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
        onSendCodeSuccess();
    }

    /**
     * 向服务器发送请求
     */
    Runnable sendRequestCode_r = new Runnable() {
        @Override
        public void run() {
//            String phoneNumber = ((MyApplication)getApplication()).getCurrentUser(ChangePasswordActivity.this).getUserId();
//
//            HttpParams params = new HttpParams();
//            params.put(UserProtocol.SEND_CODE_PARAM_USER_ID, phoneNumber);
//
//            HttpClient.atomicPost(ChangePasswordActivity.this, UserProtocol.URL_SEND_CODE, params, new HttpClient.MyHttpHandler() {
//                @Override
//                public void onSuccess(int i, Header[] headers, String s) {
//                    if (s == null) {
//                        UIHelper.toast(ChangePasswordActivity.this, "服务器繁忙");
//                        return;
//                    }
//                    onSendCodeResult(s);
//                }
//            });

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
        mTVSendCode.setOnClickListener(null);
        tick = SEND_CODE_INTERVAL;
        handler.sendMessage(handler.obtainMessage(MESSAGE_TICK));
    }

    private void onConfirmChange(){
        if( !checkUserInput() ){
            return;
        }

        handler.post(confirmChange_r);
    }

    Runnable confirmChange_r = new Runnable() {
        @Override
        public void run() {
//            HttpParams params = new HttpParams();
//
//            params.put(UserProtocol.CHANGE_PASSWORD_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser(ChangePasswordActivity.this).getUserId());
//            params.put(UserProtocol.CHANGE_PASSWORD_PARAM_OLD_PASSWORD, mETOldPassword.getText());
//            params.put(UserProtocol.CHANGE_PASSWORD_PARAM_NEW_PASSWORD, mETNewPassword.getText());
//            params.put(UserProtocol.CHANGE_PASSWORD_PARAM_CODE, mETCode.getText());
//
//            HttpClient.atomicPost(ChangePasswordActivity.this, UserProtocol.URL_CHANGE_PASSWORD, params, new HttpClient.MyHttpHandler() {
//                @Override
//                public void onSuccess(int i, Header[] headers, String s) {
//                    if (s == null) {
//                        UIHelper.toast(ChangePasswordActivity.this, "服务器繁忙，请稍后再试");
//                        return;
//                    }
//                    onChangeResult(s);
//                }
//            });
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
}
