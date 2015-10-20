package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.activity.base.DoubleClickExitActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.activity.fragments.HomeInvestmentFragment;
import com.dragoneye.wjjt.activity.fragments.HomeMyselfFragment;
import com.dragoneye.wjjt.activity.fragments.HomeRecordFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.BroadcastConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.protocol.UserProtocol;
import com.dragoneye.wjjt.tool.PreferencesHelper;
import com.dragoneye.wjjt.user.CurrentUser;
import com.umeng.update.UmengUpdateAgent;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.smssdk.SMSSDK;

public class MainActivity extends DoubleClickExitActivity implements View.OnClickListener{
    public static final int TAB_INVESTMENT = 0;
    public static final int TAB_RECORD = 1;
    public static final int TAB_MYSELF = 2;

    public static final int TAB_COUNT = 3;

    private class BottomButton{
        public ImageView imageView;
        public ImageView dot;
        public TextView textView;
        public LinearLayout background;
        public void setChecked(boolean checked){
            if(checked){
                imageView.setColorFilter(getResources().getColor(R.color.home_bottom_button_selected));
                textView.setTextColor(getResources().getColor(R.color.home_bottom_button_selected));
                background.setBackgroundColor(0xffca0025);
            }else {
                imageView.setColorFilter(getResources().getColor(R.color.home_bottom_button_unselected));
                textView.setTextColor(getResources().getColor(R.color.home_bottom_button_unselected));
                background.setBackgroundColor(getResources().getColor(R.color.home_red));
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
    private BottomButton investButton, recordButton, myselfButton;
    private BottomButton currentCheckedButton;
    private FragmentAdapter mFragmentAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_switch_bottom);
        initView();
    }

    private void initView(){


        viewPager = (ViewPager)findViewById(R.id.viewpager);

        investButton = new BottomButton();
        investButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_investment_imageView);
        investButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_investment_textView);
        investButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_investment_red);
        investButton.background = (LinearLayout)findViewById(R.id.function_switch_bottom_button_investment);
        investButton.background.setOnClickListener(this);

        recordButton = new BottomButton();
        recordButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_record_imageView);
        recordButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_record_textView);
        recordButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_record_red);
        recordButton.background = (LinearLayout)findViewById(R.id.function_switch_bottom_button_record);
        recordButton.background.setOnClickListener(this);

        myselfButton = new BottomButton();
        myselfButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_me_imageView);
        myselfButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_me_textView);
        myselfButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_me_red);
        myselfButton.background = (LinearLayout)findViewById(R.id.function_switch_bottom_button_me);
        myselfButton.background.setOnClickListener(this);

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setOffscreenPageLimit(3);
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
                    case TAB_INVESTMENT:
                        investButton.setChecked(true);
                        currentCheckedButton = investButton;
                        break;
                    case TAB_RECORD:
                        recordButton.setChecked(true);
                        currentCheckedButton = recordButton;
                        break;
                    case TAB_MYSELF:
                        myselfButton.setChecked(true);
                        currentCheckedButton = myselfButton;
                        break;
                }
                BaseFragment baseFragment = (BaseFragment)mFragmentAdapter.getItem(position);
//                baseFragment.onSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.setOnPageChangeListener(onPageChangeListener);
        investButton.setChecked(true);
        currentCheckedButton = investButton;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private IntentFilter intentFilter = new IntentFilter(BroadcastConfig.NEW_EARNING_MESSAGE);
    private BroadcastReceiver earningMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recordButton.setShowDot(true);
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(earningMessageReceiver, intentFilter);
        if( PreferencesHelper.isHaveEarningMessage(this) ){
            recordButton.setShowDot(true);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(earningMessageReceiver);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.function_switch_bottom_button_investment:
                viewPager.setCurrentItem(TAB_INVESTMENT);
                break;
            case R.id.function_switch_bottom_button_record:
                recordButton.setShowDot(false);
                PreferencesHelper.setIsHaveEarningMessage(this, false);
                viewPager.setCurrentItem(TAB_RECORD);
                break;
            case R.id.function_switch_bottom_button_me:
                viewPager.setCurrentItem(TAB_MYSELF);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SubMenu subMenu = menu.addSubMenu("消息");
        final MenuItem noticeItem = subMenu.getItem();
        noticeItem.setIcon(R.mipmap.icon_message_prompt);
        noticeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        noticeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                noticeItem.setIcon(R.mipmap.icon_message);
                NoticeActivity.CallActivity(MainActivity.this);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.main_menu_notice){
//            NoticeActivity.CallActivity(this);
            ShareSDK.initSDK(this);
            OnekeyShare oks = new OnekeyShare();

            oks.disableSSOWhenAuthorize();

            oks.setTitle("测试分享");

            oks.setText("我是分享文本");
//            // url仅在微信（包括好友和朋友圈）中使用
//            oks.setUrl("http://sharesdk.cn");
//            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//            oks.setComment("我是测试评论文本");
//            // site是分享此内容的网站名称，仅在QQ空间使用
//            oks.setSite(getString(R.string.app_name));
//            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//            oks.setSiteUrl("http://sharesdk.cn");

//
//// 启动分享GUI
            oks.show(this);

            return true;
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.main_menu_submit_project) {
//            Intent intent = new Intent(this, ProjectEditActivity.class);
//            startActivity(intent);
//            return true;
//        }
//
//        if(id == R.id.main_menu_project_detail){
//            Intent intent = new Intent(this, InvestProjectActivity.class);
//            startActivity(intent);
//            return true;
//        }
//
//        if(id == R.id.main_menu_improve_user_info){
//            Intent intent = new Intent(this, ImproveUserInfoActivity.class);
//            startActivity(intent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private class FragmentAdapter extends FragmentPagerAdapter{
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            switch (id) {
                case TAB_INVESTMENT:
                    return new HomeInvestmentFragment();
                case TAB_RECORD:
                    return new HomeRecordFragment();
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

    public static void CallMainActivity(Activity context){
        if( ((MyApplication)context.getApplication()).getCurrentUser(context).getUserType() == UserProtocol.PROTOCOL_USER_TYPE_INVESTOR ){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }else {
            Intent intent = new Intent(context, EntrepreneurActivity.class);
            context.startActivity(intent);
        }
    }
}
