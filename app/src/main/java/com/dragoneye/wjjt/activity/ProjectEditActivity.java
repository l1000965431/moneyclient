package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.ImageSelectedActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.dao.Project;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.MyProjectModel;
import com.dragoneye.wjjt.model.ProjectDetailModel;
import com.dragoneye.wjjt.protocol.UploadProjectProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


public class ProjectEditActivity extends ImageSelectedActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {
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

    private ArrayList<Uri> mImageUri = new ArrayList<>();
    private ArrayList<String> mImagesURL = new ArrayList<>();
    private String mUploadToken;

    private ArrayList<RadioButton> mRBProjectTypeList = new ArrayList<>();
    private CompoundButton mRBSelectedProjectType;

    private ImageView mIVImageViews[] = new ImageView[MAX_IMAGE_NUM];

    ProjectDetailModel mDetailModel = new ProjectDetailModel();
    ProgressDialog progressDialog;

    private int uploadingImageIndex = 0;
    private boolean isCancelUpload = true;
    UploadManager uploadManager;

    private boolean mIsReeditMode = false;

    public static void CallActivity(Activity activity, MyProjectModel myProjectModel){
        Intent intent = new Intent(activity, ProjectEditActivity.class);
        intent.putExtra("myProjectModel", myProjectModel);
        activity.startActivity(intent);
    }

