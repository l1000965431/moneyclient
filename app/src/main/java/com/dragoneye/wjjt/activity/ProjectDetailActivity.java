package com.dragoneye.wjjt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.DotViewPagerActivity;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.view.DotViewPager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProjectDetailActivity extends DotViewPagerActivity implements View.OnClickListener{

    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;

    private TextView mTVMarketAnalyze;
    private TextView mTVProfitMode;
    private TextView mTVTeamIntroduction;
    private TextView mTVProjectIntroduction;
    private TextView mTVProjectSummary;
    private TextView mTVAddress;
    private TextView mTVCategory;
    private TextView mTVCreateDate;



    MenuItem mProgressMenu;

    Handler handler = new Handler();

    public static void CallProjectDetailActivity(Context context, String activityId, String name ,ArrayList<String> imageUrl, int targetFund, int currentFund){
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("activityId", activityId);
        intent.putExtra("name", name);
        intent.putStringArrayListExtra("imageUrl", imageUrl);
        intent.putExtra("targetFund", targetFund);
        intent.putExtra("currentFund", currentFund);
        context.startActivity(intent);
    }

    public static void CallProjectDetailActivityFullInfo(Context context, String name, ArrayList<String> imageUrl, int targetFund,
                                                         int currentFund, String marketAnalyze, String profitMode, String teamIntroduction,
                                                         String summary, String address, String activityIntroduction, Date createDate,
                                                         String category){
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("name", name);
        intent.putStringArrayListExtra("imageUrl", imageUrl);
        intent.putExtra("targetFund", targetFund);
        intent.putExtra("currentFund", currentFund);
        intent.putExtra("isFullInfo", true);
        intent.putExtra("marketAnalyze", marketAnalyze);
        intent.putExtra("profitMode", profitMode);
        intent.putExtra("teamIntroduction", teamIntroduction);
        intent.putExtra("summary", summary);
        intent.putExtra("address", address);
        intent.putExtra("activityIntroduction", activityIntroduction);
        intent.putExtra("category", category);
        intent.putExtra("createDate", createDate.getTime());
        context.startActivity(intent);
    }

    private String mMarketAnalyze;
    private String mProfitMode;
    private String mTeamIntroduction;
    private String mActivityId;
    private ArrayList<String> pmImageUrl;
    private int mTargetFund;
    private int mCurrentFund;
    private Date mCreateDate;
    private String mCategory;

    private boolean isFullInfo;
    private String mName;
    private String mSummary;
    private String mAddress;
    private String mActivityIntroduction;

    private void readIntent(){
        Intent intent = getIntent();
        pmImageUrl = intent.getStringArrayListExtra("imageUrl");
        mTargetFund = intent.getIntExtra("targetFund", 0);
        mCurrentFund = intent.getIntExtra("currentFund", 0);
        isFullInfo = intent.getBooleanExtra("isFullInfo", false);
        mName = intent.getStringExtra("name");
        if( isFullInfo ){
            mMarketAnalyze = intent.getStringExtra("marketAnalyze");
            mProfitMode = intent.getStringExtra("profitMode");
            mTeamIntroduction = intent.getStringExtra("teamIntroduction");
            mSummary = intent.getStringExtra("summary");
            mAddress = intent.getStringExtra("address");
            mActivityIntroduction = intent.getStringExtra("activityIntroduction");
            mCreateDate = new Date(intent.getLongExtra("createDate", 0));
            mCategory = intent.getStringExtra("category");
        }else {
            mActivityId = intent.getStringExtra("activityId");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readIntent();
        setIsNeedLoadingFeature(true);
        setContentView(R.layout.home_investment_detail);
        initView();
        initData();
        updateUIContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_progress_action_bar, menu);
        mProgressMenu = menu.findItem(R.id.refresh_loading);
        return true;
    }

    public void setLoadingState(boolean refreshing) {
//        if (mProgressMenu != null) {
//            if (refreshing) {
//                mProgressMenu
//                        .setActionView(R.layout.actionbar_indeterminate_progress);
//                mProgressMenu.setVisible(true);
//            } else {
//                mProgressMenu.setVisible(false);
//                mProgressMenu.setActionView(null);
//            }
//        }
        if(refreshing){
            setStartLoading();
        }else {
            finishLoading(true);
        }
    }

    @Override
    protected void initViewPager(){
        mDotViewPager = (DotViewPager)findViewById(R.id.project_detail_dot_viewpager);
    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();

        if( pmImageUrl != null && pmImageUrl.size() > 0 ){
            mImageUrl = pmImageUrl;
        }else {
            mImageUrl = new ArrayList<>();
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.icon_albums).toString());
        }
    }

    private void initView(){

        mProgressBar = (ProgressBar)findViewById(R.id.project_detail_progressbar);
        mTextViewProjectProgress = (TextView)findViewById(R.id.project_detail_tv_project_progress);

        mTVMarketAnalyze = (TextView)findViewById(R.id.project_detail_tv_marketAnalyze);
        mTVMarketAnalyze.setOnClickListener(this);
        mTVProfitMode = (TextView)findViewById(R.id.project_detail_tv_profitMode);
        mTVProfitMode.setOnClickListener(this);
        mTVTeamIntroduction = (TextView)findViewById(R.id.project_detail_tv_teamIntroduction);
        mTVTeamIntroduction.setOnClickListener(this);

        mTVProjectSummary = (TextView)findViewById(R.id.project_detail_tv_summary);
        mTVProjectIntroduction = (TextView)findViewById(R.id.project_detail_tv_projectIntroduction);
        mTVAddress = (TextView)findViewById(R.id.project_detail_tv_address);
        mTVCategory = (TextView)findViewById(R.id.project_detail_tv_category);
        mTVCreateDate = (TextView)findViewById(R.id.project_detail_tv_createDate);

        int percent = (int)((float)mCurrentFund / mTargetFund * 100 + 0.5f);
        mProgressBar.setProgress(percent);
        mTextViewProjectProgress.setText(String.format("筹款进度: %d%%", percent));

        String title = mName;
        if( title.length() > 10 ){
            title = title.substring(0, 10) + "...";
        }
        setTitle(title);
    }

    private void initData(){
        if( isFullInfo ){
            mTVAddress.setText(mAddress);
            mTVProjectSummary.setText(mSummary);
            mTVProjectIntroduction.setText(mActivityIntroduction);
            mTVCategory.setText(mCategory);
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                mTVCreateDate.setText(sdf.format(mCreateDate));
            }catch (Exception e){

            }
        }
        else {
            handler.post(onUpdateProjectDetail_r);
        }
    }

    @Override
    protected void onRetryLoading(){
        handler.post(onUpdateProjectDetail_r);
    }

    Runnable onUpdateProjectDetail_r = new Runnable() {
        @Override
        public void run() {
            setStartLoading();
            HttpParams params = new HttpParams();
            params.put("activityId", mActivityId);

            HttpClient.atomicPost(ProjectDetailActivity.this, GetProjectListProtocol.URL_GET_PROJECT_INFO, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                    finishLoading(false);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    finishLoading(true);
                    String result = HttpClient.getValueFromHeader(headers, GetProjectListProtocol.GET_PROJECT_INFO_RESULT_KEY);
                    if (result == null || s == null) {
                        UIHelper.toast(ProjectDetailActivity.this, "服务器繁忙");
                        return;
                    }
                    onUpdateProjectDetailResult(result, s);
                }
            });
        }
    };

    private void onUpdateProjectDetailResult(String result, String response){
        switch (result){
            case GetProjectListProtocol.GET_PROJECT_INFO_SUCCESS:
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    mMarketAnalyze = jsonObject.getString("marketAnalysis");
                    mProfitMode = jsonObject.getString("profitMode");
                    mTeamIntroduction = jsonObject.getString("teamIntroduction");

                    mTVProjectSummary.setText(jsonObject.getString("summary"));
                    mTVAddress.setText(jsonObject.getString("address"));
                    mTVProjectIntroduction.setText(jsonObject.getString("projectIntroduction"));
                    mTVCategory.setText(jsonObject.getString("category"));
                    try{
                        Date date = new Date(jsonObject.getLong("createDate"));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        mTVCreateDate.setText(sdf.format(date));
                    }catch (Exception e){

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            case GetProjectListProtocol.GET_PROJECT_INFO_NO_PROJECT:
                UIHelper.toast(this, "项目已关闭");
                break;
        }
    }

    private void updateUIContent(){
//        if( mProject.getStatus() == ProjectStatusConfig.PROJECT_SUCCESS ){
//            mProgressBar.setProgress(100);
//            mTextViewProjectProgress.setText( String.format(getString(R.string.invest_project_project_progress), 100));
//        }else {
//            mProgressBar.setProgress(10);
//            mTextViewProjectProgress.setText(String.format(getString(R.string.invest_project_project_progress), 10));
//        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.project_detail_tv_marketAnalyze:
            case R.id.project_detail_tv_profitMode:
            case R.id.project_detail_tv_teamIntroduction:
                onTopButton(v.getId());
                break;
        }
    }

    private void onTopButton(int id){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        switch (id){
            case R.id.project_detail_tv_marketAnalyze:
                alertDialog.setMessage(mMarketAnalyze);
                break;
            case R.id.project_detail_tv_profitMode:
                alertDialog.setMessage(mProfitMode);
                break;
            case R.id.project_detail_tv_teamIntroduction:
                alertDialog.setMessage(mTeamIntroduction);
                break;
        }

        alertDialog.show();
    }
}
