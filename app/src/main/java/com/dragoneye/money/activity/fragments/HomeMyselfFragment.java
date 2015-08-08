package com.dragoneye.money.activity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.ChargeActivity;
import com.dragoneye.money.activity.ImproveUserInfoActivity;
import com.dragoneye.money.activity.ProjectEditActivity;
import com.dragoneye.money.activity.SettingsActivity;
import com.dragoneye.money.activity.UserInfoActivity;
import com.dragoneye.money.application.MyApplication;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.protocol.UserProtocol;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.user.CurrentUser;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

/**
 * Created by happysky on 15-6-19.
 */
public class HomeMyselfFragment extends BaseFragment implements View.OnClickListener{
    private TextView mTVUserName;
    private TextView mTVWalletBalance;
    private ImageView mIVPortrait;

    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_self_group, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        mTVUserName = (TextView)getActivity().findViewById(R.id.home_selfui_name);

        mTVWalletBalance = (TextView)getActivity().findViewById(R.id.textView17);

        mIVPortrait = (ImageView)getActivity().findViewById(R.id.home_self_group_iv_portrait);
        String userPortrait = ((MyApplication)getActivity().getApplication()).getCurrentUser().getUserHeadPortrait();
        if( userPortrait != null && userPortrait.length() > 0 ){
            ImageLoader.getInstance().displayImage(userPortrait, mIVPortrait);
        }

        // 充值
        View chargeButton = getActivity().findViewById(R.id.home_self_group_linearLayout2);
        chargeButton.setOnClickListener(this);

        // 提现
        View withdrawButton = getActivity().findViewById(R.id.linearLayout4);
        withdrawButton.setOnClickListener(this);

        // 设置
        View settingsButton = getActivity().findViewById(R.id.home_self_group_linearLayout1);
        settingsButton.setOnClickListener(this);

        // 个人信息
        View userInfoButton = getActivity().findViewById(R.id.home_self_group_linearLayout3);
        userInfoButton.setOnClickListener(this);

        // 创建新项目
        View submitProjectButton = getActivity().findViewById(R.id.linearLayout21);
        submitProjectButton.setOnClickListener(this);
        if(((MyApplication)getActivity().getApplication()).getCurrentUser().getUserType() == UserProtocol.PROTOCOL_USER_TYPE_INVESTOR){
            submitProjectButton.setVisibility(View.GONE);
        }

        mTVUserName.setText(((MyApplication)getActivity().getApplication()).getCurrentUser().getUserName());

        handler.post(getWalletBalance_r);
    }

    private void initData(){

    }

    Runnable getWalletBalance_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put("userId", ((MyApplication)getActivity().getApplication()).getCurrentUser().getUserId());

            HttpClient.atomicPost(getActivity(), UserProtocol.URL_GET_WALLET_BALANCE, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        UIHelper.toast(getActivity(), "服务器繁忙，稍后再试");
                        return;
                    }
                    int balance = Integer.parseInt(s);
                    mTVWalletBalance.setText(ToolMaster.convertToPriceString(balance));
                }
            });
        }
    };

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.home_self_group_linearLayout1: // 设置
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.home_self_group_linearLayout3:
                onUserInfo();
                break;
            case R.id.home_self_group_linearLayout2: // 充值
                onCharge();
                break;
            case R.id.linearLayout4:    // 提现
                break;
            case R.id.linearLayout21:   // 创建项目
                intent = new Intent(getActivity(), ProjectEditActivity.class);
                startActivity(intent);
                break;
            case R.id.home_self_group_iv_portrait:
                onChangePortrait();
                break;
        }
    }

    private void onChangePortrait(){

    }

    private void onUserInfo(){
        if( ((MyApplication)getActivity().getApplication()).getCurrentUser().isPerfectInfo() ){
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getActivity(), ImproveUserInfoActivity.class);
            startActivity(intent);
        }
    }

    private void onCharge(){
        Intent intent = new Intent(getActivity(), ChargeActivity.class);
        startActivity(intent);
    }
}
