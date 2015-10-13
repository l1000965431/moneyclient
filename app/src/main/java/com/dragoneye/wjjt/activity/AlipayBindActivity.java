package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.tool.UIHelper;

import org.apache.http.Header;

/**
 * Created by happysky on 15-9-28.
 *
 */
public class AlipayBindActivity extends BaseActivity implements View.OnClickListener{
    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, AlipayBindActivity.class);
        activity.startActivity(intent);
    }

    EditText mETAlipayAccount;
    EditText mETAlipayName;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_bind);

        mETAlipayAccount = (EditText)findViewById(R.id.alipay_bind_et_alipay_account);
        mETAlipayName = (EditText)findViewById(R.id.alipay_bind_et_alipay_name);

        View confirm = findViewById(R.id.alipay_bind_tv_confirm);
        confirm.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.alipay_bind_tv_confirm:
                onConfirm();
                break;
        }
    }

    private void onConfirm(){
        if(!checkUserInfput()){
            return;
        }

        progressDialog.show();

        String userId = ((MyApplication)getApplication()).getCurrentUser(AlipayBindActivity.this).getUserId();

        HttpParams params = new HttpParams();

        params.put("userId", userId);
        params.put("alipayId", mETAlipayAccount.getText().toString());
        params.put("realName", mETAlipayName.getText().toString());


        HttpClient.atomicPost(AlipayBindActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/BindingalipayId", params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                progressDialog.dismiss();
                if( s != null && s.compareTo("SUCCESS") ==0 ){
                    UIHelper.toast(AlipayBindActivity.this, "绑定成功");
                    finish();
                }else {
                    UIHelper.toast(AlipayBindActivity.this, "绑定失败");
                }
            }
        });

    }

    private boolean checkUserInfput(){
        if(mETAlipayAccount.getText().length() == 0){
            UIHelper.toast(this, "请输入支付宝账号!");
            return false;
        }

        if(mETAlipayName.getText().length() == 0){
            UIHelper.toast(this, "请输入支付宝姓名!");
            return false;
        }

        return true;
    }
}
