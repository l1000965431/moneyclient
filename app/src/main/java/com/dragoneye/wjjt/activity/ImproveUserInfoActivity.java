package com.dragoneye.wjjt.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.InputChecker;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;
import com.dragoneye.wjjt.user.UserBase;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;

public class ImproveUserInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView mTVPhoneNumber;
    private EditText mETUserName;
    private EditText mETEmail;
    private RadioGroup mRGSexuality;
    private EditText mETAddress;
    private EditText mETRealName;
    private EditText mETIdentityId;
    private EditText mETSelfIntroduction;
    private EditText mETEduInfo;
    private EditText mETCareer;
    private TextView mTVConfirmButton;

    private View mLLEntrepreneurInfo;

    ProgressDialog progressDialog;

    /**
     *
     * 擅长领域控件
     */
    private ArrayList<CheckBox> mCBExpertise = new ArrayList<>();
    private EditText mETCustomExpertise;
    private TextView mTVAddCustomExpertise;
    private LinearLayout mLLExpertiseRootLeft;
    private LinearLayout mLLExpertiseRootRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_project_people_info);
        initView();
        initData();
    }

    private void initView(){
        mTVPhoneNumber = (TextView)findViewById(R.id.improve_user_info_tv_tel);
        mETUserName = (EditText)findViewById(R.id.improve_user_info_et_user_name);
        mETEmail = (EditText)findViewById(R.id.improve_user_info_et_email);
        mRGSexuality = (RadioGroup)findViewById(R.id.improve_user_info_rg_sexuality);
        mETAddress = (EditText)findViewById(R.id.improve_user_info_et_address);
        mETRealName = (EditText)findViewById(R.id.improve_user_info_et_realName);
        mETIdentityId = (EditText)findViewById(R.id.improve_user_info_identityId);
        mETSelfIntroduction = (EditText)findViewById(R.id.improve_user_info_selfIntroduction);
        mETEduInfo = (EditText)findViewById(R.id.improve_user_info_et_eduInfo);
        mETCareer = (EditText)findViewById(R.id.improve_user_info_et_career);
        mTVConfirmButton = (TextView)findViewById(R.id.improve_user_info_tv_confirmButton);
        mTVConfirmButton.setOnClickListener(this);

        View agreement = findViewById(R.id.fragment_register_Agreement_text);
        agreement.setOnClickListener(this);

        mLLEntrepreneurInfo = findViewById(R.id.inmprove_user_info_ll_entrepreneurInfo);

        mTVPhoneNumber.setText(((MyApplication)getApplication()).getCurrentUser(this).getUserId());

        mLLExpertiseRootLeft = (LinearLayout)findViewById(R.id.improve_user_info_ll_expertise_rootL);
        mLLExpertiseRootRight = (LinearLayout)findViewById(R.id.improve_user_info_ll_expertise_rootR);
        mETCustomExpertise = (EditText)findViewById(R.id.improve_user_info_et_customExpertise);
        mTVAddCustomExpertise = (TextView)findViewById(R.id.improve_user_info_tv_addCustomExpertise);
        mTVAddCustomExpertise.setOnClickListener(this);

        mCBExpertise.add((CheckBox) findViewById(R.id.checkBox));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox2));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox3));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox4));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox5));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox6));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox7));
        mCBExpertise.add((CheckBox)findViewById(R.id.checkBox8));

        setUIMode(((MyApplication) getApplication()).getCurrentUser(this).getUserType());

        progressDialog = new ProgressDialog(this);
    }

    private void initData(){

    }

    private void setUIMode(int userType){
        if(userType == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
            mLLEntrepreneurInfo.setVisibility(View.VISIBLE);
        }else {
            mLLEntrepreneurInfo.setVisibility(View.GONE);
        }
    }

    private int getSexuality(){
        switch (mRGSexuality.getCheckedRadioButtonId()){
            case R.id.improve_user_info_rb_male:
                return UserBase.SEXUALITY_MALE;
            case R.id.improve_user_info_rb_female:
                return UserBase.SEXUALITY_FEMALE;
            case R.id.improve_user_info_rb_privacy:
            default:
                return UserBase.SEXUALITY_PRIVACY;
        }
    }

    private boolean checkUserInput(){
        if(mETUserName.getText().length() == 0){
            UIHelper.toast(this, "请输入用户名");
            return false;
        }
        if(!InputChecker.isEmail(mETEmail.getText().toString())){
            UIHelper.toast(this, "请输入正确的邮箱");
            return false;
        }
        if(mETRealName.getText().length() == 0){
            UIHelper.toast(this, "请输入真实姓名");
            return false;
        }
        if(!InputChecker.isIdCardNumber(mETIdentityId.getText().toString())){
            UIHelper.toast(this, "请输入正确的身份证号");
            return false;
        }

        if(((MyApplication)getApplication()).getCurrentUser(this).getUserType() == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
            if(mETAddress.getText().length() == 0){
                UIHelper.toast(this, "请输入地址");
                return false;
            }
            if(getExpertiseString().isEmpty()){
                UIHelper.toast(this, "请选择擅长领域");
                return false;
            }

            if(mETSelfIntroduction.getText().length() == 0){
                UIHelper.toast(this, "请输入个人介绍");
                return false;
            }
            if(mETEduInfo.getText().length() == 0){
                UIHelper.toast(this, "请输入教育经历");
                return false;
            }
            if(mETCareer.getText().length() == 0){
                UIHelper.toast(this, "请输入事业经历");
                return false;
            }
        }

        return true;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.improve_user_info_tv_confirmButton:
                onConfirm();
                break;
            case R.id.improve_user_info_tv_addCustomExpertise:
                onAddCustomExpertise();
                break;
            case R.id.fragment_register_Agreement_text:
                AgreementActivity.OpenAgreement(this);
                break;
        }
    }

    private void onAddCustomExpertise(){
        String customExpertise = mETCustomExpertise.getText().toString();
        if( customExpertise.isEmpty() ){
            return;
        }

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(customExpertise);
        checkBox.setTextSize(13);
        checkBox.setTextColor(0xff999999);
        mCBExpertise.add(checkBox);
        if (mCBExpertise.size() % 2 == 0) {
            mLLExpertiseRootRight.addView(checkBox);
        }else {
            mLLExpertiseRootLeft.addView(checkBox);
        }
        checkBox.setChecked(true);
        mETCustomExpertise.setText("");
    }

    private String getExpertiseString(){
        String expertise = "";
        for(CheckBox checkBox : mCBExpertise){
            if(checkBox.isChecked()){
                expertise += checkBox.getText().toString() + ";";
            }
        }

        return expertise;
    }

    private void onConfirm(){
        if( !checkUserInput() ){
            return;
        }

        progressDialog.show();
        final HttpParams params = new HttpParams();
        params.put(UserProtocol.IMPROVE_USER_INFO_PARAM_TOKEN, ((MyApplication)getApplication()).getToken(this));
        params.put(UserProtocol.IMPROVE_USER_INFO_PARAM_USER_TYPE, ((MyApplication)getApplication()).getCurrentUser(this).getUserType());
        final HashMap<String, String> userImproveInfo = getUserImproveInfo();
        params.put(UserProtocol.IMPROVE_USER_INFO_PARAM_INFO, ToolMaster.gsonInstance().toJson(userImproveInfo) );
        params.put(UserProtocol.IMPROVE_USER_INFO_PARAM_USER_ID, ((MyApplication)getApplication()).getCurrentUser(this).getUserId());

        HttpClient.atomicPost(this, UserProtocol.URL_IMPROVE_USER_INFO, params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                progressDialog.dismiss();
            }
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                progressDialog.dismiss();
                if (s == null) {
                    UIHelper.toast(ImproveUserInfoActivity.this, getString(R.string.http_server_exception));
                    return;
                }
                onImproveResult(s, userImproveInfo);
            }
        });

    }

    private void onImproveResult(String result, HashMap<String, String> userImproveInfo){
        int resultCode = 0;
        try{
            resultCode = Integer.parseInt(result);
        }catch (Exception e){
            UIHelper.toast(this, getString(R.string.http_server_exception));
        }

        switch (resultCode){
            case UserProtocol.IMPROVE_USER_INFO_RESULT_NEED_LOGIN:
                UIHelper.toast(this, "需要登录");
                break;
            case UserProtocol.IMPROVE_USER_INFO_RESULT_INFO_ERROR:
                UIHelper.toast(this, "用户信息有误");
                break;
            case UserProtocol.IMPROVE_USER_INFO_RESULT_INFO_TOKEN_INCORRECT:
                UIHelper.toast(this, "秘钥不正确");
                break;
            case UserProtocol.IMPROVE_USER_INFO_RESULT_INFO_USER_TYPE_ERROR:
                UIHelper.toast(this, "用户类型错误");
                break;
            case UserProtocol.IMPROVE_USER_INFO_RESULT_INFO_IDENTITY_CONFILCT:
                UIHelper.toast(this, "身份证号已存在");
                break;
            case UserProtocol.IMPROVE_USER_INFO_RESULT_INFO_EMAIL_CONFILCT:
                UIHelper.toast(this, "邮箱已被占用");
                break;
            case UserProtocol.IMPROVE_USER_INFO_RESULT_SUCCESS:
                UIHelper.toast(this, "更改成功");
                UserBase userBase = ((MyApplication)getApplication()).getCurrentUser(this);
                userBase.setIsPerfectInfo(true);
                userBase.setUserName(userImproveInfo.get("userName"));
                userBase.setEmail(userImproveInfo.get("mail"));
                userBase.setRealName(userImproveInfo.get("realName"));
                userBase.setIdentityId(userImproveInfo.get("identity"));
                if(userBase.getUserType() == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
                    userBase.setIntroduction(userImproveInfo.get("selfintroduce"));
                    userBase.setExpertise(userImproveInfo.get("goodAtField"));
                    userBase.setEduInfo(userImproveInfo.get("education"));
                    userBase.setCareer(userImproveInfo.get("personalProfile"));
                }

                finish();
                break;
        }
    }

    private HashMap<String, String> getUserImproveInfo(){
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("mail", mETEmail.getText().toString());
        userInfo.put("sex", String.valueOf(getSexuality()));
        userInfo.put("userName", mETUserName.getText().toString());
        userInfo.put("realName", mETRealName.getText().toString());
        userInfo.put("identity", mETIdentityId.getText().toString());

        if(((MyApplication)getApplication()).getCurrentUser(this).getUserType() == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
            userInfo.put("location", mETAddress.getText().toString());
            userInfo.put("education", mETEduInfo.getText().toString());
            userInfo.put("personalProfile", mETCareer.getText().toString());
            userInfo.put("selfintroduce", mETSelfIntroduction.getText().toString());
            userInfo.put("goodAtField", getExpertiseString());
        }

        return userInfo;
    }
}
