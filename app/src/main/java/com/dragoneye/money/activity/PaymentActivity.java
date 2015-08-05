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
import com.dragoneye.money.model.ProjectDetailModel;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;

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

        mTVTotalPriceNum.setText( String.valueOf(mInvestPriceNum) );
        mTVStageNum.setText( String.valueOf(mInvestStageNum) );
    }

    @Override
    protected void initViewPager(){
        mDotViewPager = (DotViewPager)findViewById(R.id.investment_project_detail_dot_viewpager);
    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString());
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
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
