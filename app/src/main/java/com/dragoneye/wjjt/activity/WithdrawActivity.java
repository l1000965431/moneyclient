package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.app.AlertDialog;
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

    EditText mETChargeNum;
    TextView mTVWalletBalance;
    EditText mETLoginPassword;

    Handler handler = new Handler();

    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, WithdrawActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsNeedLoadingFeature(true);
        setContentView(R.layout.home_self_group_top);

        TextView tv = (TextView)findViewById(R.id.textView13);
        tv.setText("请输入您要提现的金额：");

        View passwordLayout = findViewById(R.id.charge_activity_password_layout);
        passwordLayout.setVisibility(View.VISIBLE);


        TextView textView = (TextView)findViewById(R.id.invest_project_tv_confirm);
        textView.setOnClickListener(this);

        mETChargeNum = (EditText)findViewById(R.id.charge_activity_et_chargeNum);
        mTVWalletBalance = (TextView)findViewById(R.id.charge_tv_wallet_balance);
        mETLoginPassword = (EditText)findViewById(R.id.charge_et_login_password);

        setStartLoading();
        handler.post(getWalletBalance_r);

//        setStatusBarColor(findViewById(R.id.statusBarBackground), Color.GREEN);
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

                    handler.post(checkIsBindWeChat_r);
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
//                        AlertDialog dialog = new AlertDialog.Builder(WithdrawActivity.this)
//                                .setPositiveButton("前往关注", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
////                                        WXTextObject textObject = new WXTextObject();
////                                        textObject.text = "一条测试";
////
////                                        WXMediaMessage mediaMessage = new WXMediaMessage();
////                                        mediaMessage.mediaObject = textObject;
////                                        mediaMessage.description = "一条测试";
////
////                                        SendMessageToWX.Req req = new SendMessageToWX.Req();
////                                        req.transaction = String.valueOf(System.currentTimeMillis());
////                                        req.message = mediaMessage;
////
////                                        ((MyApplication) getApplication()).getWXAPI().sendReq(req);
////                                        finish();
//                                        finishLoading(true);
//                                    }
//                                })
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                    }
//                                })
//                                .setMessage("目前只支持微信钱包提现；完成该服务需要您先关注微聚竞投的微信公众账号！")
//                                .create();
//                        dialog.show();
                        WxBindActivity.CallActivity(WithdrawActivity.this);
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
                handler.post(transferWallet_r);
                break;
        }
    }

    Runnable transferWallet_r = new Runnable() {
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

    private void onTransferWalletResult(String s){
        try{
            int result = Integer.parseInt(s);
            switch (result){
                case 1:     // 提现成功
                    UIHelper.toast(this, "提现成功");
                    break;
                case 0:     // 提现错误
                    UIHelper.toast(this, "提现错误");
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

}
