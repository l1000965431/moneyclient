package com.dragoneye.wjjt.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.DotViewPagerActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.ProjectDetailModel;
import com.dragoneye.wjjt.protocol.InvestProjectProtocol;
import com.dragoneye.wjjt.protocol.PaymentProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.view.DotViewPager;
import com.google.gson.reflect.TypeToken;
import com.pingplusplus.libone.PayActivity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentActivity extends DotViewPagerActivity implements View.OnClickListener {

    public static final String EXTRA_INVEST_TYPE = "EXTRA_INVEST_TYPE";
    public static final String EXTRA_INVEST_STAGE_NUM = "EXTRA_INVEST_STAGE_NUM";
    public static final String EXTRA_INVEST_PRICE_NUM = "EXTRA_INVEST_PRICE_NUM";

    private int mInvestType;
    private int mInvestStageNum;
    private int mInvestPriceNum;

    private TextView mTVStageInfo;
    private TextView mTVTotalPriceNum;
    private TextView mTVStageNum;

    private ProjectDetailModel mProjectDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mProjectDetailModel = (ProjectDetailModel)intent.getSerializableExtra(InvestProjectActivity.EXTRA_PROJECT_MODEL);
        mInvestType = intent.getIntExtra(EXTRA_INVEST_TYPE, -1);
        mInvestPriceNum = intent.getIntExtra(EXTRA_INVEST_PRICE_NUM, -1);
        mInvestStageNum = intent.getIntExtra(EXTRA_INVEST_STAGE_NUM , -1);
        if( mInvestType == -1 || mInvestPriceNum == -1 || mInvestStageNum == -1 ){
            UIHelper.toast(this, "参数不正确");
            finish();
            return;
        }
        setContentView(R.layout.home_investment_listview_order);
        initView();
        initData();
    }

    private void initView(){
        mTVStageInfo = (TextView)findViewById(R.id.payment_tv_stageInfo);
        mTVStageNum = (TextView)findViewById(R.id.payment_tv_stageNum);
        mTVTotalPriceNum = (TextView)findViewById(R.id.payment_tv_totalPrice);

        View goToPay = findViewById(R.id.rush_failure_tv_auto_close);
        goToPay.setOnClickListener(this);
    }

    private void initData(){
        String strCurrentStage = String.format(getString(R.string.project_list_item_stage_info,
                (mProjectDetailModel.getCurrentStage() + "/" + mProjectDetailModel.getTotalStage())));
        mTVStageInfo.setText(strCurrentStage);

        mTVTotalPriceNum.setText(String.valueOf(mInvestPriceNum));
        mTVStageNum.setText(String.valueOf(mInvestStageNum));
    }

    @Override
    protected void initViewPager(){
        mDotViewPager = (DotViewPager)findViewById(R.id.investment_project_detail_dot_viewpager);
    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();
        ArrayList<String> imageUrl = ToolMaster.gsonInstance().fromJson(mProjectDetailModel.getImageUrl(),
                new TypeToken<ArrayList<String>>(){}.getType());
        if( imageUrl != null && imageUrl.size() > 0 ){
            mImageUrl = imageUrl;
        }else {
            mImageUrl = new ArrayList<>();
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.icon_albums).toString());
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rush_failure_tv_auto_close:
                onPay();
//                onTest();
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

        //计算总金额（以分为单位）
        int amount = 0;
        JSONArray billList = new JSONArray();
        billList.put("购买期数: " + " x " + mInvestStageNum);
        billList.put("每期金额: " + " x " + mInvestPriceNum);
        amount = mInvestStageNum * mInvestPriceNum * 100;

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
            displayItem.put("name", "详情");
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

    private void onPay(){
        onInvest(mInvestType, mInvestStageNum, mInvestPriceNum);
    }

    private void onInvest(int investType, int investStageNum, int investPriceNum){
        HttpParams params = new HttpParams();

        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser(this).getUserId());
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_ACTIVITY_STAGE_ID, mProjectDetailModel.getActivityStageId());
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_TYPE, investType);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_STAGE_NUM, investStageNum);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_PRICE_NUM, investPriceNum);

        HttpClient.atomicPost(this, InvestProjectProtocol.URL_INVEST_PROJECT, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                onInvestResult(s);
            }
        });
    }

    private void onInvestResult(String s){
        if( s == null ){
            UIHelper.toast(this, "服务器异常");
            return;
        }

        int resultCode = 0;
        try{
            resultCode = Integer.parseInt(s);
        }catch (Exception e){
            UIHelper.toast(this, "服务器异常");
            return;
        }

        switch (resultCode){
            case InvestProjectProtocol.INVEST_RESULT_SUCCESS:
                UIHelper.toast(this, "投资成功");
                break;
            case InvestProjectProtocol.INVEST_RESULT_IMPROVE_INFO:
                UIHelper.toast(this, "需要完善个人信息");
                break;
            case InvestProjectProtocol.INVEST_RESULT_FAILED:
                UIHelper.toast(this, "投资失败");
                break;
            case InvestProjectProtocol.INVEST_RESULT_MONEY_NOT_ENOUGH:
                UIHelper.toast(this, "资金不足");
                break;
            case InvestProjectProtocol.INVEST_RESULT_TICKET_SOLD_OUT:
                UIHelper.toast(this, "期或票不够");
                break;
            default:
                UIHelper.toast(this, "服务器异常");
                break;
        }
    }
}