    private void readIntent(){
        MyProjectModel myProjectModel = (MyProjectModel)getIntent().getSerializableExtra("myProjectModel");
        if( myProjectModel != null ){
            mIsReeditMode = true;
            mETProjectName.setText(myProjectModel.getName());
            mETProjectTargetPrice.setText(String.valueOf(myProjectModel.getTargetFund()));
            mETRaiseDay.setText(String.valueOf(myProjectModel.getRaiseDay()));
            mETTeamSize.setText(String.valueOf(myProjectModel.getTeamSize()));
            mETAddress.setText(myProjectModel.getAddress());
            mImageUri.clear();
            try{
                JSONArray array = new JSONArray(myProjectModel.getImageUrl());
                for(int i = 0; i < array.length(); i++){
                    Uri uri = Uri.parse(array.getString(i));
                    addImageToShow(uri);
                }
            }catch (JSONException e){

            }
            mETVideoUrl.setText(myProjectModel.getVideoUrl());
            mETProjectSummary.setText(myProjectModel.getSummary());
            mETProjectIntroduce.setText(myProjectModel.getActivityIntroduce());
            mETMarketAnalysis.setText(myProjectModel.getMarketAnalysis());
            mETProfitModel.setText(myProjectModel.getProfitMode());
            mETTeamIntroduce.setText(myProjectModel.getTeamIntroduce());
            mETTags.setText(myProjectModel.getTags());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_project_launched);
        initView();
        initData();
        readIntent();
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
        CheckBox checkBox = (CheckBox)findViewById(R.id.fragment_register_Agreement_checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTVSaveProject.setEnabled(isChecked);
                if (isChecked) {
                    mTVSaveProject.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded10blue));
                } else {
                    mTVSaveProject.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rounded12));
                }
            }
        });
        checkBox.setChecked(true);
        checkBox.setChecked(false);
        View agreement = findViewById(R.id.fragment_register_Agreement_text);
        agreement.setOnClickListener(this);

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

        mIVImageViews[0] = (ImageView)findViewById(R.id.project_launched_iv_projectImage0);
        mIVImageViews[1] = (ImageView)findViewById(R.id.project_launched_iv_projectImage1);
        mIVImageViews[2] = (ImageView)findViewById(R.id.project_launched_iv_projectImage2);
        mIVImageViews[3] = (ImageView)findViewById(R.id.project_launched_iv_projectImage3);
        mIVImageViews[4] = (ImageView)findViewById(R.id.project_launched_iv_projectImage4);
        mIVImageViews[5] = (ImageView)findViewById(R.id.project_launched_iv_projectImage5);
        mIVImageViews[6] = (ImageView)findViewById(R.id.project_launched_iv_projectImage6);
        mIVImageViews[7] = (ImageView)findViewById(R.id.project_launched_iv_projectImage7);
        for(int i = 0; i < mIVImageViews.length; i++){
            mIVImageViews[i].setVisibility(View.GONE);
            mIVImageViews[i].setOnClickListener(this);
            mIVImageViews[i].setOnLongClickListener(this);
            mIVImageViews[i].setTag(i);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
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
            case R.id.project_launched_iv_projectImage0:
            case R.id.project_launched_iv_projectImage1:
            case R.id.project_launched_iv_projectImage2:
            case R.id.project_launched_iv_projectImage3:
            case R.id.project_launched_iv_projectImage4:
            case R.id.project_launched_iv_projectImage5:
            case R.id.project_launched_iv_projectImage6:
            case R.id.project_launched_iv_projectImage7:
                Object tag = v.getTag();
                if( tag != null ){
                    int index = (int)tag;
                    onPreviewImage(index);
                }
                break;
            case R.id.fragment_register_Agreement_text:
                AgreementActivity.CallThisActivity(this);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v){
        switch (v.getId()){
            case R.id.project_launched_iv_projectImage0:
            case R.id.project_launched_iv_projectImage1:
            case R.id.project_launched_iv_projectImage2:
            case R.id.project_launched_iv_projectImage3:
            case R.id.project_launched_iv_projectImage4:
            case R.id.project_launched_iv_projectImage5:
            case R.id.project_launched_iv_projectImage6:
            case R.id.project_launched_iv_projectImage7:
                Object tag = v.getTag();
                if( tag != null ){
                    int index = (int)tag;
                    removeOnImage(index);
                    return true;
                }
                break;
        }

        return false;
    }

    private void onPreviewImage(int index){
        ImageExplorerActivity.CallActivity(this, mImageUri, index);
    }

    private void onSelectedImage(){
        goToGallerySelect();
//        Intent intent = new Intent(this, ImageMulSelectedActivity.class);
//        startActivity(intent);
    }

    private void addImageToShow(Uri uri){
        if( mImageUri.size() >= MAX_IMAGE_NUM ){
            UIHelper.toast(this, "超出图片数量限制");
            return;
        }
        int currentSize = mImageUri.size();
        mIVImageViews[currentSize].setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(uri.toString(), mIVImageViews[currentSize]);
//        mIVImageViews[currentSize].setImageBitmap(ToolMaster.getBitmapFromUri(this, uri));

        mImageUri.add(uri);
    }

    private void removeOnImage(int index){
        if(index > mImageUri.size()){
            return;
        }

        mImageUri.remove(index);
        for(ImageView imageView : mIVImageViews){
            imageView.setVisibility(View.GONE);
        }

        for(int i = 0; i < mImageUri.size(); i++){
            mIVImageViews[i].setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(mImageUri.get(i).toString(), mIVImageViews[i]);
//            mIVImageViews[i].setImageBitmap(ToolMaster.getBitmapFromUri(this, mImageUri.get(i)));
        }
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

        addImageToShow(uri);
    }

    private void onSave(){
        if( !getProjectInput(mDetailModel) ){
            return;
        }

        if( mImageUri.size() == 0 ){
            UIHelper.toast(this, "请添加一张或多张项目图片");
            return;
        }

        if( mUploadToken == null ){
            isCancelUpload = false;
            progressDialog.setMessage("正在连接服务器");
            progressDialog.show();
            handler.post(getUploadToken_r);
        }else {
            startUploadImages();
        }
    }

    Runnable getUploadToken_r = new Runnable() {
        @Override
        public void run() {
            HttpClient.atomicPost(ProjectEditActivity.this, HttpUrlConfig.URL_ROOT + "ImageUploadController/getUploadToken",
                    null, new HttpClient.MyHttpHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                            cancelUpload();
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            mUploadToken = s;
                            startUploadImages();
                        }
                    });
        }
    };

    private void startUploadImages(){
        isCancelUpload = false;
        uploadingImageIndex = 0;
        uploadManager = new UploadManager();
        progressDialog.show();
        mImagesURL.clear();
        if( mIsReeditMode ){
            for(Uri uri : mImageUri){
                if( uri.toString().startsWith("http") ){
                    mImagesURL.add(uri.toString());
                }
            }
        }else {
            handler.post(uploadOneImage_r);
        }
    }

    private void cancelUpload(){
        isCancelUpload = true;
        progressDialog.dismiss();
    }

    Runnable uploadOneImage_r = new Runnable() {
        @Override
        public void run() {
            try{
                progressDialog.setMessage("正在上传图片: " + (uploadingImageIndex + 1) + "/" + mImageUri.size());
                String filePath = ToolMaster.getRealPathFromURI(ProjectEditActivity.this, mImageUri.get(uploadingImageIndex));
                if(filePath == null){
                    throw new Exception();
                }
                File file = new File(filePath);
                String key = UUID.randomUUID().toString();
                uploadManager.put(file, key, mUploadToken, new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        ++uploadingImageIndex;
                        mImagesURL.add(HttpUrlConfig.URL_IMAGE_SERVER_HEAD + s);
                        if(uploadingImageIndex < mImageUri.size()){
                            handler.post(uploadOneImage_r);
                        }else {
                            handler.post(uploadProject_r);
                        }
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String s, double v) {

                    }
                }, new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        return isCancelUpload;
                    }
                }));
            }catch (Exception e){
                UIHelper.toast(ProjectEditActivity.this, "发生意外错误，提交失败!");
                e.printStackTrace();
                cancelUpload();
            }
        }
    };

    Runnable uploadProject_r = new Runnable() {
        @Override
        public void run() {
            progressDialog.setMessage("正在提交项目...");
            String json = ToolMaster.gsonInstance().toJson(mImagesURL);
            mDetailModel.setImageUrl(json);
            HttpParams httpParams = new HttpParams();
            httpParams.putGsonData(mDetailModel);

            HttpClient.post(UploadProjectProtocol.URL_SUBMIT_PROJECT, httpParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    UIHelper.toast(ProjectEditActivity.this, "无法连接到服务器");
                    cancelUpload();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    Log.d("SubmitProject Success", s);
                    progressDialog.dismiss();
                    if( s != null && s.compareTo("success") == 0 ){
                        UIHelper.toast(ProjectEditActivity.this, "项目提交成功");
                        finish();
                    }else {
                        UIHelper.toast(ProjectEditActivity.this, "项目提交失败, 可能某些内容长度过长。");
                    }
                }
            });
        }
    };

    private boolean getProjectInput(ProjectDetailModel projectDetail){
        if(mETProjectName.getText().length() == 0 || mETProjectName.getText().length() > 15){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "", 15));
            UIHelper.toast(this, "请输入项目名称");
            return false;
        }
        projectDetail.setName(mETProjectName.getText().toString());

        try{
            if( mETProjectTargetPrice.getText().length() == 0 ){
                UIHelper.toast(this, "请输入筹资金额");
                return false;
            }
            projectDetail.setTargetFund(Integer.parseInt(mETProjectTargetPrice.getText().toString()));

            if( mETRaiseDay.getText().length() == 0 ){
                UIHelper.toast(this, "请输入筹资天数");
                return false;
            }
            projectDetail.setRaiseDay(Integer.parseInt(mETRaiseDay.getText().toString()));

            if( mETTeamSize.getText().length() == 0 ){
                UIHelper.toast(this, "请输入团队人数");
                return false;
            }
            projectDetail.setTeamSize(Integer.parseInt(mETTeamSize.getText().toString()));
        }catch (NumberFormatException e){
            UIHelper.toast(this, "筹资金额、筹资天数或团队人数输入有误!");
            return false;
        }


        if( mRBSelectedProjectType == null ){
            UIHelper.toast(this, "请选择项目类别");
            return false;
        }
        projectDetail.setCategory(mRBSelectedProjectType.getText().toString());

        if( mETAddress.getText().length() == 0 || mETAddress.getText().length() > 64){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "地址", 64));
            return false;
        }
        projectDetail.setAddress(mETAddress.getText().toString());

