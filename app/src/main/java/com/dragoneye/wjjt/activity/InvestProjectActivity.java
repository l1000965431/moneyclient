package com.dragoneye.wjjt.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import android.widget.Toast;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.DotViewPagerActivity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InvestProjectActivity extends DotViewPagerActivity implements View.OnClickListener {

    public static final String EXTRA_PROJECT_MODEL = "EXTRA_PROJECT_MODEL";

    private TextView mTextViewConfirm;
    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;
    private LinearLayout mLinearLayoutSrResultRoot;
    private LinearLayout mLinearLayoutSrProportion;
    private LinearLayout mLinearLayoutBrResultRoot;
    private LinearLayout mLinearLayoutBrProportion;
    private ProjectDetailModel mProjectDetailModel;

    private TextView mTVStageInfo;
    private TextView mTVTargetFund;
    private TextView mTVTargetFundNum;

//    private View mIVLeadAdd;
//    private View mIVLeadSubtract;
//    private View mIVFallowAdd;
//    private View mIVFallowSubtract;
//    private View mIVVTicketAdd;
//    private View mIVVTicketSubtract;
//    private TextView mTVLeadStage;
//    private TextView mTVFallowStage;
    private TextView mTVMaxFallowPrice;
//    private TextView mTVVTicket;
    private EditText mETInvestPrice;
//    private int mProjectStageMaxNum;
    private int mSelectedLeadStageNum;
    private int mSelectedFallowStageNum;
    private int mSelectedVTicketNum;
    private int mVTicketLeft;
    private int mLeadInvestPrice;
    private int mFallowInvestPrice;
    private int mFallowInvestTicketLeft;
    private int mFallowInvestMaxPrice = 0;
    private int mVTicketMaxUse = 0;
    private int mVTicketMaxUseTemp = 0;
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
    private AlertDialog mSuccessDialog;

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

        // 领投投资收益几率显示
        mLinearLayoutBrProportion = (LinearLayout)findViewById(R.id.investment_ll_proportion_layout_br);
        mLinearLayoutBrResultRoot = (LinearLayout)findViewById(R.id.investment_ll_result_root_br);

        // 跟投投资收益几率显示
        mLinearLayoutSrProportion = (LinearLayout)findViewById(R.id.investment_ll_proportion_layout);
        mLinearLayoutSrResultRoot = (LinearLayout)findViewById(R.id.investment_ll_result_root);

        // 投资期数显示标签
//        mTVLeadStage = (TextView)findViewById(R.id.invest_project_tv_leadStage);
//        mTVFallowStage = (TextView)findViewById(R.id.invest_project_tv_fallowStage);

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
//        mIVLeadAdd = findViewById(R.id.invest_project_iv_leadAdd);
//        mIVLeadAdd.setOnClickListener(this);
//        mIVLeadSubtract = findViewById(R.id.invest_project_iv_leadSubstract);
//        mIVLeadSubtract.setOnClickListener(this);
        mIVLeadArrow = (ImageView)findViewById(R.id.invest_project_iv_leadArrow);
        mTVLeadInvestInfo = (TextView)findViewById(R.id.investment_tv_lead_investInfo);

        // 跟投面板
        mLLFallowButton = findViewById(R.id.investment_ll_fallowButton);
        mLLFallowButton.setOnClickListener(this);
        mLLFallowPanel = findViewById(R.id.investment_ll_fallowPanel);
//        mIVFallowAdd = findViewById(R.id.invest_project_iv_fallowAdd);
//        mIVFallowAdd.setOnClickListener(this);
//        mIVFallowSubtract = findViewById(R.id.invest_project_iv_fallowSubtract);
//        mIVFallowSubtract.setOnClickListener(this);
        mIVFallowArrow = (ImageView)findViewById(R.id.invest_project_iv_fallowArrow);

//        mIVVTicketAdd = findViewById(R.id.invest_project_iv_vTicketAdd);
//        mIVVTicketAdd.setOnClickListener(this);
//        mIVVTicketSubtract = findViewById(R.id.invest_project_iv_vTicketSub);
//        mIVVTicketSubtract.setOnClickListener(this);
//        mTVVTicket = (TextView)findViewById(R.id.invest_project_tv_vTicket);

        String title = mProjectDetailModel.getName();
        if( title.length() > 10 ){
            title = title.substring(0, 10) + "...";
        }
        setTitle(title);

        setImageScaleType(ImageView.ScaleType.CENTER_CROP);
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

        int progress = (int)(mProjectDetailModel.getCurrentFund() / (float)mProjectDetailModel.getTargetFund() * 100);
        String strProgress = getString(R.string.project_list_item_progress) + "%" + progress;
        mTextViewProjectProgress.setText(strProgress);
        mProgressBar.setProgress(progress);

        handler.post(getWalletBalance_r);
        setStartLoading();
    }

    @Override
    public void onRetryLoading(){
        handler.post(getWalletBalance_r);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.invest_project_tv_confirm:
//                if(isLeadInvest()){
//                    int stageLeft = mProjectDetailModel.getTotalStage() - mProjectDetailModel.getCurrentStage();
//                    int realStage = mProjectDetailModel.getTotalStage() - stageLeft + 1;
//                    if(mLeadStageLeft <= stageLeft){
//                        AlertDialog alertDialog = new AlertDialog.Builder(this)
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        onConfirm();
//                                    }
//                                })
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                                .setMessage(String.format("当前期已售空，将为您购买第%d期", realStage))
//                                .create();
//                        alertDialog.show();
//                    }
//                }else {
                    onConfirm();
//                }

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
            mSelectedLeadStageNum = 0;
        }else {
            mLLLeadPanel.setVisibility(View.VISIBLE);
            mIVLeadArrow.setRotation(0);
            mSelectedLeadStageNum = mLeadStageLeft >= 1 ? 1 : 0;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
        updateBrEarningProportion(mSelectedLeadStageNum);
    }

    private void onFallow(){
        resetLead();
        if( mLLFallowPanel.getVisibility() == View.VISIBLE ){
            mLLFallowPanel.setVisibility(View.GONE);
            mIVFallowArrow.setRotation(-90.0f);
            mSelectedFallowStageNum = 0;
            updateSrEarningProportion(0);
        }else {
            mLLFallowPanel.setVisibility(View.VISIBLE);
            mIVFallowArrow.setRotation(0);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
            mSelectedFallowStageNum = mFallowStageLeft >= 1 ? 1 : 0;
            updateSrEarningProportion(0);
            if( mFallowInvestMaxPrice >= 1 ){
                mETInvestPrice.setText(String.valueOf(1));
                mETInvestPrice.setSelection(mETInvestPrice.getText().length());
                updateSrEarningProportion(1);
            }
        }
    }

    private void resetLead(){
        mSelectedLeadStageNum = 0;
        mLLLeadPanel.setVisibility(View.GONE);
        mIVLeadArrow.setRotation(-90.0f);
        updateBrEarningProportion(0);
    }

    private void resetFallow(){
        mSelectedFallowStageNum = 0;
        mLLFallowPanel.setVisibility(View.GONE);
        mIVFallowArrow.setRotation(-90.0f);
        mETInvestPrice.setText("");
        updateSrEarningProportion(0);
    }

    Runnable getWalletBalance_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            MyApplication application = (MyApplication)getApplication();
            params.put("userId", application.getCurrentUser(InvestProjectActivity.this).getUserId());
            params.put("token", application.getToken(InvestProjectActivity.this));

            HttpClient.atomicPost(InvestProjectActivity.this, HttpUrlConfig.URL_ROOT + "User/getUserSetInfo", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    finishLoading(false);
                }
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        UIHelper.toast(InvestProjectActivity.this, getString(R.string.http_server_exception));
                        return;
                    }
                    int balance = 0, exp = 0, vTicket = 0, leadTicket = 0;
                    try {
                        JSONObject object = new JSONObject(s);
                        mVTicketLeft = object.getInt("virtualSecurities");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.post(getInvestInfo_r);
                }
            });
        }
    };

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
                    mVTicketMaxUse = object.getInt("MaxVirtualSecuritiesBuy");
                    if(mVTicketMaxUse > mVTicketLeft){
                        mVTicketMaxUse = mVTicketLeft;
                    }
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
                    mTVMaxFallowPrice.setText(String.format("(最大跟投数额:%d)", mFallowInvestMaxPrice));
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
                        int earningType = object1.getInt("earningType");
                        if( earningType == 1 ){
                            EarningModel earningModel = new EarningModel();
                            earningModel.setNum( brEarnings.getJSONObject(i).getInt("num"));
                            earningModel.setPrice(brEarnings.getJSONObject(i).getInt("earningPrice"));
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

                        if( mLeadStageLeft == 0 && mFallowStageLeft == 0 && mFallowInvestTicketLeft == 0 ){
                            UIHelper.toast(InvestProjectActivity.this, "此项目筹款已结束");
                            setResult(RESULT_OK);
                            finish();
                            return;
                        }

                        if( mLeadStageLeft == 0 ){
                            mLLLeadButton.setVisibility(View.GONE);
                            mLLLeadPanel.setVisibility(View.GONE);
                        }

                        if( mFallowStageLeft == 0 && mFallowInvestTicketLeft == 0 ){
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
        mLinearLayoutSrResultRoot.removeAllViews();
        mLinearLayoutSrProportion.setVisibility(View.VISIBLE);
        if( investPrice == 0 ){
            mLinearLayoutSrProportion.setVisibility(View.GONE);
        }else {
            List<TextView> list = getSrProportionTextView(investPrice);
            for(TextView tv : list){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 5, 0, 0);
                mLinearLayoutSrResultRoot.addView(tv, layoutParams);
            }
        }
    }

    private void updateBrEarningProportion(int stageNum){
        mLinearLayoutBrResultRoot.removeAllViews();
        mLinearLayoutBrProportion.setVisibility(View.VISIBLE);
        if( stageNum == 0 ){
            mLinearLayoutBrProportion.setVisibility(View.GONE);
        }else {
            List<TextView> list = getBrProportionTextView(stageNum);
            for(TextView tv : list){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 5, 0, 0);
                mLinearLayoutBrResultRoot.addView(tv, layoutParams);
            }
        }

    }

    private List<TextView> getSrProportionTextView(int investPrice){
        List<TextView> list = new ArrayList<>();
        for(EarningModel model : mSrEarningModels){
            TextView textView = new TextView(this);
            float proportion = (float)investPrice / mFallowInvestPrice * model.getNum() * 100;
            setProportion(textView, proportion, model.getPrice(), false, 0);

            list.add(textView);
        }
        return list;
    }

    private List<TextView> getBrProportionTextView(int stageNum){
        List<TextView> list = new ArrayList<>();
        for(EarningModel model : mBrEarningModels){
            TextView textView = new TextView(this);
            float proportion = 1.0f / mBrEarningModels.size() * 100;
            setProportion(textView, proportion, model.getPrice(), true, mBrEarningModels.size());

            list.add(textView);
        }
        return list;
    }

    private void setProportion(TextView textView, float proportion, int price, boolean isBr, int brSize){
        if( proportion > 99.99f ){
            proportion = 99.99f;
        }
        if( proportion == 0 ){
            proportion = 0.1f;
        }

        if(isBr){
            String text = String.format("1/%d几率获得%s元收益", brSize, price);
            textView.setText(text);
        }else if( proportion < 1.0f ){
            int ip = (int)(100 / proportion + 0.5f);
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

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialog = inflater.inflate(R.layout.home_investment_listview_order, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialog)
                .show();

        final TextView tvTotalPrice = (TextView)dialog.findViewById(R.id.invest_confirm_tv_totalPrice);
        final View llVTicketPanel = dialog.findViewById(R.id.invest_confirm_ll_vTicketPanel);
        final TextView tvVTicket = (TextView)dialog.findViewById(R.id.invest_confirm_tv_vTicketNum);
        final int fallowInvestPrice = getFallowPriceNum();

        // 期数选择按钮
        final TextView tvStageInfo = (TextView)dialog.findViewById(R.id.invest_confirm_tv_vStageNum);
        final View vStageSub = dialog.findViewById(R.id.invest_confirm_iv_vStageSub);
        final View vStageAdd = dialog.findViewById(R.id.invest_confirm_iv_vStageAdd);
        if( isLeadInvest() ){
            mSelectedLeadStageNum = 1;
            tvStageInfo.setText(String.format("%d/%d", mSelectedLeadStageNum, mLeadStageLeft));
            tvTotalPrice.setText(ToolMaster.convertRMBPriceString(mSelectedLeadStageNum * mLeadInvestPrice));
            flInvestType = InvestProjectProtocol.INVEST_TYPE_LEAD;
        }else {
            mSelectedFallowStageNum = 1;
            tvStageInfo.setText(String.format("%d/%d", mSelectedFallowStageNum, mFallowStageLeft));
            tvTotalPrice.setText(ToolMaster.convertRMBPriceString(mSelectedFallowStageNum * fallowInvestPrice));
            flInvestType = InvestProjectProtocol.INVEST_TYPE_FALLOW;
        }
        vStageSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isLeadInvest() ){
                    if( mSelectedLeadStageNum > 1 ){
                        mSelectedLeadStageNum--;
                        tvStageInfo.setText(String.format("%d/%d", mSelectedLeadStageNum, mLeadStageLeft));
                        tvTotalPrice.setText(ToolMaster.convertRMBPriceString(mSelectedLeadStageNum * mLeadInvestPrice));
                    }
                }else {
                    if( mSelectedFallowStageNum > 1 ){
                        --mSelectedFallowStageNum;
                        if(mSelectedFallowStageNum == 1 && fallowInvestPrice > 1){
                            llVTicketPanel.setVisibility(View.VISIBLE);
                            mSelectedVTicketNum = 0;
                            tvVTicket.setText(String.format("%d/%d", mSelectedVTicketNum, mVTicketMaxUse));
                        }
                        tvStageInfo.setText(String.format("%d/%d", mSelectedFallowStageNum, mFallowStageLeft));
                        tvTotalPrice.setText(ToolMaster.convertRMBPriceString(mSelectedFallowStageNum * fallowInvestPrice  - mSelectedVTicketNum));

                    }
                }
            }
        });

        vStageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isLeadInvest() ){
                    if( mSelectedLeadStageNum < mLeadStageLeft ){
                        mSelectedLeadStageNum++;
                        tvStageInfo.setText(String.format("%d/%d", mSelectedLeadStageNum, mLeadStageLeft));
                        tvTotalPrice.setText(ToolMaster.convertRMBPriceString(mSelectedLeadStageNum * mLeadInvestPrice));
                    }
                }else {
                    if( mSelectedFallowStageNum < mFallowStageLeft ){
                        mSelectedFallowStageNum++;
                        tvStageInfo.setText(String.format("%d/%d", mSelectedFallowStageNum, mFallowStageLeft));
                        tvTotalPrice.setText(ToolMaster.convertRMBPriceString(mSelectedFallowStageNum * fallowInvestPrice));
                        llVTicketPanel.setVisibility(View.GONE);
                        mSelectedVTicketNum = 0;
                    }
                }
            }
        });

        if(flInvestType == InvestProjectProtocol.INVEST_TYPE_LEAD || flInvestStageNum > 1 || getFallowPriceNum() < 2 || mVTicketMaxUse== 0 ){
            llVTicketPanel.setVisibility(View.GONE);
        }else {
            mVTicketMaxUseTemp = mVTicketMaxUse;
            if(mVTicketMaxUseTemp > fallowInvestPrice - 1){
                mVTicketMaxUseTemp = fallowInvestPrice - 1;
            }

            if(mVTicketMaxUseTemp >= 1){
                mSelectedVTicketNum = 0;
                tvVTicket.setText(String.format("%d/%d", mSelectedVTicketNum, mVTicketMaxUseTemp));
            }else {
                mSelectedVTicketNum = 0;
                tvVTicket.setText(String.format("%d/%d", 0, 0));
                tvVTicket.setVisibility(View.GONE);
            }

            // v券选择按钮
            final View vAdd = dialog.findViewById(R.id.invest_confirm_iv_vTicketAdd);
            vAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectedVTicketNum < mVTicketMaxUseTemp){
                        ++mSelectedVTicketNum;
                        tvVTicket.setText(String.format("%d/%d", mSelectedVTicketNum, mVTicketMaxUseTemp));
                        tvTotalPrice.setText(ToolMaster.convertRMBPriceString(flInvestPriceNum - mSelectedVTicketNum));
                    }
                }
            });
            final View vSub = dialog.findViewById(R.id.invest_confirm_iv_vTicketSub);
            vSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectedVTicketNum > 0){
                        --mSelectedVTicketNum;
                        tvVTicket.setText(String.format("%d/%d", mSelectedVTicketNum, mVTicketMaxUseTemp));
                        tvTotalPrice.setText(ToolMaster.convertRMBPriceString(flInvestPriceNum - mSelectedVTicketNum));
                    }
                }
            });
        }

        // 当前版本隐藏
        llVTicketPanel.setVisibility(View.GONE);

        // 更新收益信息
        List<TextView> list;
        list = flInvestType == InvestProjectProtocol.INVEST_TYPE_LEAD ? getBrProportionTextView(flInvestStageNum)
                : getSrProportionTextView(getFallowPriceNum());
        final LinearLayout llProbabilityPanel = (LinearLayout)dialog.findViewById(R.id.invset_confirm_ll_earnProbability_root);
        for(TextView tv : list){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 0);
            llProbabilityPanel.addView(tv, layoutParams);
        }

        final View confirmButton = dialog.findViewById(R.id.invest_confirm_tv_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isLeadInvest() ){
                    flInvestStageNum = mSelectedLeadStageNum;
                    flInvestPriceNum = mLeadInvestPrice;
                }else {
                    flInvestStageNum = mSelectedFallowStageNum;
                    flInvestPriceNum = fallowInvestPrice;
                }

                onInvest(flInvestType, flInvestStageNum, flInvestPriceNum, 1, mSelectedVTicketNum);
                alertDialog.dismiss();
            }
        });
    }

    private boolean checkUserInput(){
        if( !isLeadInvest() && !isFallowInvest() ){
            UIHelper.toast(this, "请选择跟投或领投", Toast.LENGTH_LONG);
            return false;
        }

        if( isLeadInvest() ){
            if(mSelectedLeadStageNum < 1){
                UIHelper.toast(this, "请输入要领投的期数");
                return false;
            }
            return true;
        }else{
            if(mSelectedFallowStageNum < 1){
                UIHelper.toast(this, "请输入要跟投的期数");
                return false;
            }
            if( mETInvestPrice.getText().length() == 0 ){
                UIHelper.toast(this, "请输入要跟投的数量");
                return false;
            }
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
        return mLLLeadPanel.getVisibility() == View.VISIBLE;
    }

    private boolean isFallowInvest(){
        return mLLFallowPanel.getVisibility() == View.VISIBLE;
    }

    private void onInvest(int investType, final int investStageNum, final int investPriceNum, int messageType, int ticketUse){
        mProgressDialog.setMessage("正在提交, 请耐心等待");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        HttpParams params = new HttpParams();

        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser(this).getUserId());
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_ACTIVITY_STAGE_ID, mProjectDetailModel.getActivityStageId());
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_TYPE, investType);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_STAGE_NUM, investStageNum);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_PRICE_NUM, investPriceNum);
        params.put("MessageType", messageType);
        params.put("token", ((MyApplication)getApplication()).getToken(InvestProjectActivity.this));
        params.put("VirtualSecurities", ticketUse);

        HttpClient.atomicPost(this, InvestProjectProtocol.URL_INVEST_PROJECT, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                mProgressDialog.dismiss();
            }
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                mProgressDialog.dismiss();
                onInvestResult(s, investStageNum, investPriceNum, headers);
            }
        });
    }

    private void onInvestResult(String s, int investStageNum, int investPriceNum, Header[] headers){
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
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onInvest(flInvestType, flInvestStageNum, flInvestPriceNum, 2, mSelectedVTicketNum);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setMessage(String.format("当前期已售空，将从第%d期开始购买",
                                mProjectDetailModel.getTotalStage() - mLeadStageLeft + 1))
                        .create();
                alertDialog.show();


//                alertDialog = new AlertDialog.Builder(this)
//                        .setTitle("本期已满")
//                        .setMessage("本期已满，是否预购下一期?")
//                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                onInvest(flInvestType, flInvestStageNum, flInvestPriceNum, 2, mSelectedVTicketNum);
//                            }
//                        })
//                        .setNegativeButton("否", null)
//                        .create();
//                alertDialog.show();
                break;
            case -1:
                ((MyApplication)getApplication()).reLogin(this);
                break;
            case InvestProjectProtocol.INVEST_RESULT_TICKET_USE_ERROR:
                UIHelper.toast(this, "微券花费错误");
                break;
            case InvestProjectProtocol.INVEST_RESULT_FALLOW_PRICE_LEFT_NOT_ENOUGH:
                String priceLeft = "0";
                try{

                }catch (Exception e){
                    priceLeft = HttpClient.getValueFromHeader(headers, "values");
                }

                UIHelper.toast(this, String.format("本期众筹资金只剩余%s元，请购买小于该数额资金", priceLeft));
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
        final TextView confirm = (TextView)dialog.findViewById(R.id.rush_failure_tv_auto_close);

        String titleString = mProjectDetailModel.getName();
        titleString = String.format("您已入资 %s 项目", titleString);
        title.setText(titleString);

        pricePerStage.setText(ToolMaster.convertRMBPriceString(investPriceNum));
        stageNum.setText(String.valueOf(investStageNum) + "   期");
        totalPrice.setText(ToolMaster.convertRMBPriceString(investStageNum * investPriceNum));

        mSuccessDialog = new AlertDialog.Builder(this)
            .setView(dialog)
            .create();
        mSuccessDialog.show();
        mSuccessDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mSuccessDialog.dismiss();
                    finish();
                    return true;
                }
                return false;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccessDialog.dismiss();
                finish();
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
                    img, mProjectDetailModel.getTargetFund(), mProjectDetailModel.getCurrentFund(), true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
