package com.dragoneye.money.activity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.config.PullRefreshConfig;
import com.dragoneye.money.view.RefreshableView;
import com.dragoneye.money.view.TopTabButton;

/**
 * Created by happysky on 15-6-19.
 */
public class HomeRecordFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private TopTabButton mIncomingButton, mInvestmentButton, mBonusButton, mSearchButton, mCurrentSelectButton;
    private RefreshableView refreshableView;
    private ListView mListView;

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
        initData();
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
                        break;
                    case R.id.home_record_top_tab_ll_investment:
                        mInvestmentButton.setChecked(true);
                        mCurrentSelectButton = mInvestmentButton;
                        break;
                    case R.id.home_record_top_tab_ll_bonus:
                        mBonusButton.setChecked(true);
                        mCurrentSelectButton = mBonusButton;
                        break;
                    case R.id.home_record_top_tab_ll_search:
                        mSearchButton.setChecked(true);
                        mCurrentSelectButton = mSearchButton;
                        break;
                }
            }
        };

        mIncomingButton = new TopTabButton(getActivity());
        mIncomingButton.imageView = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_iv);
        mIncomingButton.textView = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming_tv);
        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_incoming);
        linearLayout.setOnClickListener(tabButtonOnClickListener);
        tabButtonOnClickListener.onClick(linearLayout);

        mInvestmentButton = new TopTabButton(getActivity());
        mInvestmentButton.imageView = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_iv);
        mInvestmentButton.textView = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_investment_tv);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_investment);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mBonusButton = new TopTabButton(getActivity());
        mBonusButton.imageView = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_bonus_iv);
        mBonusButton.textView = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_bonus_tv);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_bonus);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mSearchButton = new TopTabButton(getActivity());
        mSearchButton.imageView = (ImageView)getActivity().findViewById(R.id.home_record_top_tab_ll_search_iv);
        mSearchButton.textView = (TextView)getActivity().findViewById(R.id.home_record_top_tab_ll_search_tv);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.home_record_top_tab_ll_search);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_record_refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, PullRefreshConfig.FRAGMENT_HOME_RECORD);

        mListView = (ListView)getActivity().findViewById(R.id.home_record_list_view);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);

    }

    private void initData(){

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

    }
}
