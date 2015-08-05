package com.dragoneye.money.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.BaseActivity;
import com.dragoneye.money.activity.base.ImageMulSelectedActivity;
import com.dragoneye.money.activity.base.ImageSelectedActivity;
import com.dragoneye.money.config.HttpUrlConfig;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.model.ProjectDetailModel;
import com.dragoneye.money.protocol.UploadProjectProtocol;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.user.CurrentUser;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class ProjectEditActivity extends ImageSelectedActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final long MAX_IMAGE_SIZE = 500 * 1024;
    private static final int MAX_IMAGE_NUM = 8;

    private EditText mETProjectName;
    private EditText mETProjectTargetPrice;
    private EditText mETRaiseDay;
    private EditText mETTeamSize;
    private EditText mETAddress;
    private EditText mETVideoUrl;
    private EditText mETProjectSummary;
    private EditText mETProjectIntroduce;
    private EditText mETMarketAnalysis;
    private EditText mETTeamIntroduce;
    private EditText mETTags;
    private EditText mETProfitModel;

    private TextView mTVSaveProject;
    private TextView mTVSelectedImage;

    private ArrayList<File> mImageFile = new ArrayList<>();
    private String mUploadToken;

    private ArrayList<RadioButton> mRBProjectTypeList = new ArrayList<>();
    private CompoundButton mRBSelectedProjectType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_project_launched);
        initView();
        initData();
        test();
    }

    private void initView(){
        mETProjectName = (EditText)findViewById(R.id.project_launched_et_project_name);
        mETProjectTargetPrice = (EditText)findViewById(R.id.project_launched_et_target_price);
        mETRaiseDay = (EditText)findViewById(R.id.project_launched_et_raise_day);
        mETTeamSize = (EditText)findViewById(R.id.project_launched_et_crew_number);
        mETAddress = (EditText)findViewById(R.id.project_launched_et_address);
        mETVideoUrl = (EditText)findViewById(R.id.project_launched_et_video_url);
        mETProjectSummary = (EditText)findViewById(R.id.project_launched_et_summary);
        mETProjectIntroduce = (EditText)findViewById(R.id.project_launched_et_introduce);
        mETMarketAnalysis = (EditText)findViewById(R.id.project_launched_et_market_analysis);
        mETProfitModel = (EditText)findViewById(R.id.project_launched_et_profit_model);
        mETTeamIntroduce = (EditText)findViewById(R.id.project_launched_et_team_introduce);
        mETTags = (EditText)findViewById(R.id.project_launched_et_tags);

        mTVSaveProject = (TextView)findViewById(R.id.fragment_register_buttonlogin);
        mTVSaveProject.setOnClickListener(this);
        mTVSelectedImage = (TextView)findViewById(R.id.project_launched_tv_selected_image);
        mTVSelectedImage.setOnClickListener(this);

        // 创建项目类别radio按钮
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_1));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_2));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_3));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_4));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_5));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_6));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_7));
        mRBProjectTypeList.add((RadioButton)findViewById(R.id.project_launched_rb_project_type_8));
        for(RadioButton radioButton : mRBProjectTypeList){
            radioButton.setOnCheckedChangeListener(this);
        }
    }

    private void initData(){
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        if(mRBSelectedProjectType != null){
            mRBSelectedProjectType.setChecked(false);
        }
        mRBSelectedProjectType = buttonView;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_register_buttonlogin:
                onSave();
                break;
            case R.id.project_launched_tv_selected_image:
                onSelectedImage();
                break;
        }
    }

    private void onSelectedImage(){
        goToPortraitSelect();
//        Intent intent = new Intent(this, ImageMulSelectedActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onSelectedFromGalleryFinish(Uri uri){
        String filePath = ToolMaster.getRealPathFromURI(this, uri);
        if( filePath == null ){
            UIHelper.toast(this, "错误的图片路径");
            return;
        }

        File file = new File(filePath);
        if(file.length() > MAX_IMAGE_SIZE){
            UIHelper.toast(this, "图片太大!");
            return;
        }
        mImageFile.add(file);

        UIHelper.toast(this, "选择成功");
    }

    private void onSave(){
//        ProjectDetailModel projectDetail = new ProjectDetailModel();
//        if( !getProjectInput(projectDetail) ){
//            return;
//        }

//        startUploadProject(projectDetail);
        onUploadTest();
    }

    private void test(){
        HttpClient.atomicPost(this, HttpUrlConfig.URL_ROOT + "ImageUploadController/getUploadToken",
                null, new HttpClient.MyHttpHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        UIHelper.toast(ProjectEditActivity.this, s);
                        mUploadToken = s;
                    }
                });
    }

    private void onUploadTest(){
        UploadManager uploadManager = new UploadManager();

        File file = mImageFile.get(0);

        uploadManager.put(file, "moneyImageUploadTest", mUploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                UIHelper.toast(ProjectEditActivity.this, "上传成功");
                Log.i("upload", responseInfo.toString());
            }
        }, null);
    }

    private void startUploadProject(ProjectDetailModel projectDetail){
        HttpParams httpParams = new HttpParams();
        httpParams.putGsonData(projectDetail);

        HttpClient.get(UploadProjectProtocol.URL_SUBMIT_PROJECT, httpParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d("SubmitProject Failure", s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d("SubmitProject Success", s);
                UIHelper.toast(ProjectEditActivity.this, "上传成功");
            }
        });
    }

    private boolean getProjectInput(ProjectDetailModel projectDetail){
        projectDetail.setName(mETProjectName.getText().toString());
        if( mETProjectTargetPrice.getText().length() > 0 ){
            projectDetail.setTargetFund(Integer.parseInt(mETProjectTargetPrice.getText().toString()));
        }
        if( mETRaiseDay.getText().length() > 0 ){
            projectDetail.setRaiseDay(Integer.parseInt(mETRaiseDay.getText().toString()));
        }
        if( mETTeamSize.getText().length() > 0 ){
            projectDetail.setTeamSize(Integer.parseInt(mETTeamSize.getText().toString()));
        }
        if( mRBSelectedProjectType != null ){
            projectDetail.setCategory(mRBSelectedProjectType.getText().toString());
        }

        projectDetail.setAddress(mETAddress.getText().toString());
        projectDetail.setVideoUrl(mETVideoUrl.getText().toString());
        projectDetail.setSummary(mETProjectSummary.getText().toString());
        projectDetail.setActivityIntroduce(mETProjectIntroduce.getText().toString());
        projectDetail.setMarketAnalysis(mETMarketAnalysis.getText().toString());
        projectDetail.setTeamIntroduce(mETTeamIntroduce.getText().toString());
        projectDetail.setTags(mETTags.getText().toString());
        projectDetail.setCreatorId(CurrentUser.getCurrentUser().getUserId());
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_edit, menu);
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
