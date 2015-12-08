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
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.tool.PreferencesHelper;
import com.dragoneye.wjjt.view.TopTabButton;

/**
 * Created by happysky on 15-6-19.
 * 记录主界面
 */
public class HomeRecordFragment extends BaseFragment {

    private static final int TAB_INVEST_RECORD = 0;
    private static final int TAB_EARNING_RECORD = 1;
    private static final int TAB_COUNT = 2;

    private TopTabButton mInvestButton, mEarningButton, mCurrentSelectButton;

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
    public void onResume(){
        super.onResume();
        if(mEarningRecordFragment != null){
            mEarningRecordFragment.setTopButton(mEarningButton);
        }
        if(mInvestRecordFragment != null){
            mInvestRecordFragment.setTopButton(mInvestButton);
        }
    }

    @Override
    public void onNewMessage(String messageType){
        switch (messageType){
            case PreferencesConfig.IS_HAVE_NEW_INVEST_MESSAGE:
                mInvestButton.setIsHaveNew(true);
                break;
            case PreferencesConfig.IS_HAVE_NEW_EARNING_MESSAGE:
                mEarningButton.setIsHaveNew(true);
                break;
        }
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
                    case R.id.home_record_top_tab_ll_incoming:
                        mInvestButton.setChecked(true);
                        mCurrentSelectButton = mInvestButton;
                        mViewPager.setCurrentItem(TAB_INVEST_RECORD);
                        break;
                    case R.id.home_record_top_tab_ll_investment:
                        mEarningButton.setChecked(true);
                        mCurrentSelectButton = mEarningButton;
                        mViewPager.setCurrentItem(TAB_EARNING_RECORD);
                        break;
                }
            }
        };

        mInvestButton = new TopTabButton(getActivity());
        mInvestButton.ivLine = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_iv);
        mInvestButton.tvTitle = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_tv);
        mInvestButton.ivNewDot = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_iv_dot);
        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming);
        linearLayout.setOnClickListener(tabButtonOnClickListener);
        mInvestButton.setIsHaveNew(PreferencesHelper.isHaveNewMessage(getActivity(), PreferencesConfig.IS_HAVE_NEW_INVEST_MESSAGE));

        mEarningButton = new TopTabButton(getActivity());
        mEarningButton.ivLine = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_iv);
        mEarningButton.tvTitle = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_tv);
        mEarningButton.ivNewDot = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_iv_dot);
        LinearLayout linearLayout1 = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_investment);
        linearLayout1.setOnClickListener(tabButtonOnClickListener);
        mEarningButton.setIsHaveNew(PreferencesHelper.isHaveNewMessage(getActivity(), PreferencesConfig.IS_HAVE_NEW_EARNING_MESSAGE));


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
                        mInvestButton.setChecked(true);
                        mCurrentSelectButton = mInvestButton;
                        break;
                    case TAB_EARNING_RECORD:
                        mEarningButton.setChecked(true);
                        mCurrentSelectButton = mEarningButton;
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
                        mInvestRecordFragment.setTopButton(mInvestButton);
                    }
                    return mInvestRecordFragment;
                case TAB_EARNING_RECORD:
                    if(mEarningRecordFragment == null){
                        mEarningRecordFragment = new EarningRecordFragment();
                        mEarningRecordFragment.setTopButton(mEarningButton);
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
