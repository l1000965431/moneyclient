package com.dragoneye.wjjt.activity.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.AgreementActivity;
import com.dragoneye.wjjt.activity.ChargeActivity;
import com.dragoneye.wjjt.activity.DeveloperAttentionActivity;
import com.dragoneye.wjjt.activity.EntrepreneurActivity;
import com.dragoneye.wjjt.activity.ImproveUserInfoActivity;
import com.dragoneye.wjjt.activity.ProjectEditActivity;
import com.dragoneye.wjjt.activity.SettingsActivity;
import com.dragoneye.wjjt.activity.UserInfoActivity;
import com.dragoneye.wjjt.activity.WithdrawActivity;
import com.dragoneye.wjjt.activity.WithdrawSelectActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.CurrentUser;
import com.dragoneye.wjjt.user.UserBase;
import com.dragoneye.wjjt.view.RoundCornerImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by happysky on 15-6-19.
 */
public class HomeMyselfFragment extends BaseFragment implements View.OnClickListener{
    private TextView mTVUserName;
    private TextView mTVWalletBalance;
    private RoundCornerImageView mIVPortrait;

    private TextView mTVMicroTicket;
    private TextView mTVLeadTicket;
    private TextView mTVExp;

    private ImageView mIVMicroTicketNew;
    private ImageView mIVLeadTicketNew;
    private ImageView mIVExpNew;

    private TextView mTVUserInviteCode;
    private EditText mETInviteCode;

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
        View walletDesc = getActivity().findViewById(R.id.textView16);

        mIVPortrait = (RoundCornerImageView)getActivity().findViewById(R.id.home_self_group_iv_portrait);

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

        // 开发者注意事项
        View devStuff = getActivity().findViewById(R.id.home_self_group_dev_stuff);
        devStuff.setOnClickListener(this);

        // 用户推荐人id
        UserBase userBase = ((MyApplication) getActivity().getApplication()).getCurrentUser(getActivity());
        mTVUserInviteCode = (TextView)getActivity().findViewById(R.id.home_self_group_tv_user_invite_code);
        mTVUserInviteCode.setText("ID:" + userBase.getUserInviteCode());

        View clickCopy = getActivity().findViewById(R.id.home_self_group_tv_click_copy);
        clickCopy.setOnClickListener(this);

        // 微券
        View microTicketButton = getActivity().findViewById(R.id.home_self_group_linearLayout_micro_ticket);
        microTicketButton.setOnClickListener(this);
        mIVMicroTicketNew = (ImageView)getActivity().findViewById(R.id.home_self_group_iv_micro_ticket_new);
        mTVMicroTicket = (TextView)getActivity().findViewById(R.id.home_self_group_tv_micro_ticket);

        // 领投券
        View leadTicketButton = getActivity().findViewById(R.id.home_self_group_linearLayout_lead_ticket);
        leadTicketButton.setOnClickListener(this);
        mIVLeadTicketNew = (ImageView)getActivity().findViewById(R.id.home_self_group_iv_lead_ticket_new);
        mTVLeadTicket = (TextView)getActivity().findViewById(R.id.home_self_group_tv_lead_ticket);

        // 经验
        View expButton = getActivity().findViewById(R.id.home_self_group_linearLayout_exp);
        expButton.setOnClickListener(this);
        mIVExpNew = (ImageView)getActivity().findViewById(R.id.home_self_group_iv_exp_new);
        mTVExp = (TextView)getActivity().findViewById(R.id.home_self_group_tv_exp_new);

