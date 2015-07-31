package com.dragoneye.money.activity;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.GetProjectListProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener{
    public static final String EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID";


    private String mProjectId;
    private DotViewPager mDotViewPager;
    private ArrayList<String> mImageUrl;
    ArrayList<View> viewContainer = new ArrayList<>();
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
        initViewPagerImages();
        mDotViewPager.setAdapter(new ImageViewPagerAdapter());
        updateUIContent();
    }

    private void initView(){
        mDotViewPager = (DotViewPager)findViewById(R.id.project_detail_dot_viewpager);

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

        onUpdateProjectDetail();
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

    private void onUpdateProjectDetail(){
        HttpParams params = new HttpParams();
        params.put("activityId", mProjectId);

        HttpClient.post(GetProjectListProtocol.URL_GET_PROJECT_INFO, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                UIHelper.toast(ProjectDetailActivity.this, "网络异常");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                String result = HttpClient.getValueFromHeader(headers, GetProjectListProtocol.GET_PROJECT_INFO_RESULT_KEY);
                if( result == null || s == null ){
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

    private class ImageViewPagerAdapter extends PagerAdapter {

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
                    Intent intent = new Intent(ProjectDetailActivity.this, ImageExplorerActivity.class);
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (String url : mImageUrl) {
                        uris.add(Uri.parse(url));
                    }
                    intent.putExtra(ImageExplorerActivity.EXTRA_URI_ARRAY, uris);
                    intent.putExtra(ImageExplorerActivity.EXTRA_INDEX_TO_SHOW, position);
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
        getMenuInflater().inflate(R.menu.menu_project_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_project_detail_cheat) {
            onCheat();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onCheat(){
        updateUIContent();
    }
}
