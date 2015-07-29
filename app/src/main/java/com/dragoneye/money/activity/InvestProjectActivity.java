package com.dragoneye.money.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.money.DemoDataModel;
import com.dragoneye.money.R;
import com.dragoneye.money.application.AppInfoManager;
import com.dragoneye.money.application.MyApplication;
import com.dragoneye.money.config.ProjectStatusConfig;
import com.dragoneye.money.dao.InvestedProject;
import com.dragoneye.money.dao.InvestedProjectDao;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.Project;
import com.dragoneye.money.dao.ProjectDao;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.InvestProjectProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.user.CurrentUser;
import com.dragoneye.money.view.DotViewPager;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class InvestProjectActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID";

    private DotViewPager mDotViewPager;
    private ArrayList<String> mImageUrl;
    ArrayList<View> viewContainer = new ArrayList<>();
    private TextView mTextViewConfirm;
    private Project mProject;
    private InvestedProject mInvestedProject;
    //private TextView mTextViewInvestPrice;
    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;
    private LinearLayout mLinearLayoutResultRoot;
    private ArrayList<Integer> mPriceList = new ArrayList<>();
    private DemoDataModel.ModeProjectGroup modeProjectGroup;
    DemoDataModel.ModeProject mModeProject;

    private View mIVLeadAdd;
    private View mIVLeadSubtract;
    private View mIVFallowAdd;
    private View mIVFallowSubtract;
    private TextView mTVLeadStage;
    private TextView mTVFallowStage;
    private EditText mETInvestPrice;
    private int mProjectStageMaxNum = 20;
    private int mSelectedLeadStageNum;
    private int mSelectedFallowStageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_investment_listview_detail);
        initView();
        initData();
        initViewPagerImages();
        initSpinnerItems();
    }

    @Override
    protected void onResume(){
        super.onResume();

        ProjectDao projectDao = MyDaoMaster.getDaoSession().getProjectDao();
        mProject = projectDao.load(mProject.getId());
        if( mProject == null ){
            finish();
            return;
        }
        updateUIContent();
    }

    private void initView(){
        mDotViewPager = (DotViewPager)findViewById(R.id.investment_project_detail_dot_viewpager);

        mTextViewConfirm = (TextView)findViewById(R.id.invest_project_tv_confirm);
        mTextViewConfirm.setOnClickListener(this);

//        mTextViewInvestPrice = (TextView)findViewById(R.id.invest_project_tv_invest_price);

        mProgressBar = (ProgressBar)findViewById(R.id.invest_project_progressbar);
        mTextViewProjectProgress = (TextView)findViewById(R.id.invest_project_tv_project_progress);

        mLinearLayoutResultRoot = (LinearLayout)findViewById(R.id.investment_ll_result_root);

        mIVLeadAdd = findViewById(R.id.invest_project_iv_leadAdd);
        mIVLeadAdd.setOnClickListener(this);
        mIVLeadSubtract = findViewById(R.id.invest_project_iv_leadSubstract);
        mIVLeadSubtract.setOnClickListener(this);
        mIVFallowAdd = findViewById(R.id.invest_project_iv_fallowAdd);
        mIVFallowAdd.setOnClickListener(this);
        mIVFallowSubtract = findViewById(R.id.invest_project_iv_fallowSubtract);
        mIVFallowSubtract.setOnClickListener(this);

        mTVLeadStage = (TextView)findViewById(R.id.textView25);
        mTVFallowStage = (TextView)findViewById(R.id.textView21);

        mETInvestPrice = (EditText)findViewById(R.id.invest_project_et_price_num);
    }

    private void initData(){
        modeProjectGroup = ((MyApplication)getApplication()).demoDataModel.modeProjectGroup;
        mImageUrl = new ArrayList<>();

        Intent intent = getIntent();
        long projectId = intent.getLongExtra(EXTRA_PROJECT_ID, 1);

        ProjectDao projectDao = MyDaoMaster.getDaoSession().getProjectDao();
        mProject = projectDao.load(projectId);
        if( mProject == null ){
            finish();
            return;
        }

        ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();
        QueryBuilder queryBuilder = projectImageDao.queryBuilder();
        queryBuilder.where(ProjectImageDao.Properties.ProjectId.eq(projectId));
        List<ProjectImage> projectImages = queryBuilder.build().list();

        for(ProjectImage projectImage : projectImages){
            mImageUrl.add(projectImage.getImageUrl());
        }

        InvestedProjectDao investedProjectDao = MyDaoMaster.getDaoSession().getInvestedProjectDao();
        queryBuilder = investedProjectDao.queryBuilder();
        queryBuilder.where(InvestedProjectDao.Properties.ProjectId.eq(mProject.getId()));
        mInvestedProject = (InvestedProject)queryBuilder.unique();

        resetLead();
        resetFallow();
        setLeadStageNum(0);
        setFallowStageNum(0);
    }

    private void updateUIContent(){
        updateInvestedPrice();
        if( mProject.getStatus() == ProjectStatusConfig.PROJECT_SUCCESS ){
            mProgressBar.setProgress(100);
            mTextViewProjectProgress.setText( String.format(getString(R.string.invest_project_project_progress), 100));
        }else {
            mProgressBar.setProgress(10);
            mTextViewProjectProgress.setText(String.format(getString(R.string.invest_project_project_progress), 10));
        }
    }

    private void updateInvestedPrice(){
        if( mInvestedProject == null ){
//            mTextViewInvestPrice.setText(R.string.invest_project_no_invested_price);
        }else {
            String string = String.format(getString(R.string.invest_project_invested_price), mInvestedProject.getPrice());
//            mTextViewInvestPrice.setText(string);
        }
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
        }
    }

    private void onLeadAdd(){
        resetFallow();
        if( mSelectedLeadStageNum < mProjectStageMaxNum ){
            mSelectedLeadStageNum++;
            setLeadStageNum(mSelectedLeadStageNum);
        }
    }

    private void onLeadSubtract(){
        resetFallow();
        if( mSelectedLeadStageNum > 0 ){
            mSelectedLeadStageNum--;
            setLeadStageNum(mSelectedLeadStageNum);
        }
    }

    private void onFallowAdd(){
        resetLead();
        if( mSelectedFallowStageNum < mProjectStageMaxNum ){
            mSelectedFallowStageNum++;
            setFallowStageNum(mSelectedFallowStageNum);
        }
    }

    private void onFallowSubtract(){
        resetLead();
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
    }

    private void resetFallow(){
        if( mSelectedFallowStageNum != 0 ){
            setFallowStageNum(0);
        }
        mSelectedFallowStageNum = 0;
    }

    private void setLeadStageNum(int num){
        mTVLeadStage.setText(String.format("%d/%d", num, mProjectStageMaxNum));
    }

    private void setFallowStageNum(int num){
        mTVFallowStage.setText(String.format("%d/%d", num, mProjectStageMaxNum));
    }



    private void initViewPagerImages(){
        for(String url : mImageUrl){
            ImageView imageView = new ImageView(this);
            try{
                imageView.setImageBitmap( MediaStore.Images.Media.getBitmap(getContentResolver(),
                        Uri.parse(url) ) );
                viewContainer.add(imageView);
            }catch (IOException e){

            }
        }
        mDotViewPager.setAdapter(new ImageViewPagerAdapter());
    }

    private void initSpinnerItems(){

        for( int price : modeProjectGroup.sTicketsPrices){
            mPriceList.add(price);
        }

        for(DemoDataModel.ModeProject modeProject : modeProjectGroup.projects){
            if( modeProject.id == mProject.getId() ){
                mModeProject = modeProject;
                break;
            }
        }

        for( Integer price : mModeProject.bTicketsList ){
            mPriceList.add(price);
        }

        ArrayList<String> spinnerItemStrings = new ArrayList<>();
        for( int price : mPriceList){
            spinnerItemStrings.add(String.valueOf(price) + getString(R.string.monetary_unit_rmb));
        }
    }

    private void updateProbability(int price){
        mLinearLayoutResultRoot.removeAllViews();

        float factor = price / 2.0f;
        Object[] key_arr = modeProjectGroup.bonusProbabilities.keySet().toArray();
        Arrays.sort(key_arr);
        for( Object key : key_arr ){
            TextView textView = new TextView(this);
            mLinearLayoutResultRoot.addView(textView);

            int bonus = (Integer)key;
            float probability = modeProjectGroup.bonusProbabilities.get(key) * factor * 100;
            String str = String.format("%%%f\t\t     ￥%d", probability, bonus);
            textView.setText(str);
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
                    + 2000000 + "，共计￥" + 2000000 * mSelectedLeadStageNum + "。";
            investType = InvestProjectProtocol.INVEST_TYPE_LEAD;
            investStageNum = mSelectedLeadStageNum;
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
                        onInvest(flInvestType, flInvestStageNum, flInvestPriceNum);
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
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_ACTIVITY_STAGE_ID, "");
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_TYPE, investType);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_STAGE_NUM, investStageNum);
        params.put(InvestProjectProtocol.INVEST_PROJECT_PARAM_INVEST_PRICE_NUM, investPriceNum);

        HttpClient.post(InvestProjectProtocol.URL_INVEST_PROJECT, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                UIHelper.toast(InvestProjectActivity.this, "网络异常");
            }

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
            case InvestProjectProtocol.INVSET_RESULT_FAILED:
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

    private class ImageViewPagerAdapter extends PagerAdapter{


        //viewpager中的组件数量
        @Override
        public int getCount() {
            return viewContainer.size();
        }
        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewContainer.get(position));
        }
        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(viewContainer.get(position));
            viewContainer.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InvestProjectActivity.this, ImageExplorerActivity.class);
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (String url : mImageUrl) {
                        uris.add(Uri.parse(url));
                    }
                    intent.putExtra(ImageExplorerActivity.EXTRA_URI_ARRAY, uris);
                    intent.putExtra(ImageExplorerActivity.EXTR_INDEX_TO_SHOW, position);
                    startActivity(intent);
                }
            });
            return viewContainer.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
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
            intent.putExtra(EXTRA_PROJECT_ID, mProject.getId());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