        // 推荐人ID
        if( userBase.isInvited() ){
            View inputInviteCodeLayout = getActivity().findViewById(R.id.home_self_group_ll_input_invite_code);
            inputInviteCodeLayout.setVisibility(View.GONE);
        }else {
            mETInviteCode = (EditText)getActivity().findViewById(R.id.home_self_group_et_invite_code);
            mETInviteCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() >= 6) {
                        onAddUserExp(s.toString());
                        mETInviteCode.setText("");
                    }
                }
            });
        }


        if(((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserType() == UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
            chargeButton.setVisibility(View.GONE);
            withdrawButton.setVisibility(View.GONE);
            walletDesc.setVisibility(View.GONE);
            mTVWalletBalance.setVisibility(View.GONE);
        }else {
            devStuff.setVisibility(View.GONE);
        }

        mTVUserName.setText(((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserName());
    }

    private void initData(){

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getUserVisibleHint()){
            updateUI();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            updateUI();
        }
    }

    private void updateUI(){
        if(((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserType() != UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR){
            handler.post(getWalletBalance_r);
        }

        String userPortrait = ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserHeadPortrait();
        if( userPortrait != null && userPortrait.length() > 0 ){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(false)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.mipmap.icon_albums)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
            ImageLoader.getInstance().displayImage(userPortrait, mIVPortrait, options);
        }
    }

    @Override
    public void onSelected(){
        super.onSelected();

    }

    Runnable getWalletBalance_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            MyApplication application = (MyApplication)getActivity().getApplication();
            params.put("userId", application.getCurrentUser(getActivity()).getUserId());
            params.put("token", application.getToken(getActivity()));

            HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "User/getUserSetInfo", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        UIHelper.toast(getActivity(), "服务器繁忙，稍后再试");
                        return;
                    }
                    int balance = 0, exp = 0;

                    try {
                        JSONObject object = new JSONObject(s);
                        exp = object.getInt("Exp");
                        balance = object.getInt("wallet");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mTVWalletBalance.setText(ToolMaster.convertToPriceString(balance));
                    mTVExp.setText(String.format("经验：%d", exp));
                }
            });
        }
    };

    private void onAddUserExp(String inviteCode){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在验证推荐人ID，请稍后");
        progressDialog.show();

        HttpParams params = new HttpParams();

        UserBase userBase = ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity());
        params.put("userId", userBase.getUserId());
        params.put("inviteCode", inviteCode);

        HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "User/AddUserExp", params, new HttpClient.MyHttpHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                progressDialog.dismiss();
                UIHelper.toast(getActivity(), "无法连接到服务器，请稍后再试");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                progressDialog.dismiss();
                int exp = 0;
                try{
                    exp = Integer.parseInt(s);
                }catch (Exception e){
                    e.printStackTrace();
                    UIHelper.toast(getActivity(), "服务器繁忙，请稍后再试");
                }
                if(exp == 0){
                    UIHelper.toast(getActivity(), "推荐人ID不正确，请输入正确的推荐人ID");
                }else {
                    UIHelper.toast(getActivity(), String.format("获得%dEXP", exp));
                }
            }
        });
    }

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
                WithdrawSelectActivity.CallActivity(getActivity());
                break;
            case R.id.home_self_group_iv_portrait:
                onChangePortrait();
                break;
            case R.id.home_self_group_dev_stuff:    // 开发者注意事项
                DeveloperAttentionActivity.CallActivity(getActivity());
                break;
            case R.id.home_self_group_linearLayout_micro_ticket:
                onMicroTicket();
                break;
            case R.id.home_self_group_linearLayout_lead_ticket:
                onLeadTicket();
                break;
            case R.id.home_self_group_linearLayout_exp:
                onExp();
                break;
            case R.id.home_self_group_tv_click_copy:
                onCopyUserInviteCode();
                break;
        }
    }

    private void onCopyUserInviteCode(){
        UserBase userBase = ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity());
        ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, userBase.getUserInviteCode());
        clipboardManager.setPrimaryClip(clipData);
        UIHelper.toast(getActivity(), "已复制到粘帖板");
    }

    private void onChangePortrait(){

    }

    private void onUserInfo(){
        if( ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).isPerfectInfo() ){
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

    private void onMicroTicket(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage("微券")
                .create();
        alertDialog.show();
    }

    private void onLeadTicket(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage("领投券")
                .create();
        alertDialog.show();
    }

    private void onExp(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage("经验")
                .create();
        alertDialog.show();
    }
}
