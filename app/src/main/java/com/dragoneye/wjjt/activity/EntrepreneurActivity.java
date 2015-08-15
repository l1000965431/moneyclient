package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.activity.base.DoubleClickExitActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.activity.fragments.HomeEntrepreneurFragment;
import com.dragoneye.wjjt.activity.fragments.HomeInvestmentFragment;
import com.dragoneye.wjjt.activity.fragments.HomeMyselfFragment;
import com.dragoneye.wjjt.activity.fragments.HomeRecordFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.user.CurrentUser;
import com.dragoneye.wjjt.user.UserBase;


public class EntrepreneurActivity extends DoubleClickExitActivity implements View.OnClickListener{

    public static final int TAB_PROJECT_LIST = 0;
    public static final int TAB_MYSELF = 1;

    public static final int TAB_COUNT = 2;

    private class BottomButton{
        public ImageView imageView;
        public ImageView dot;
        public TextView textView;
        public void setChecked(boolean checked){
            if(checked){
                imageView.setColorFilter(getResources().getColor(R.color.home_bottom_button_selected));
                textView.setTextColor(getResources().getColor(R.color.home_bottom_button_selected));
            }else {
                imageView.setColorFilter(getResources().getColor(R.color.home_bottom_button_unselected));
                textView.setTextColor(getResources().getColor(R.color.home_bottom_button_unselected));
            }
        }
        public void setShowDot(boolean isShow){
            if(isShow){
                dot.setVisibility(View.VISIBLE);
            }else {
                dot.setVisibility(View.INVISIBLE);
            }
        }
    }

    private ViewPager viewPager;
    private BottomButton projectListButton,  myselfButton;
    private BottomButton currentCheckedButton;
    private FragmentAdapter mFragmentAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_switch_developer_bottom);
        initView();
    }

    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        projectListButton = new BottomButton();
        projectListButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_record_imageView);
        projectListButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_record_textView);
        projectListButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_record_red);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.function_switch_bottom_button_record);
        linearLayout.setOnClickListener(this);

        myselfButton = new BottomButton();
        myselfButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_me_imageView);
        myselfButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_me_textView);
        myselfButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_me_red);
        linearLayout = (LinearLayout)findViewById(R.id.function_switch_bottom_button_me);
        linearLayout.setOnClickListener(this);

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setOffscreenPageLimit(2);
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentCheckedButton != null) {
                    currentCheckedButton.setChecked(false);
                }
                switch (position) {
                    case TAB_PROJECT_LIST:
                        projectListButton.setChecked(true);
                        currentCheckedButton = projectListButton;
                        break;
                    case TAB_MYSELF:
                        myselfButton.setChecked(true);
                        currentCheckedButton = myselfButton;
                        break;
                }
                BaseFragment baseFragment = (BaseFragment)mFragmentAdapter.getItem(position);
                baseFragment.onSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.setOnPageChangeListener(onPageChangeListener);
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.function_switch_bottom_button_record:
                viewPager.setCurrentItem(TAB_PROJECT_LIST);
                break;
            case R.id.function_switch_bottom_button_me:
                viewPager.setCurrentItem(TAB_MYSELF);
                break;
            default:
                break;
        }
    }

    private class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            switch (id) {
                case TAB_PROJECT_LIST:
                    return new HomeEntrepreneurFragment();
                case TAB_MYSELF:
                    return new HomeMyselfFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    public static void startSubmitProjectActivity(final Activity activity, UserBase userBase){
        if(userBase.isPerfectInfo()){
            Intent intent = new Intent(activity, ProjectEditActivity.class);
            activity.startActivity(intent);
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton("去完善资料", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(activity, ImproveUserInfoActivity.class);
                            activity.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setMessage("提交项目需要您先完善资料，现在去完善吗？")
                    .create();
            alertDialog.show();

        }
    }

}
