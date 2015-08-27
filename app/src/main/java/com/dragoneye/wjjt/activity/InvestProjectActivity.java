package com.dragoneye.wjjt.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.DotViewPagerActivity;
import com.dragoneye.wjjt.application.AppInfoManager;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.ProjectDetailModel;
import com.dragoneye.wjjt.model.EarningModel;
import com.dragoneye.wjjt.protocol.InvestProjectProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.view.DotViewPager;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InvestProjectActivity extends DotViewPagerActivity implements View.OnClickListener {

    public static final String EXTRA_PROJECT_MODEL = "EXTRA_PROJECT_MODEL";

    private TextView mTextViewConfirm;
    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;
    private LinearLayout mLinearLayoutResultRoot;
    private LinearLayout mLinearLayoutProportion;
    private ProjectDetailModel mProjectDetailModel;

    private TextView mTVStageInfo;
    private TextView mTVTargetFund;
    private TextView mTVTargetFundNum;

    private View mIVLeadAdd;
    private View mIVLeadSubtract;
    private View mIVFallowAdd;
    private View mIVFallowSubtract;
    private TextView mTVLeadStage;
    private TextView mTVFallowStage;
    private TextView mTVMaxFallowPrice;
    private EditText mETInvestPrice;
//    private int mProjectStageMaxNum;
    private int mSelectedLeadStageNum;
    private int mSelectedFallowStageNum;
    private int mLeadInvestPrice;
    private int mFallowInvestPrice;
    private int mFallowInvestTicketLeft;
    private int mFallowInvestMaxPrice = 0;
    private int mFallowStageLeft;
    private int mLeadStageLeft;

    private View mLLLeadButton;
    private View mLLLeadPanel;
    private TextView mTVLeadInvestInfo;
    private ImageView mIVLeadArrow;
    private View mLLFallowButton;
    private View mLLFallowPanel;
    private ImageView mIVFallowArrow;
    private ScrollView mScrollView;

    private ArrayList<EarningModel> mSrEarningModels = new ArrayList<>();
    private ArrayList<EarningModel> mBrEarningModels = new ArrayList<>();

    private Handler handler = new Handler();

    ProgressDialog mProgressDialog;

    int flInvestType;
    int flInvestStageNum;
    int flInvestPriceNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mProjectDetailModel = (ProjectDetailModel)intent.getSerializableExtra(EXTRA_PROJECT_MODEL);
        setIsNeedLoadingFeature(true);
        setContentView(R.layout.home_investment_listview_detail);
        initView();
        initData();
    }

    @Override
    protected void initViewPager(){
        // 图片浏览控件
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

    private void initView(){
        mProgressDialog = new ProgressDialog(this);
        mTVStageInfo = (TextView)findViewById(R.id.textView2);
        mTVTargetFund = (TextView)findViewById(R.id.textView3);
        mTVTargetFundNum = (TextView)findViewById(R.id.textView5);

        mScrollView = (ScrollView)findViewById(R.id.invest_project_scrollView);

        // 确认投资按钮
        mTextViewConfirm = (TextView)findViewById(R.id.invest_project_tv_confirm);
        mTextViewConfirm.setOnClickListener(this);

        // 投资进度条和标签
        mProgressBar = (ProgressBar)findViewById(R.id.invest_project_progressbar);
        mTextViewProjectProgress = (TextView)findViewById(R.id.invest_project_tv_project_progress);

        // 投资收益几率显示
        mLinearLayoutProportion = (LinearLayout)findViewById(R.id.investment_ll_proportion_layout);
        mLinearLayoutResultRoot = (LinearLayout)findViewById(R.id.investment_ll_result_root);

        // 投资期数显示标签
        mTVLeadStage = (TextView)findViewById(R.id.invest_project_tv_leadStage);
        mTVFallowStage = (TextView)findViewById(R.id.invest_project_tv_fallowStage);

        mTVMaxFallowPrice = (TextView)findViewById(R.id.invest_project_tv_maxFallowPrice);

        // 跟投数量输入控件
        mETInvestPrice = (EditText)findViewById(R.id.invest_project_et_price_num);
        mETInvestPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    int price = Integer.parseInt(s.toString());
                    if( price > mFallowInvestMaxPrice){
                        AlertDialog alertDialog = new AlertDialog.Builder(InvestProjectActivity.this)
                                .setTitle("数额过大")
                                .setMessage("输入的金额超过了可跟投的最大金额,请更正")
                                .setPositiveButton("确定", null)
                                .create();
                        alertDialog.show();
                        s.delete(s.length() - 1, s.length());
                        mETInvestPrice.setText(s);
                        return;
                    }
                    updateSrEarningProportion(Integer.parseInt(s.toString()));
                }catch (Exception e){
                    updateSrEarningProportion(0);
                }
            }
        });

        // 领投面板
        mLLLeadButton = findViewById(R.id.investment_ll_leadButton);
        mLLLeadButton.setOnClickListener(this);
        mLLLeadPanel = findViewById(R.id.investment_ll_leadPanel);
        mIVLeadAdd = findViewById(R.id.invest_project_iv_leadAdd);
        mIVLeadAdd.setOnClickListener(this);
        mIVLeadSubtract = findViewById(R.id.invest_project_iv_leadSubstract);
        mIVLeadSubtract.setOnClickListener(this);
        mIVLeadArrow = (ImageView)findViewById(R.id.invest_project_iv_leadArrow);
        mTVLeadInvestInfo = (TextView)findViewById(R.id.invest_project_tv_leadInvestInfo);

        // 跟投面板
        mLLFallowButton = findViewById(R.id.investment_ll_fallowButton);
        mLLFallowButton.setOnClickListener(this);
        mLLFallowPanel = findViewById(R.id.investment_ll_fallowPanel);
        mIVFallowAdd = findViewById(R.id.invest_project_iv_fallowAdd);
        mIVFallowAdd.setOnClickListener(this);
        mIVFallowSubtract = findViewById(R.id.invest_project_iv_fallowSubtract);
        mIVFallowSubtract.setOnClickListener(this);
        mIVFallowArrow = (ImageView)findViewById(R.id.invest_project_iv_fallowArrow);

        String title = mProjectDetailModel.getName();
        if( title.length() > 10 ){
            title = title.substring(0, 10) + "...";
        }
        setTitle(title);
    }

    private void initData(){


//        mProjectStageMaxNum = mProjectDetailModel.getTotalStage() - mProjectDetailModel.getCurrentStage() + 1;


        String strCurrentStage = String.format(getString(R.string.project_list_item_stage_info,
                mProjectDetailModel.getCurrentStage() + "/" + mProjectDetailModel.getTotalStage()));
        mTVStageInfo.setText( strCurrentStage );

        String strTargetFund = getString(R.string.project_list_item_target_fund);
        mTVTargetFund.setText(strTargetFund);

        mTVTargetFundNum.setText(ToolMaster.convertToPriceString(mProjectDetailModel.getTargetFund()));

        resetLead();
        resetFallow();
        setLeadStageNum(0);
        setFallowStageNum(0);


        int progress = (int)(mProjectDetailModel.getCurrentFund() / (float)mProjectDetailModel.getTargetFund() * 100);
        String strProgress = getString(R.string.project_list_item_progress) + "%" + progress;
        mTextViewProjectProgress.setText(strProgress);
        mProgressBar.setProgress(progress);

        handler.post(getInvestInfo_r);
        setStartLoading();
    }

    @Override
    public void onRetryLoading(){
        handler.post(getInvestInfo_r);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.invest_project_tv_confirm:
                onConfirm();
                break;
            case R.id.invest_project_iv_leadSubstract:
                onLeadSubtract();
                break;
            case R.id.invest_project_iv_leadAdd:
                onLeadAdd();
                break;
            case R.id.invest_project_iv_fallowAdd:
                onFallowAdd();
                break;
            case R.id.invest_project_iv_fallowSubtract:
                onFallowSubtract();
                break;
            case R.id.investment_ll_leadButton:
                onLead();
                break;
            case R.id.investment_ll_fallowButton:
                onFallow();
                break;
        }
    }

    private void onLead(){
        resetFallow();
        if( mLLLeadPanel.getVisibility() == View.VISIBLE ){
            mLLLeadPanel.setVisibility(View.GONE);
            mIVLeadArrow.setRotation(-90.0f);
        }else {
            mLLLeadPanel.setVisibility(View.VISIBLE);
            mIVLeadArrow.setRotation(0);
            mSelectedLeadStageNum = 1;
            setLeadStageNum(mSelectedLeadStageNum);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private void onFallow(){
        resetLead();
        if( mLLFallowPanel.getVisibility() == View.VISIBLE ){
            mLLFallowPanel.setVisibility(View.GONE);
            mIVFallowArrow.setRotation(-90.0f);
        }else {
            mLLFallowPanel.setVisibility(View.VISIBLE);
            mIVFallowArrow.setRotation(0);
            mSelectedFallowStageNum = 1;
            setFallowStageNum(mSelectedFallowStageNum);
            if( mFallowInvestMaxPrice >= 1 ){
                mETInvestPrice.setText(String.valueOf(1));
                mETInvestPrice.setSelection(mETInvestPrice.getText().length());
                updateSrEarningProportion(1);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    private void onLeadAdd(){
        if( mSelectedLeadStageNum < mLeadStageLeft ){
            mSelectedLeadStageNum++;
            setLeadStageNum(mSelectedLeadStageNum);
        }
    }

    private void onLeadSubtract(){
        if( mSelectedLeadStageNum > 0 ){
            mSelectedLeadStageNum--;
            setLeadStageNum(mSelectedLeadStageNum);
        }
    }

    private void onFallowAdd(){
        if( mSelectedFallowStageNum < mFallowStageLeft ){
            mSelectedFallowStageNum++;
            setFallowStageNum(mSelectedFallowStageNum);
        }
    }

    private void onFallowSubtract(){
        if( mSelectedFallowStageNum > 0 ){
            mSelectedFallowStageNum--;
            setFallowStageNum(mSelectedFallowStageNum);
        }
    }

    private void resetLead(){
        if( mSelectedLeadStageNum != 0 ){
            setLeadStageNum(0);
        }
        mSelectedLeadStageNum = 0;
        mLLLeadPanel.setVisibility(View.GONE);
        mIVLeadArrow.setRotation(-90.0f);
        updateBrEarningProportion(0);
    }

    private void resetFallow(){
        if( mSelectedFallowStageNum != 0 ){
            setFallowStageNum(0);
        }
        mSelectedFallowStageNum = 0;
        mLLFallowPanel.setVisibility(View.GONE);
        mIVFallowArrow.setRotation(-90.0f);
        mETInvestPrice.setText("");
        updateSrEarningProportion(0);
    }

    private void setLeadStageNum(int num){
        mTVLeadStage.setText(String.format("%d/%d", num, mLeadStageLeft));
        updateBrEarningProportion(num);
    }

    private void setFallowStageNum(int num){
        mTVFallowStage.setText(String.format("%d/%d", num, mFallowStageLeft));
    }

    Runnable getInvestInfo_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();
            httpParams.put(InvestProjectProtocol.GET_INVEST_INFO_PARAM_ACTIVITY_STAGE_ID, mProjectDetailModel.getActivityStageId());

            HttpClient.atomicPost(InvestProjectActivity.this, InvestProjectProtocol.URL_GET_INVEST_INFO,
                    httpParams, new HttpClient.MyHttpHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                            finishLoading(false);
                        }
                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            String response = HttpClient.getValueFromHeader(headers, "response");
                            onGetInvestInfoResult(response, s);
                        }
                    });
        }
    };

    private void onGetInvestInfoResult(String response, String result){
        if( response == null || result == null ){
            finishLoading(false);
            return;
        }

        switch (response){
            case InvestProjectProtocol.GET_INVEST_INFO_SUCCESS:
                try{
                    JSONObject object = new JSONObject(result);
                    mLeadInvestPrice = object.getInt("TotalLinePeoples");
                    mFallowInvestPrice = object.getInt("TotalLines");;
                    JSONArray srEarnings = object.getJSONArray("SREarning");
                    mSrEarningModels.clear();
                    for(int i = 0; i < srEarnings.length(); i++){
                        JSONObject object1 = srEarnings.getJSONObject(i);
                        int earningType = object1.getInt("srEarningType");
                        if( earningType == 2 ){
                            EarningModel earningModel = new EarningModel();
                            earningModel.setNum( srEarnings.getJSONObject(i).getInt("srEarningNum"));
                            earningModel.setPrice(srEarnings.getJSONObject(i).getInt("srEarningPrice"));
                            if( earningModel.getPrice() > mFallowInvestMaxPrice){
                                mFallowInvestMaxPrice = earningModel.getPrice();
                            }
                            mSrEarningModels.add(earningModel);
                        }
                    }
                    Collections.sort(mSrEarningModels, new Comparator<EarningModel>() {
                        @Override
                        public int compare(EarningModel lhs, EarningModel rhs) {
                            return lhs.getPrice() > rhs.getPrice() ? -1 : 0;
                        }
                    });

                    JSONArray brEarnings = object.getJSONArray("EarningPeoples");
                    mBrEarningModels.clear();
                    for( int i = 0; i < brEarnings.length(); i++ ){
                        JSONObject object1 = brEarnings.getJSONObject(i);
                        int earningType = object1.getInt("srEarningType");
                        if( earningType == 1 ){
                            EarningModel earningModel = new EarningModel();
                            earningModel.setNum( brEarnings.getJSONObject(i).getInt("srEarningNum"));
                            earningModel.setPrice(brEarnings.getJSONObject(i).getInt("srEarningPrice"));
                            mBrEarningModels.add(earningModel);
                        }
                    }
                    String leadInvestInfo = String.format(getString(R.string.invest_project_lead_invest_info),
                            ToolMaster.convertToPriceString(mLeadInvestPrice));
                    mTVLeadInvestInfo.setText(leadInvestInfo);

                    handler.post(getStageNumLeft_r);
                }catch (Exception e){
                    e.printStackTrace();
                    finishLoading(false);
                }
                break;
            case InvestProjectProtocol.GET_INVEST_INFO_FAILED:
                UIHelper.toast(this, "此项目已过期");
                finish();
                break;
        }
    }

    Runnable getStageNumLeft_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();
            httpParams.put("installmentActivityID", mProjectDetailModel.getActivityStageId());

            HttpClient.atomicPost(InvestProjectActivity.this, HttpUrlConfig.URL_ROOT + "PurchaseInAdvance/PurchaseActivityNum", httpParams, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if( s == null ){
                        finishLoading(false);
                        return;
                    }

                    try{
                        JSONArray array = new JSONArray(s);
                        mFallowInvestTicketLeft = array.getInt(0);
                        mFallowStageLeft = array.getInt(1);
                        mLeadStageLeft = array.getInt(2);
                        if(mFallowInvestTicketLeft < mFallowInvestMaxPrice){
                            mFallowInvestMaxPrice = mFallowInvestTicketLeft;
                        }

                        if( mLeadStageLeft == 0 && mFallowStageLeft == 0 ){
                            UIHelper.toast(InvestProjectActivity.this, "此项目筹款已结束");
                            setResult(RESULT_OK);
                            finish();
                            return;
                        }

                        if( mLeadStageLeft == 0 ){
                            mLLLeadButton.setVisibility(View.GONE);
                            mLLLeadPanel.setVisibility(View.GONE);
                        }

                        if( mFallowStageLeft == 0 ){
                            mLLFallowButton.setVisibility(View.GONE);
                            mLLFallowPanel.setVisibility(View.GONE);
                        }


                        mTVMaxFallowPrice.setText(String.format("(最大跟投数额:%d)", mFallowInvestMaxPrice));
                        finishLoading(true);
                    }catch (Exception e){
                        finishLoading(false);
                        e.printStackTrace();
                    }
                    finishLoading(true);
                }
            });
        }
    };

    private void updateSrEarningProportion(int investPrice){
        mLinearLayoutResultRoot.removeAllViews();
        mLinearLayoutProportion.setVisibility(View.VISIBLE);
        if( investPrice == 0 ){
            mLinearLayoutProportion.setVisibility(View.GONE);
        }else {
            for(EarningModel model : mSrEarningModels){
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 5, 0, 0);
                mLinearLayoutResultRoot.addView(textView, layoutParams);

                float proportion = (float)investPrice / mFallowInvestPrice * model.getNum() * 100;
                setProportion(textView, proportion, model.getPrice(), false);
            }
        }
    }

    private void updateBrEarningProportion(int stageNum){
        mLinearLayoutResultRoot.removeAllViews();
        mLinearLayoutProportion.setVisibility(View.VISIBLE);
        if( stageNum == 0 ){
            mLinearLayoutProportion.setVisibility(View.GONE);
        }else {
            for(EarningModel model : mBrEarningModels){
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 5, 0, 0);
                mLinearLayoutResultRoot.addView(textView, layoutParams);

                float proportion = 1.0f / mBrEarningModels.size() * 100;
                setProportion(textView, proportion, model.getPrice(), true);
            }
        }

    }

    private void setProportion(TextView textView, float proportion, int price, boolean isBr){
        if( proportion > 99.99f ){
            proportion = 99.99f;
        }
        if( proportion == 0 ){
            proportion = 0.1f;
        }

        if( proportion < 1.0f || isBr ){
            int ip = (int)(100 / proportion);
            String text = String.format("1/%d几率获得%s元收益", ip, price);
            textView.setText(text);
        }else {
            String text = String.format("%.2f%%几率获得%s元收益", proportion, price);
            textView.setText(text);
        }
    }



    private void onConfirm(){
        if( !checkUserInput() ){
            return;
        }

        int investType = 0;
        int investStageNum = 0;
        int investPriceNum = 0;
        String tips = "您确定要";
        if( isLeadInvest() ){
            tips += "领投" + mSelectedLeadStageNum + "期，每期金额为￥"
                    + mLeadInvestPrice + "，共计￥" + mLeadInvestPrice * mSelectedLeadStageNum + "。";
            investType = InvestProjectProtocol.INVEST_TYPE_LEAD;
            investStageNum = mSelectedLeadStageNum;
            investPriceNum = mLeadInvestPrice;
        }else {
            tips += "跟投" + mSelectedFallowStageNum + "期，每期金额为￥"
                    + getFallowPriceNum() + "，共计￥" + getFallowPriceNum() * mSelectedFallowStageNum + "。";
            investType = InvestProjectProtocol.INVEST_TYPE_FALLOW;
            investPriceNum = getFallowPriceNum();
            investStageNum = mSelectedFallowStageNum;
        }

        flInvestType = investType;
        flInvestStageNum = investStageNum;
        flInvestPriceNum = investPriceNum;

        new AlertDialog.Builder(this).setTitle(AppInfoManager.getApplicationName(this))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(InvestProjectActivity.this, PaymentActivity.class);
//                        intent.putExtra(EXTRA_PROJECT_MODEL, mProjectDetailModel);
//                        intent.putExtra(PaymentActivity.EXTRA_INVEST_TYPE, flInvestType);
//                        intent.putExtra(PaymentActivity.EXTRA_INVEST_STAGE_NUM, flInvestStageNum);
//                        intent.putExtra(PaymentActivity.EXTRA_INVEST_PRICE_NUM, flInvestPriceNum);
//                        startActivity(intent);
                        onInvest(flInvestType, flInvestStageNum, flInvestPriceNum, 1);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMessage(tips).show();

    }

    private boolean checkUserInput(){
        if( isLeadInvest() ){
            return true;
        }else if( isFallowInvest() ){
            if( mETInvestPrice.getText().length() == 0 ){
                UIHelper.toast(this, "请输入要跟投的数量");
                return false;
            }
        }else {
            UIHelper.toast(this, "请选你的领投或跟投期数");
            return false;
        }

        return true;
    }

    private int getFallowPriceNum(){
        String num = mETInvestPrice.getText().toString();
        if( num.isEmpty() )
            return 0;

        return Integer.parseInt(num);
    }

    private boolean isLeadInvest(){
        return mSelectedLeadStageNum > 0;
    }

    private boolean isFallowInvest(){
        return mSelectedFallowStageNum > 0;
    }

    private void onInvest(int investType, final int investStageNum, final int investPriceNum, int messageType){
        mProgressDialog.setMessage("正在提交");
        mProgressDialog.show();
        HttpParams params = new HttpParams();

        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser(this).getUserId());
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_ACTIVITY_STAGE_ID, mProjectDetailModel.getActivityStageId());
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_TYPE, investType);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_STAGE_NUM, investStageNum);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_PRICE_NUM, investPriceNum);
        params.put("MessageType", messageType);

        HttpClient.atomicPost(this, InvestProjectProtocol.URL_INVEST_PROJECT, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                mProgressDialog.dismiss();
            }
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                mProgressDialog.dismiss();
                onInvestResult(s, investStageNum, investPriceNum);
            }
        });
    }

    private void onInvestResult(String s, int investStageNum, int investPriceNum){
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

        AlertDialog alertDialog;
        switch (resultCode){
            case InvestProjectProtocol.INVEST_RESULT_SUCCESS:
                showSuccessDialog(investStageNum, investPriceNum);
                break;
            case InvestProjectProtocol.INVEST_RESULT_SUCCESS_AND_REFRESH:
                showSuccessDialog(investStageNum, investPriceNum);
                setResult(RESULT_OK, null);
                break;
            case InvestProjectProtocol.INVEST_RESULT_IMPROVE_INFO:
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("个人信息需完善")
                        .setMessage("需要先完善个人信息，是否现在去完善?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(InvestProjectActivity.this, ImproveUserInfoActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("否", null)
                        .create();
                alertDialog.show();
                break;
            case InvestProjectProtocol.INVEST_RESULT_FAILED:
                UIHelper.toast(this, "投资失败");
                break;
            case InvestProjectProtocol.INVEST_RESULT_MONEY_NOT_ENOUGH:
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("资金不足")
                        .setMessage("资金不足，是否前往充值?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChargeActivity.CallActivity(InvestProjectActivity.this);
                            }
                        })
                        .setNegativeButton("否", null)
                        .create();
                alertDialog.show();
                break;
            case InvestProjectProtocol.INVEST_RESULT_TICKET_SOLD_OUT:
                UIHelper.toast(this, "期或票不够");
                break;
            case InvestProjectProtocol.INVEST_RESULT_THIS_STAGE_NOT_ENOUGH:
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("本期已满")
                        .setMessage("本期已满，是否预购下一期?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onInvest(flInvestType, flInvestStageNum, flInvestPriceNum, 2);
                            }
                        })
                        .setNegativeButton("否", null)
                        .create();
                alertDialog.show();
                break;
            default:
                UIHelper.toast(this, "服务器异常");
                break;
        }
    }

    private void showSuccessDialog(int investStageNum, int investPriceNum){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialog = inflater.inflate(R.layout.home_investment_listview_order_confirm, null);

        final TextView pricePerStage = (TextView)dialog.findViewById(R.id.payment_tv_totalPrice);
        final TextView stageNum = (TextView)dialog.findViewById(R.id.payment_tv_stageNum);
        final TextView totalPrice = (TextView)dialog.findViewById(R.id.textView24);
        final TextView title = (TextView)dialog.findViewById(R.id.payment_tv_stageInfo);
        final TextView confirm = (TextView)dialog.findViewById(R.id.payment_tv_goToPay);

        String titleString = mProjectDetailModel.getName();
        titleString = String.format("您已入资 %s 项目", titleString);
        title.setText(titleString);

        pricePerStage.setText(String.valueOf(investPriceNum));
        stageNum.setText(String.valueOf(investStageNum));
        totalPrice.setText(String.valueOf(investStageNum * investPriceNum));

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setView(dialog)
            .create();
        alertDialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invest_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_invest_project_detail) {
            ArrayList<String> img = new ArrayList<>();
            try{
                img = ToolMaster.gsonInstance().fromJson(mProjectDetailModel.getImageUrl(),
                        new TypeToken<ArrayList<String>>(){}.getType());
            }catch (Exception e){

            }
            ProjectDetailActivity.CallProjectDetailActivity(this, mProjectDetailModel.getActivityId(), mProjectDetailModel.getName(),
                    img, mProjectDetailModel.getTargetFund(), mProjectDetailModel.getCurrentFund());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
