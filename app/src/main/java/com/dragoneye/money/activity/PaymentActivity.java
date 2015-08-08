package com.dragoneye.money.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.DotViewPagerActivity;
import com.dragoneye.money.application.MyApplication;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.model.ProjectDetailModel;
import com.dragoneye.money.protocol.InvestProjectProtocol;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;

import java.util.ArrayList;

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
        setContentView(R.layout.home_investment_listview_order);
        initView();
        initData();
    }

    private void initView(){
        mTVStageInfo = (TextView)findViewById(R.id.payment_tv_stageInfo);
        mTVStageNum = (TextView)findViewById(R.id.payment_tv_stageNum);
        mTVTotalPriceNum = (TextView)findViewById(R.id.payment_tv_totalPrice);

        View goToPay = findViewById(R.id.payment_tv_goToPay);
        goToPay.setOnClickListener(this);
    }

    private void initData(){
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

        String strCurrentStage = String.format(getString(R.string.project_list_item_stage_info,
                mProjectDetailModel.getCurrentStage() + "/" + mProjectDetailModel.getTotalStage()));
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
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString());
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString());
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString());
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.payment_tv_goToPay:
                onPay();
                break;
        }
    }

    private void onPay(){
        onInvest(mInvestType, mInvestStageNum, mInvestPriceNum);
    }

    private void onInvest(int investType, int investStageNum, int investPriceNum){
        HttpParams params = new HttpParams();

        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser().getUserId());
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
            default:
                UIHelper.toast(this, "服务器异常");
                break;
        }
    }
}
