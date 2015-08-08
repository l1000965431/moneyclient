package com.dragoneye.money.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.ImageSelectedActivity;
import com.dragoneye.money.application.MyApplication;
import com.dragoneye.money.config.HttpUrlConfig;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.UserProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

public class UserInfoActivity extends ImageSelectedActivity implements View.OnClickListener{

    TextView mTVUserName;
    TextView mTVUserId;
    TextView mTVAddress;
    TextView mTVSexuality;
    ImageButton mIBPortrait;

    private String mUploadToken;
    ProgressDialog progressDialog;
    private boolean isCancelUpload = true;
    UploadManager uploadManager;
    File mSelectedFile;
    private String uploadPortraitUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_pinfo);
        initView();
        initData();
    }

    @Override
    protected void onMenuCreated(){
    }

    private void initView(){
        mTVUserName = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout1_num);
        mTVUserId = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout2_num);
        mTVAddress = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout3_num);
        mTVSexuality = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout5_num);
        mIBPortrait = (ImageButton)findViewById(R.id.home_self_group_pinfo_imageButton1_group);
        mIBPortrait.setOnClickListener(this);
        String userPortrait = ((MyApplication)getApplication()).getCurrentUser().getUserHeadPortrait();
        if( userPortrait != null && userPortrait.length() > 0 ){
            ImageLoader.getInstance().displayImage(userPortrait, mIBPortrait);
        }

        mTVUserName.setText(((MyApplication) getApplication()).getCurrentUser().getUserName());
        mTVUserId.setText(((MyApplication)getApplication()).getCurrentUser().getUserId());
        mTVAddress.setText(((MyApplication)getApplication()).getCurrentUser().getAddress());
        mTVSexuality.setText(((MyApplication)getApplication()).getCurrentUser().getSexualityString());

        progressDialog = new ProgressDialog(this);
    }

    private void initData(){
        setIsNeedCropImage(true);
    }

    Runnable getUploadToken_r = new Runnable() {
        @Override
        public void run() {
            HttpClient.atomicPost(UserInfoActivity.this, HttpUrlConfig.URL_ROOT + "ImageUploadController/getUploadToken",
                    null, new HttpClient.MyHttpHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            cancelUpload();
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            UIHelper.toast(UserInfoActivity.this, s);
                            mUploadToken = s;
                            startUploadImages();
                        }
                    });
        }
    };

    private void startUploadImages(){
        isCancelUpload = false;
        uploadManager = new UploadManager();
        progressDialog.show();
        handler.post(uploadOneImage_r);
    }

    private void cancelUpload(){
        isCancelUpload = true;
        progressDialog.dismiss();
    }

    Runnable uploadOneImage_r = new Runnable() {
        @Override
        public void run() {
            try{
                progressDialog.setMessage("正在上传图片");
                String key = "user_portrait_" + ((MyApplication)getApplication()).getCurrentUser().getUserId();
                uploadManager.put(mSelectedFile, key, mUploadToken, new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        uploadPortraitUrl = HttpUrlConfig.URL_IMAGE_SERVER_HEAD + s;
                        handler.post(updatePortrait_r);
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
                UIHelper.toast(UserInfoActivity.this, "发生意外错误，上传失败!");
                e.printStackTrace();
                cancelUpload();
            }
        }
    };

    Runnable updatePortrait_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();
            httpParams.put(UserProtocol.CHANGE_USER_PORTRAIT_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser().getUserId());
            httpParams.put(UserProtocol.CHANGE_USER_PORTRAIT_PARAM_URL, uploadPortraitUrl);

            HttpClient.atomicPost(UserInfoActivity.this, UserProtocol.URL_CHANGE_USER_PORTRAIT, httpParams, new HttpClient.MyHttpHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if( s == null ){
                        UIHelper.toast(UserInfoActivity.this, "服务器繁忙，请稍后再试");
                        cancelUpload();
                        return;
                    }
                    onUpdatePortraitResult(s);
                }
            });
        }
    };

    private void onUpdatePortraitResult(String s){
        try{
            Integer integer = Integer.parseInt(s);
            switch (integer){
                case UserProtocol.CHANGE_PORTRAIT_RESULT_SUCCESS:
                    ((MyApplication)getApplication()).getCurrentUser().setUserHeadPortrait(uploadPortraitUrl);
                    ImageLoader.getInstance().displayImage(uploadPortraitUrl, mIBPortrait);
                    cancelUpload();
                    break;
                case UserProtocol.CHANGE_PORTRAIT_RESULT_FAILED:
                default:
                    throw new Exception();
            }
        }catch (Exception e){
            UIHelper.toast(UserInfoActivity.this, "服务器繁忙，请稍后再试");
            cancelUpload();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.home_self_group_pinfo_imageButton1_group:
                goToPortraitSelect();
                break;
        }
    }

    @Override
    protected void onSelectedCropFinish(Bitmap bitmap, File file){
        mSelectedFile = file;
        if( mUploadToken == null ){
            progressDialog.setMessage("正在连接服务器");
            progressDialog.show();
            handler.post(getUploadToken_r);
        }else {
            progressDialog.show();
            handler.post(uploadOneImage_r);
        }
    }
}
