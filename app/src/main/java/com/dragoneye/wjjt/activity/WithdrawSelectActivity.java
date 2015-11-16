package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.tool.UIHelper;

/**
 * Created by happysky on 15-9-28.
 *
 */
public class WithdrawSelectActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private RadioButton mRBWxPay;
    private RadioButton mRBAlipay;

    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, WithdrawSelectActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_order);

        mRBWxPay = (RadioButton)findViewById(R.id.home_self_group_order_rb_wxpay);
        mRBWxPay.setOnCheckedChangeListener(this);
        mRBAlipay = (RadioButton)findViewById(R.id.home_self_group_order_rb_alipay);
        mRBAlipay.setOnCheckedChangeListener(this);

        View ivWxPay = findViewById(R.id.home_self_group_order_iv_wxpay);
        ivWxPay.setOnClickListener(this);
        View ivAlipay = findViewById(R.id.home_self_group_order_iv_alipay);
        ivAlipay.setOnClickListener(this);

        View confirm = findViewById(R.id.rush_failure_tv_auto_close);
        confirm.setOnClickListener( this );
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rush_failure_tv_auto_close:
                onConfirmPayType();
                break;
            case R.id.home_self_group_order_iv_wxpay:
                mRBWxPay.setChecked(true);
                break;
            case R.id.home_self_group_order_iv_alipay:
                mRBAlipay.setChecked(true);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        if( !isChecked ){
            return;
        }

        switch (buttonView.getId()){
            case R.id.home_self_group_order_rb_wxpay:
                mRBAlipay.setChecked(false);
                break;
            case R.id.home_self_group_order_rb_alipay:
                mRBWxPay.setChecked(false);
                break;
        }
    }

    private void onConfirmPayType(){
        if(mRBWxPay.isChecked()){
            WithdrawActivity.CallActivity(this, WithdrawActivity.WITHDRAW_TYPE_WX);
        }else if(mRBAlipay.isChecked()){
            WithdrawActivity.CallActivity(this, WithdrawActivity.WITHDRAW_TYPE_ALIPAY);
        }else {
            UIHelper.toast(this, "请选择提现方式!");
        }
    }
}
