package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.PaymentProtocol;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.pingplusplus.libone.PayActivity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChargeActivity extends BaseActivity implements View.OnClickListener{

    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, ChargeActivity.class);
        activity.startActivity(intent);
    }

    EditText mETChargeNum;
    TextView mTVWalletBalance;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsNeedLoadingFeature(true);
        setContentView(R.layout.home_self_group_top);

        TextView textView = (TextView)findViewById(R.id.invest_project_tv_confirm);
        textView.setOnClickListener(this);

        mETChargeNum = (EditText)findViewById(R.id.charge_activity_et_chargeNum);
        mTVWalletBalance = (TextView)findViewById(R.id.charge_tv_wallet_balance);

        setStartLoading();
        handler.post(getWalletBalance_r);

//        setStatusBarColor(findViewById(R.id.statusBarBackground), Color.GREEN);
    }

    @Override
    protected void onRetryLoading(){
        handler.post(getWalletBalance_r);
    }

    public void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    Runnable getWalletBalance_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication) getApplication()).getCurrentUser(ChargeActivity.this).getUserId());

            HttpClient.atomicPost(ChargeActivity.this, UserProtocol.URL_GET_WALLET_BALANCE, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        finishLoading(false);
                        return;
                    }

                    finishLoading(true);
                    int balance = Integer.parseInt(s);
                    mTVWalletBalance.setText(String.format("钱包余额：%s", ToolMaster.convertToPriceString(balance)));
                }
            });
        }
    };

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.invest_project_tv_confirm:
                onCharge();
                finish();
                break;
        }
    }

    private void onCharge(){
        PayActivity.SHOW_CHANNEL_WECHAT = true;
        //打开支付宝按钮
        PayActivity.SHOW_CHANNEL_ALIPAY = true;

        String userId = ((MyApplication)getApplication()).getCurrentUser(this).getUserId();

        // 产生个订单号
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + userId;

        int num = 0;
        try{
            num = Integer.parseInt(mETChargeNum.getText().toString());
        }catch (Exception e){
            num = 0;
            e.printStackTrace();
        }
        if( num <= 0 ){
            UIHelper.toast(this, "请输入要充值的金额");
            return;
        }

        //计算总金额（以分为单位）
        int amount = num * 100;
        String amountStr = String.format("￥%.2f", amount / 100.0f);
        JSONArray billList = new JSONArray();
        billList.put("人民币: " + " x " + amountStr);
//        billList.put("每期金额: " + " x " + mInvestPriceNum);
//        amount = mInvestStageNum * mInvestPriceNum * 100;

        //自定义的额外信息 选填
        JSONObject extras = new JSONObject();
        try {
            extras.put("UserId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //构建账单json对象
        JSONObject bill = new JSONObject();
        JSONObject displayItem = new JSONObject();
        try {
            displayItem.put("name", "充值信息");
            displayItem.put("contents", billList);
            JSONArray display = new JSONArray();
            display.put(displayItem);
            bill.put("order_no", orderNo);
            bill.put("amount", amount);
            bill.put("display", display);
            bill.put("extras", extras);//该字段选填
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PayActivity.CallPayActivity(this, bill.toString(), PaymentProtocol.URL_PAYMENT);
    }
}
