package com.dragoneye.money.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.dragoneye.money.model.ProjectDetailModel;
import com.dragoneye.money.protocol.GetProjectListProtocol;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ProjectDetailActivity extends DotViewPagerActivity implements View.OnClickListener{
    private ProjectDetailModel mProjectDetailModel;

    private ProgressBar mProgressBar;
    private TextView mTextViewProjectProgress;

    private TextView mTVMarketAnalyze;
    private TextView mTVProfitMode;
    private TextView mTVTeamIntroduction;
    private TextView mTVProjectIntroduction;
    private TextView mTVProjectSummary;
    private TextView mTVAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mProjectDetailModel = (ProjectDetailModel)intent.getSerializableExtra(InvestProjectActivity.EXTRA_PROJECT_MODEL);
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

        ArrayList<String> imageUrl = ToolMaster.gsonInstance().fromJson(mProjectDetailModel.getImageUrl(),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if( imageUrl != null && imageUrl.size() > 0 ){
            mImageUrl = imageUrl;
        }else {
            mImageUrl = new ArrayList<>();
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString());
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString());
            mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString());
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
        params.put("activityId", mProjectDetailModel.getActivityId());

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
                    mProjectDetailModel.setMarketAnalysis(jsonObject.getString("marketAnalysis"));
                    mProjectDetailModel.setProfitMode(jsonObject.getString("profitMode"));
                    mProjectDetailModel.setTeamIntroduce(jsonObject.getString("teamIntroduction"));
                    mProjectDetailModel.setSummary(jsonObject.getString("summary"));

                    mTVProjectSummary.setText(mProjectDetailModel.getSummary());
                    mTVAddress.setText(mProjectDetailModel.getAddress());
                    mTVProjectIntroduction.setText(mProjectDetailModel.getActivityIntroduce());
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
                alertDialog.setMessage(mProjectDetailModel.getMarketAnalysis());
                break;
            case R.id.project_detail_tv_profitMode:
                alertDialog.setMessage(mProjectDetailModel.getProfitMode());
                break;
            case R.id.project_detail_tv_teamIntroduction:
                alertDialog.setMessage(mProjectDetailModel.getTeamIntroduce());
                break;
        }

        alertDialog.show();
    }
}
