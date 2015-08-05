package com.dragoneye.money.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.DotViewPagerActivity;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.GetProjectListProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ProjectDetailActivity extends DotViewPagerActivity implements View.OnClickListener{
    public static final String EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID";


    private String mProjectId;
    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;

    private TextView mTVMarketAnalyze;
    private TextView mTVProfitMode;
    private TextView mTVTeamIntroduction;
    private TextView mTVProjectIntroduction;
    private TextView mTVProjectSummary;
    private TextView mTVAddress;

    private String mMarketAnalyze;
    private String mProfitMode;
    private String mTeamIntroduction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_investment_detail);
        initView();
        initData();
        updateUIContent();
    }

    @Override
    protected void initViewPager(){
        mDotViewPager = (DotViewPager)findViewById(R.id.project_detail_dot_viewpager);
    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();

        Intent intent = getIntent();
        mProjectId = intent.getStringExtra(EXTRA_PROJECT_ID);

        ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();
        QueryBuilder queryBuilder = projectImageDao.queryBuilder();
        queryBuilder.where(ProjectImageDao.Properties.ProjectId.eq(1));
        List<ProjectImage> projectImages = queryBuilder.build().list();

        for(ProjectImage projectImage : projectImages){
            mImageUrl.add(projectImage.getImageUrl());
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
    }

    private void initData(){
        onUpdateProjectDetail();
    }

    private void onUpdateProjectDetail(){
        HttpParams params = new HttpParams();
        params.put("activityId", mProjectId);

        HttpClient.atomicPost(this, GetProjectListProtocol.URL_GET_PROJECT_INFO, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                String result = HttpClient.getValueFromHeader(headers, GetProjectListProtocol.GET_PROJECT_INFO_RESULT_KEY);
                if (result == null || s == null) {
                    UIHelper.toast(ProjectDetailActivity.this, "服务器异常");
                    return;
                }
                onUpdateProjectDetailResult(result, s);
            }
        });
    }

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
