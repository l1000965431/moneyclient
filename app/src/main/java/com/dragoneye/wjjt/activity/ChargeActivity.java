package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.protocol.PaymentProtocol;
import com.dragoneye.wjjt.tool.UIHelper;
import com.pingplusplus.libone.PayActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_top);

        TextView textView = (TextView)findViewById(R.id.invest_project_tv_confirm);
        textView.setOnClickListener(this);

        mETChargeNum = (EditText)findViewById(R.id.charge_activity_et_chargeNum);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.invest_project_tv_confirm:
                onTest();
                break;
        }
    }

    private void onTest(){
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
