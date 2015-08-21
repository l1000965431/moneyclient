package com.dragoneye.wjjt.activity.fragments;

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
import com.dragoneye.wjjt.activity.fragments.record.EarningRecordFragment;
import com.dragoneye.wjjt.activity.fragments.record.InvestRecordFragment;
import com.dragoneye.wjjt.view.TopTabButton;

/**
 * Created by happysky on 15-6-19.
 * 记录主界面
 */
public class HomeRecordFragment extends BaseFragment {

    private static final int TAB_INVEST_RECORD = 0;
    private static final int TAB_EARNING_RECORD = 1;
    private static final int TAB_COUNT = 2;

    private TopTabButton mIncomingButton, mInvestmentButton, mCurrentSelectButton;

    private ViewPager mViewPager;
    private FragmentAdapter mFragmentAdapter;

    EarningRecordFragment mEarningRecordFragment;
    InvestRecordFragment mInvestRecordFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.function_record_top, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(mViewPager != null){
                int curPos = mViewPager.getCurrentItem();
                BaseFragment baseFragment = (BaseFragment)((FragmentPagerAdapter)mViewPager.getAdapter()).getItem(curPos);
                baseFragment.onShow();
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
                    case R.id.home_record_top_tab_ll_incoming:
                        mIncomingButton.setChecked(true);
                        mCurrentSelectButton = mIncomingButton;
                        mViewPager.setCurrentItem(TAB_INVEST_RECORD);
                        break;
                    case R.id.home_record_top_tab_ll_investment:
                        mInvestmentButton.setChecked(true);
                        mCurrentSelectButton = mInvestmentButton;
                        mViewPager.setCurrentItem(TAB_EARNING_RECORD);
                        break;
                }
            }
        };

        mIncomingButton = new TopTabButton(getActivity());
        mIncomingButton.imageView = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_iv);
        mIncomingButton.textView = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_tv);
        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mInvestmentButton = new TopTabButton(getActivity());
        mInvestmentButton.imageView = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_iv);
        mInvestmentButton.textView = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_tv);
        LinearLayout linearLayout1 = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_investment);
        linearLayout1.setOnClickListener(tabButtonOnClickListener);


        mViewPager = (ViewPager)getActivity().findViewById(R.id.home_record_main_viewPager);
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
                    case TAB_INVEST_RECORD:
                        mIncomingButton.setChecked(true);
                        mCurrentSelectButton = mIncomingButton;
                        break;
                    case TAB_EARNING_RECORD:
                        mInvestmentButton.setChecked(true);
                        mCurrentSelectButton = mInvestmentButton;
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

    private class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            switch (id) {
                case TAB_INVEST_RECORD:
                    if(mInvestRecordFragment == null){
                        mInvestRecordFragment = new InvestRecordFragment();
                    }
                    return mInvestRecordFragment;
                case TAB_EARNING_RECORD:
                    if(mEarningRecordFragment == null){
                        mEarningRecordFragment = new EarningRecordFragment();
                    }
                    return mEarningRecordFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
