package com.dragoneye.wjjt.activity.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.fragments.invest.NormalInvestFragment;
import com.dragoneye.wjjt.activity.fragments.invest.PreferentialInvestFragment;
import com.dragoneye.wjjt.view.TopTabButton;

/**
 * Created by happysky on 15-6-19.
 * 主界面-投资
 */
public class HomeInvestmentFragment extends BaseFragment {
    private static final int TAB_INVEST_NORMAL = 0;
    private static final int TAB_INVEST_PREFERENTIAL = 1;
    private static final int TAB_COUNT = 2;

    private TopTabButton mNormalButton, mPreferentialButton, mCurrentSelectButton;

    private ViewPager mViewPager;
    private FragmentAdapter mFragmentAdapter;

    NormalInvestFragment mNormalInvestFragment;
    PreferentialInvestFragment mPreferentialInvestFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.function_switch_top_top, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mViewPager != null){
            int curPos = mViewPager.getCurrentItem();
            BaseFragment baseFragment = (BaseFragment)((FragmentPagerAdapter)mViewPager.getAdapter()).getItem(curPos);
            if(isVisibleToUser){
                baseFragment.onShow();
            }else {
                baseFragment.onHide();
            }
        }

    }

    private void initView(){
        View.OnClickListener tabButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mCurrentSelectButton != null ){
                    mCurrentSelectButton.setChecked(false);
                }
                switch (v.getId()){
                    case R.id.home_invest_top_tab_ll_normal:
                        mNormalButton.setChecked(true);
                        mCurrentSelectButton = mNormalButton;
                        mViewPager.setCurrentItem(TAB_INVEST_NORMAL);
                        break;
                    case R.id.home_invest_top_tab_ll_preferential:
                        mPreferentialButton.setChecked(true);
                        mCurrentSelectButton = mPreferentialButton;
                        mViewPager.setCurrentItem(TAB_INVEST_PREFERENTIAL);
                        break;
                }
            }
        };

        mNormalButton = new TopTabButton(getActivity());
        mNormalButton.imageView = (ImageView)getActivity().findViewById(R.id.home_invest_top_tab_ll_normal_iv);
        mNormalButton.textView = (TextView)getActivity().findViewById(R.id.home_invest_top_tab_ll_normal_tv);
        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_invest_top_tab_ll_normal);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mPreferentialButton = new TopTabButton(getActivity());
        mPreferentialButton.imageView = (ImageView)getActivity().findViewById(R.id.home_invest_top_tab_ll_preferential_iv);
        mPreferentialButton.textView = (TextView)getActivity().findViewById(R.id.home_invest_top_tab_ll_preferential_tv);
        LinearLayout linearLayout1 = (LinearLayout)getActivity().findViewById(R.id.home_invest_top_tab_ll_preferential);
        linearLayout1.setOnClickListener(tabButtonOnClickListener);


        mViewPager = (ViewPager)getActivity().findViewById(R.id.home_invest_main_viewPager);
        mFragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mCurrentSelectButton != null) {
                    mCurrentSelectButton.setChecked(false);
                }
                switch (position) {
                    case TAB_INVEST_NORMAL:
                        mNormalButton.setChecked(true);
                        mCurrentSelectButton = mNormalButton;
                        break;
                    case TAB_INVEST_PREFERENTIAL:
                        mPreferentialButton.setChecked(true);
                        mCurrentSelectButton = mPreferentialButton;
                        break;
                }
                BaseFragment baseFragment = (BaseFragment) mFragmentAdapter.getItem(position);
                baseFragment.onSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabButtonOnClickListener.onClick(linearLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(mCurrentSelectButton == mNormalButton){
            mNormalInvestFragment.onActivityResult(requestCode, resultCode, data);
        }else if(mCurrentSelectButton == mPreferentialButton){
            mPreferentialInvestFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            switch (id) {
                case TAB_INVEST_NORMAL:
                    if(mNormalInvestFragment == null){
                        mNormalInvestFragment = new NormalInvestFragment();
                    }
                    return mNormalInvestFragment;
                case TAB_INVEST_PREFERENTIAL:
                    if(mPreferentialInvestFragment == null){
                        mPreferentialInvestFragment = new PreferentialInvestFragment();
                    }
                    return mPreferentialInvestFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
