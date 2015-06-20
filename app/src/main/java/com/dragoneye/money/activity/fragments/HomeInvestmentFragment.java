package com.dragoneye.money.activity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.money.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-6-19.
 * 主界面-投资
 */
public class HomeInvestmentFragment extends BaseFragment implements View.OnClickListener{
    private class TopButton{
        public ImageView imageView;
        public TextView textView;
        public void setChecked(boolean checked){
            if(checked){
                imageView.setVisibility(View.VISIBLE);
                textView.setTextColor(getResources().getColor(R.color.home_investment_top_button_selected));
            }else {
                imageView.setVisibility(View.INVISIBLE);
                textView.setTextColor(getResources().getColor(R.color.home_investment_top_button_unselected));
            }
        }
    }

    private TopButton mIncomingButton, mHotProjectButton, mSearchButton, mCurrentSelectedButton;
    private ListView mListView;
    private ArrayList<String> mDataArrays = new ArrayList<>();
    private InvestmentListViewAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.function_switch_top, container, false);
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
                if( mCurrentSelectedButton != null ){
                    mCurrentSelectedButton.setChecked(false);
                }
                switch (v.getId()){
                    case R.id.function_switch_bottom_button_investment:
                        mIncomingButton.setChecked(true);
                        mCurrentSelectedButton = mIncomingButton;
                        break;
                    case R.id.linearLayout4:
                        mHotProjectButton.setChecked(true);
                        mCurrentSelectedButton = mHotProjectButton;
                        break;
                    case R.id.linearLayout5:
                        mSearchButton.setChecked(true);
                        mCurrentSelectedButton = mSearchButton;
                        break;
                }
            }
        };

        mIncomingButton = new TopButton();
        mIncomingButton.imageView = (ImageView)getActivity().findViewById(R.id.function_switch_bottom_button_investment_imageView);
        mIncomingButton.textView = (TextView)getActivity().findViewById(R.id.function_switch_bottom_button_investment_textView);
        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.function_switch_bottom_button_investment);
        linearLayout.setOnClickListener(tabButtonOnClickListener);
        tabButtonOnClickListener.onClick(linearLayout);

        mHotProjectButton = new TopButton();
        mHotProjectButton.imageView = (ImageView)getActivity().findViewById(R.id.imageView4);
        mHotProjectButton.textView = (TextView)getActivity().findViewById(R.id.textView4);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayout4);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mSearchButton = new TopButton();
        mSearchButton.imageView = (ImageView)getActivity().findViewById(R.id.imageView5);
        mSearchButton.textView = (TextView)getActivity().findViewById(R.id.textView5);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayout5);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mListView = (ListView)getActivity().findViewById(R.id.home_investment_list_view);
        mListView.setDividerHeight(0);
    }

    private void initData(){
        mDataArrays = new ArrayList<>();
        mAdapter = new InvestmentListViewAdapter(getActivity(), mDataArrays);
        mListView.setAdapter(mAdapter);

        mDataArrays.add("");
        mDataArrays.add("");
        mDataArrays.add("");
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){

        }
    }

    public class InvestmentListViewAdapter extends BaseAdapter {
        private List<String> data;
        private Context context;
        private LayoutInflater mInflater;

        public InvestmentListViewAdapter(Context context, List<String> data){
            this.context = context;
            this.data = data;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount(){
            return data.size();
        }

        @Override
        public Object getItem(int position){
            return data.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public int getItemViewType(int position){
            return 0;
        }

        @Override
        public int getViewTypeCount(){
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.imageView);
                viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.textView);
                viewHolder.tvDay = (TextView)convertView.findViewById(R.id.textView5);
                viewHolder.tvHour = (TextView)convertView.findViewById(R.id.textView7);
                viewHolder.tvMiniute = (TextView)convertView.findViewById(R.id.textView9);
                viewHolder.tvInvestInfo = (TextView)convertView.findViewById(R.id.textView12);
                viewHolder.tvAwarding = (TextView)convertView.findViewById(R.id.textView2);
                viewHolder.tvAwardTarget = (TextView)convertView.findViewById(R.id.textView3);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvDescription;
            TextView tvDay;
            TextView tvHour;
            TextView tvMiniute;
            TextView tvInvestInfo;
            TextView tvAwarding;
            TextView tvAwardTarget;
        }
    }
}
