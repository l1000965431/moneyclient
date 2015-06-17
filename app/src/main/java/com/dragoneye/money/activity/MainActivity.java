package com.dragoneye.money.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.fragments.Fragment1;
import com.dragoneye.money.activity.fragments.Fragment2;


public class MainActivity extends BaseActivity implements View.OnClickListener{
    public static final int TAB_HOME = 0;
    public static final int TAH_2 = 1;

    private ViewPager viewPager;
    private RadioButton radioButton1, radioButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        addListener();
    }

    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        radioButton1 = (RadioButton)findViewById(R.id.radioButton);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);

        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    private void addListener(){
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case TAB_HOME:
                        radioButton1.setChecked(true);
                        break;
                    case TAH_2:
                        radioButton2.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.radioButton:
                viewPager.setCurrentItem(TAB_HOME);
                break;
            case R.id.radioButton2:
                viewPager.setCurrentItem(TAH_2);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FragmentAdapter extends FragmentPagerAdapter{
        public final static int TAB_COUNT = 2;
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            switch (id) {
                case MainActivity.TAB_HOME:
                    Fragment1 homeFragment = new Fragment1();
                    return homeFragment;
                case TAH_2:
                    Fragment2 fragment2 = new Fragment2();
                    return fragment2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
