package com.dragoneye.money.activity.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.InvestProjectActivity;
import com.dragoneye.money.config.PullRefreshConfig;
import com.dragoneye.money.dao.InvestedProject;
import com.dragoneye.money.dao.InvestedProjectDao;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.Project;
import com.dragoneye.money.dao.ProjectDao;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.view.RefreshableView;
import com.dragoneye.money.view.TopTabButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by happysky on 15-6-19.
 */
public class HomeRecordFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private TopTabButton mIncomingButton, mInvestmentButton, mBonusButton, mSearchButton, mCurrentSelectButton;
    private RefreshableView refreshableView;
    private ListView mListView;
    private HashMap<Long, Project> mProjects = new HashMap<>();
    private ArrayList<InvestedProject> mInvestedProjects = new ArrayList<>();
    private InvestmentListViewAdapter mAdapter;

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

        mInvestedProjects = new ArrayList<>();
        mAdapter = new InvestmentListViewAdapter(getActivity(), mInvestedProjects);
        mListView.setAdapter(mAdapter);
    }

    private void initData(){
        InvestedProjectDao investedProjectDao = MyDaoMaster.getDaoSession().getInvestedProjectDao();
        mInvestedProjects.addAll(investedProjectDao.loadAll());



        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        InvestedProject investedProject = (InvestedProject)mListView.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), InvestProjectActivity.class);
        intent.putExtra(InvestProjectActivity.EXTRA_PROJECT_ID, investedProject.getProjectId());
        startActivity(intent);
    }

    public class InvestmentListViewAdapter extends BaseAdapter {
        private List<InvestedProject> data;
        private Context context;
        private LayoutInflater mInflater;

        public InvestmentListViewAdapter(Context context, List<InvestedProject> data){
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
            InvestedProject investedProject = (InvestedProject)getItem(position);

            ProjectDao projectDao = MyDaoMaster.getDaoSession().getProjectDao();
            Project project = projectDao.load(investedProject.getProjectId());

            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.imageView);
                viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.textView);
                viewHolder.tvDay = (TextView)convertView.findViewById(R.id.textView5);
                viewHolder.tvHour = (TextView)convertView.findViewById(R.id.textView7);
                viewHolder.tvMinute = (TextView)convertView.findViewById(R.id.textView9);
                viewHolder.tvInvestInfo = (TextView)convertView.findViewById(R.id.textView12);
                viewHolder.tvAwarding = (TextView)convertView.findViewById(R.id.textView2);
                viewHolder.tvAwardTarget = (TextView)convertView.findViewById(R.id.textView3);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();
            QueryBuilder queryBuilder = projectImageDao.queryBuilder();
            queryBuilder.where(ProjectImageDao.Properties.ProjectId.eq(project.getId()));
            ProjectImage projectImage = (ProjectImage)queryBuilder.build().list().get(0);
            if(projectImage != null){
                try{
                    viewHolder.ivLogo.setImageBitmap( MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            Uri.parse(projectImage.getImageUrl())));
                }catch (IOException e){

                }
            }

            String string = String.format(getString(R.string.invest_project_invested_price), investedProject.getPrice());
            viewHolder.tvInvestInfo.setText(string);

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvDescription;
            TextView tvDay;
            TextView tvHour;
            TextView tvMinute;
            TextView tvInvestInfo;
            TextView tvAwarding;
            TextView tvAwardTarget;
        }
    }
}