//        if( mETVideoUrl.getText().length() == 0 ){
//            UIHelper.toast(this, "请输入视频地址");
//            return false;
//        }
        projectDetail.setVideoUrl(mETVideoUrl.getText().toString());

        if( mETProjectSummary.getText().length() == 0 || mETProjectSummary.getText().length() > 140){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "项目简介", 140));
            return false;
        }
        projectDetail.setSummary(mETProjectSummary.getText().toString());

        if( mETProjectIntroduce.getText().length() == 0 || mETProjectIntroduce.getText().length() > 1000){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "项目详细介绍", 1000));
            return false;
        }
        projectDetail.setActivityIntroduce(mETProjectIntroduce.getText().toString());

        if( mETMarketAnalysis.getText().length() == 0 || mETMarketAnalysis.getText().length() > 1000 ){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "市场分析", 1000));
            return false;
        }
        projectDetail.setMarketAnalysis(mETMarketAnalysis.getText().toString());

        if( mETProfitModel.getText().length() == 0 || mETMarketAnalysis.getText().length() > 1000 ){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "盈利模式", 1000));
            return false;
        }
        projectDetail.setProfitMode(mETProfitModel.getText().toString());

        if( mETTeamIntroduce.getText().length() == 0 || mETTeamIntroduce.getText().length() > 320 ){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "团队介绍", 320));
            return false;
        }
        projectDetail.setTeamIntroduce(mETTeamIntroduce.getText().toString());

        if( mETTags.getText().length() == 0 || mETTags.getText().length() > 64 ){
            UIHelper.toast(this, String.format("%s格式不正确, 1到%d个中文或英文字符。", "标签", 64));
            return false;
        }
        projectDetail.setTags(mETTags.getText().toString());

        projectDetail.setCreatorId(((MyApplication)getApplication()).getCurrentUser(this).getUserId());

        return true;
    }
}
