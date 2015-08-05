package com.dragoneye.money.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.DotViewPagerActivity;
import com.dragoneye.money.application.AppInfoManager;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.model.ProjectDetailModel;
import com.dragoneye.money.model.SrEarningModel;
import com.dragoneye.money.protocol.InvestProjectProtocol;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.user.CurrentUser;
import com.dragoneye.money.view.DotViewPager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvestProjectActivity extends DotViewPagerActivity implements View.OnClickListener {

    public static final String EXTRA_PROJECT_MODEL = "EXTRA_PROJECT_MODEL";

    private TextView mTextViewConfirm;
    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;
    private LinearLayout mLinearLayoutResultRoot;
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
    private EditText mETInvestPrice;
    private int mProjectStageMaxNum;
    private int mSelectedLeadStageNum;
    private int mSelectedFallowStageNum;
    private int mLeadInvestPrice;

    private View mLLLeadButton;
    private View mLLLeadPanel;
    private TextView mTVLeadInvestInfo;
    private ImageView mIVLeadArrow;
    private View mLLFallowButton;
    private View mLLFallowPanel;
    private ImageView mIVFallowArrow;

    private ArrayList<SrEarningModel> mSrEarningModels = new ArrayList<>();

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_investment_listview_detail);
        initView();
        initData();
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void initViewPager(){
        // 图片浏览控件
        mDotViewPager = (DotViewPager)findViewById(R.id.investment_project_detail_dot_viewpager);
    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();

        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString());

    }

    private void initView(){


        mTVStageInfo = (TextView)findViewById(R.id.textView2);
        mTVTargetFund = (TextView)findViewById(R.id.textView3);
        mTVTargetFundNum = (TextView)findViewById(R.id.textView5);

        // 确认投资按钮
        mTextViewConfirm = (TextView)findViewById(R.id.invest_project_tv_confirm);
        mTextViewConfirm.setOnClickListener(this);

        // 投资进度条和标签
        mProgressBar = (ProgressBar)findViewById(R.id.invest_project_progressbar);
        mTextViewProjectProgress = (TextView)findViewById(R.id.invest_project_tv_project_progress);

        // 投资收益几率显示
        mLinearLayoutResultRoot = (LinearLayout)findViewById(R.id.investment_ll_result_root);

        // 投资期数显示标签
        mTVLeadStage = (TextView)findViewById(R.id.invest_project_tv_leadStage);
        mTVFallowStage = (TextView)findViewById(R.id.invest_project_tv_fallowStage);

        // 跟投数量输入控件
        mETInvestPrice = (EditText)findViewById(R.id.invest_project_et_price_num);

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
    }

    private void initData(){
        Intent intent = getIntent();
        mProjectDetailModel = (ProjectDetailModel)intent.getSerializableExtra(EXTRA_PROJECT_MODEL);

        mProjectStageMaxNum = mProjectDetailModel.getTotalStage() - mProjectDetailModel.getCurrentStage() + 1;


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

        handler.post(getInvestInfo_r);

        int progress = (int)(mProjectDetailModel.getCurrentFund() / (float)mProjectDetailModel.getTargetFund());
        String strProgress = getString(R.string.project_list_item_progress) + "%" + progress;
        mTextViewProjectProgress.setText(strProgress);
        mProgressBar.setProgress( progress );
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
        if( mLLLeadPanel.getVisibility() == View.VISIBLE ){
            mLLLeadPanel.setVisibility(View.GONE);
            mIVLeadArrow.setRotation(-90.0f);
        }else {
            mLLLeadPanel.setVisibility(View.VISIBLE);
            mIVLeadArrow.setRotation(0);
            mSelectedLeadStageNum = 1;
            setLeadStageNum(mSelectedLeadStageNum);
        }
        resetFallow();
    }

    private void onFallow(){
        if( mLLFallowPanel.getVisibility() == View.VISIBLE ){
            mLLFallowPanel.setVisibility(View.GONE);
            mIVFallowArrow.setRotation(-90.0f);
        }else {
            mLLFallowPanel.setVisibility(View.VISIBLE);
            mIVFallowArrow.setRotation(0);
            mSelectedFallowStageNum = 1;
            setFallowStageNum(mSelectedFallowStageNum);
        }
        resetLead();
    }

    private void onLeadAdd(){
        if( mSelectedLeadStageNum < mProjectStageMaxNum ){
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
        if( mSelectedFallowStageNum < mProjectStageMaxNum ){
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
    }

    private void resetFallow(){
        if( mSelectedFallowStageNum != 0 ){
            setFallowStageNum(0);
        }
        mSelectedFallowStageNum = 0;
        mLLFallowPanel.setVisibility(View.GONE);
        mIVFallowArrow.setRotation(-90.0f);
    }

    private void setLeadStageNum(int num){
        mTVLeadStage.setText(String.format("%d/%d", num, mProjectStageMaxNum));
    }

    private void setFallowStageNum(int num){
        mTVFallowStage.setText(String.format("%d/%d", num, mProjectStageMaxNum));
    }

    Runnable getInvestInfo_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();
            httpParams.put(InvestProjectProtocol.GET_INVEST_INFO_PARAM_ACTIVITY_STAGE_ID, mProjectDetailModel.getActivityStageId());

            HttpClient.atomicPost(InvestProjectActivity.this, InvestProjectProtocol.URL_GET_INVEST_INFO,
                    httpParams, new HttpClient.MyHttpHandler() {
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
            UIHelper.toast(this, "服务器繁忙，请稍后再试");
            finish();
            return;
        }

        switch (response){
            case InvestProjectProtocol.GET_INVEST_INFO_SUCCESS:
                try{
                    JSONObject object = new JSONObject(result);
                    mLeadInvestPrice = object.getInt("brInvestPrice");
                    JSONArray srEarnings = object.getJSONArray("srEarningInfo");
                    for(int i = 0; i < srEarnings.length(); i++){
                        SrEarningModel srEarningModel = new SrEarningModel();
                        srEarningModel.setNum( srEarnings.getJSONObject(i).getInt("srEarningNum"));
                        srEarningModel.setPrice(srEarnings.getJSONObject(i).getInt("srEarningPrice"));
                        mSrEarningModels.add(srEarningModel);
                    }
                    updateLeadInvestText();
                    updateEarningProportion();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case InvestProjectProtocol.GET_INVEST_INFO_FAILED:
                UIHelper.toast(this, "此项目已过期");
                finish();
                break;
        }
    }

    private void updateLeadInvestText(){
        String leadInvestInfo = String.format(getString(R.string.invest_project_lead_invest_info),
                ToolMaster.convertToPriceString(mLeadInvestPrice));
        mTVLeadInvestInfo.setText(leadInvestInfo);
    }

    private void updateEarningProportion(){
        for(SrEarningModel model : mSrEarningModels){
            TextView textView = new TextView(this);
            mLinearLayoutResultRoot.addView(textView);

            String text = String.format("%f几率获得%s", 0.0f, model.getPrice());
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

        final int flInvestType = investType;
        final int flInvestStageNum = investStageNum;
        final int flInvestPriceNum = investPriceNum;

        new AlertDialog.Builder(this).setTitle(AppInfoManager.getApplicationName(this))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(InvestProjectActivity.this, PaymentActivity.class);
                        intent.putExtra(EXTRA_PROJECT_MODEL, mProjectDetailModel);
                        intent.putExtra(PaymentActivity.EXTRA_INVEST_TYPE, flInvestType);
                        intent.putExtra(PaymentActivity.EXTRA_INVEST_STAGE_NUM, flInvestStageNum);
                        intent.putExtra(PaymentActivity.EXTRA_INVEST_PRICE_NUM, flInvestPriceNum);
                        startActivity(intent);
//                        onInvest(flInvestType, flInvestStageNum, flInvestPriceNum);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMessage(tips).show();

    }

    private void onInvest(int investType, int investStageNum, int investPriceNum){
        HttpParams params = new HttpParams();

        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_USER_ID, CurrentUser.getCurrentUser().getUserId());
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
            Intent intent = new Intent(this, ProjectDetailActivity.class);
            intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, mProjectDetailModel.getActivityId());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
