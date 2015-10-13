package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.DESCoder;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class WithdrawActivity extends BaseActivity implements View.OnClickListener{
    public static final int WITHDRAW_TYPE_WX = 1;
    public static final int WITHDRAW_TYPE_ALIPAY = 2;


    EditText mETChargeNum;
    TextView mTVWalletBalance;
    EditText mETLoginPassword;

    Handler handler = new Handler();

    ProgressDialog progressDialog;

    private int mWithdrawType;

    public static void CallActivity(Activity activity, int withdrawType){
        Intent intent = new Intent(activity, WithdrawActivity.class);
        intent.putExtra("withDrawType", withdrawType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsNeedLoadingFeature(true);
        Intent intent = getIntent();
        mWithdrawType = intent.getIntExtra("withDrawType", WITHDRAW_TYPE_ALIPAY);

        setContentView(R.layout.home_self_group_top);

        TextView tv = (TextView)findViewById(R.id.textView13);
        tv.setText("请输入您要提现的金额：");

        View passwordLayout = findViewById(R.id.charge_activity_password_layout);
        passwordLayout.setVisibility(View.VISIBLE);

        View explain = findViewById(R.id.charage_tv_explain);
        explain.setVisibility(View.VISIBLE);


        TextView textView = (TextView)findViewById(R.id.invest_project_tv_confirm);
        textView.setOnClickListener(this);

        mETChargeNum = (EditText)findViewById(R.id.charge_activity_et_chargeNum);
        mTVWalletBalance = (TextView)findViewById(R.id.charge_tv_wallet_balance);
        mETLoginPassword = (EditText)findViewById(R.id.charge_et_login_password);

        setStartLoading();
        handler.post(getWalletBalance_r);

//        setStatusBarColor(findViewById(R.id.statusBarBackground), Color.GREEN);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onRetryLoading(){
        handler.post(getWalletBalance_r);
    }

    Runnable getWalletBalance_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication) getApplication()).getCurrentUser(WithdrawActivity.this).getUserId());
            params.put("token", ((MyApplication) getApplication()).getToken(WithdrawActivity.this));


            HttpClient.atomicPost(WithdrawActivity.this, UserProtocol.URL_GET_WALLET_BALANCE, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        finishLoading(false);
                        return;
                    }


                    int balance = Integer.parseInt(s);
                    if(balance == -1){
                        ((MyApplication) getApplication()).reLogin(WithdrawActivity.this);
                        return;
                    }

                    mTVWalletBalance.setText(String.format("钱包余额：%s", ToolMaster.convertToPriceString(balance)));

                    if( mWithdrawType == WITHDRAW_TYPE_WX ){
                        handler.post(checkIsBindWeChat_r);
                    }else if( mWithdrawType == WITHDRAW_TYPE_ALIPAY ){
                        handler.post(checkIsBindAlipay_r);
                    }

                }
            });
        }
    };

    Runnable checkIsBindWeChat_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication)getApplication()).getCurrentUser(WithdrawActivity.this).getUserId());

            HttpClient.atomicPost(WithdrawActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/IsBinding", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        finishLoading(false);
                        return;
                    }

                    if(s.compareTo("true") != 0){
                        WxBindActivity.CallActivity(WithdrawActivity.this);
                        finish();
                    }else {
                        finishLoading(true);
                    }
                }
            });
        }
    };

    Runnable checkIsBindAlipay_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication)getApplication()).getCurrentUser(WithdrawActivity.this).getUserId());

            HttpClient.atomicPost(WithdrawActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/IsalipayBinding", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        finishLoading(false);
                        return;
                    }

                    if(s.compareTo("true") != 0){
                        AlipayBindActivity.CallActivity(WithdrawActivity.this);
                        finish();
                    }else {
                        finishLoading(true);
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.invest_project_tv_confirm:
                if(mETLoginPassword.getText().length() == 0){
                    UIHelper.toast(this, "请输入登录密码");
                    break;
                }
                if(mWithdrawType == WITHDRAW_TYPE_WX){
                    handler.post(transferWalletWX_r);
                }else {
                    handler.post(transferWalletALIPAY_r);
                }


                break;
        }
    }

    Runnable transferWalletWX_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            String userId = ((MyApplication)getApplication()).getCurrentUser(WithdrawActivity.this).getUserId();

            // 产生个订单号
            String orderNo = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + userId;

            HashMap<String, String> mapData = new HashMap<>();

            mapData.put("userId", userId);
            mapData.put("orderId", orderNo);
            mapData.put("passWord", mETLoginPassword.getText().toString());
            mapData.put("lines", mETChargeNum.getText().toString());

            String ObjectJson = ToolMaster.gsonInstance().toJson(mapData);
            try{
                byte[] data = DESCoder.encrypt(ObjectJson.getBytes(), ToolMaster.getCodeKey(userId));
                params.put("data", DESCoder.encryptBASE64(data));


            }catch (Exception e){
                e.printStackTrace();
            }

            HttpClient.getClient().addHeader("userId", (userId));

            HttpClient.atomicPost(WithdrawActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/TransferWallet", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    onTransferWalletResult(s);
                }
            });
        }
    };

    Runnable transferWalletALIPAY_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            String userId = ((MyApplication)getApplication()).getCurrentUser(WithdrawActivity.this).getUserId();

            // 产生个订单号
            String orderNo = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + userId;

            HashMap<String, String> mapData = new HashMap<>();

            mapData.put("userId", userId);
            mapData.put("passWord", mETLoginPassword.getText().toString());
            mapData.put("lines", mETChargeNum.getText().toString());

            String ObjectJson = ToolMaster.gsonInstance().toJson(mapData);
            try{
                byte[] data = DESCoder.encrypt(ObjectJson.getBytes(), ToolMaster.getCodeKey(userId));
                params.put("data", DESCoder.encryptBASE64(data));


            }catch (Exception e){
                e.printStackTrace();
            }

            HttpClient.getClient().addHeader("userId", (userId));

            HttpClient.atomicPost(WithdrawActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/alipayTransfer", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    onTransferWalletResult(s);
                }
            });
        }
    };

    private void onTransferWalletResult(String s){
        try{
            int result = Integer.parseInt(s);
            switch (result){
                case 1:     // 提现成功
                    UIHelper.toast(this, "提现申请已提交");
                    break;
                case 0:     // 提现错误
                    UIHelper.toast(this, "提现申请失败");
                    break;
                case 2:     // 提现现金不足
                    UIHelper.toast(this, "提现现金不足");
                    break;
                case 3:     // 没有绑定微信账号
                    UIHelper.toast(this, "没有绑定微信账号");
                    break;
                case 4:
                    UIHelper.toast(this, "密码不正确");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_withdraw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_withdraw_unbind:
                onUnbind();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void onUnbind(){
        if(mWithdrawType == WITHDRAW_TYPE_ALIPAY){
            handler.post(unbindAlipay_r);
        }
    }

    Runnable unbindAlipay_r = new Runnable() {
        @Override
        public void run() {
            progressDialog.show();

            HttpParams params = new HttpParams();

            String userId = ((MyApplication)getApplication()).getCurrentUser(WithdrawActivity.this).getUserId();

            params.put("userId", userId);

            HttpClient.atomicPost(WithdrawActivity.this, HttpUrlConfig.URL_ROOT + "Wallet/ClearalipayId", params, new HttpClient.MyHttpHandler() {
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    progressDialog.dismiss();
                    UIHelper.toast(WithdrawActivity.this, "连接服务器失败");
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    progressDialog.dismiss();
                    if(s != null && s.compareTo("SUCCESS") == 0){
                        UIHelper.toast(WithdrawActivity.this, "解除支付宝绑定成功");
                        finish();
                    }else{
                        UIHelper.toast(WithdrawActivity.this, "解除支付宝绑定失败");
                    }
                }
            });
        }
    };

}
