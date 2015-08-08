package com.dragoneye.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.BaseActivity;
import com.dragoneye.money.activity.fragments.HomeInvestmentFragment;
import com.dragoneye.money.activity.fragments.HomeMyselfFragment;
import com.dragoneye.money.activity.fragments.HomeRecordFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    public static final int TAB_INVESTMENT = 0;
    public static final int TAB_RECORD = 1;
    public static final int TAB_MYSELF = 2;

    public static final int TAB_COUNT = 3;

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
    private BottomButton investButton, recordButton, myselfButton;
    private BottomButton currentCheckedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_switch_bottom);
        initView();
        addListener();
    }

    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        investButton = new BottomButton();
        investButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_investment_imageView);
        investButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_investment_textView);
        investButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_investment_red);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.function_switch_bottom_button_investment);
        linearLayout.setOnClickListener(this);

        recordButton = new BottomButton();
        recordButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_record_imageView);
        recordButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_record_textView);
        recordButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_record_red);
        linearLayout = (LinearLayout)findViewById(R.id.function_switch_bottom_button_record);
        linearLayout.setOnClickListener(this);

        myselfButton = new BottomButton();
        myselfButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_me_imageView);
        myselfButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_me_textView);
        myselfButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_me_red);
        linearLayout = (LinearLayout)findViewById(R.id.function_switch_bottom_button_me);
        linearLayout.setOnClickListener(this);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void addListener(){
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.setOnPageChangeListener(onPageChangeListener);
        onPageChangeListener.onPageSelected(TAB_INVESTMENT);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.function_switch_bottom_button_investment:
                viewPager.setCurrentItem(TAB_INVESTMENT);
                break;
            case R.id.function_switch_bottom_button_record:
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_menu_submit_project) {
            Intent intent = new Intent(this, ProjectEditActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.main_menu_project_detail){
            Intent intent = new Intent(this, InvestProjectActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.main_menu_improve_user_info){
            Intent intent = new Intent(this, ImproveUserInfoActivity.class);
            startActivity(intent);
            return true;
        }

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
}
