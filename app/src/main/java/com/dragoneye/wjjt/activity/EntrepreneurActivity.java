package com.dragoneye.wjjt.activity;

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
import com.dragoneye.wjjt.activity.fragments.HomeInvestmentFragment;
import com.dragoneye.wjjt.activity.fragments.HomeMyselfFragment;
import com.dragoneye.wjjt.activity.fragments.HomeRecordFragment;


public class EntrepreneurActivity extends BaseActivity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_switch_developer_bottom);
        initView();
        addListener();
    }

    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        projectListButton = new BottomButton();
        projectListButton.imageView = (ImageView)findViewById(R.id.function_switch_bottom_button_record_imageView);
        projectListButton.textView = (TextView)findViewById(R.id.function_switch_bottom_button_record_textView);
        projectListButton.dot = (ImageView)findViewById(R.id.function_switch_bottom_button_record_red);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.function_switch_bottom_button_investment);
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
                    case TAB_PROJECT_LIST:
                        projectListButton.setChecked(true);
                        currentCheckedButton = projectListButton;
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
        onPageChangeListener.onPageSelected(TAB_PROJECT_LIST);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.function_switch_bottom_button_investment:
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
                    return new HomeInvestmentFragment();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entrepreneur, menu);
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
