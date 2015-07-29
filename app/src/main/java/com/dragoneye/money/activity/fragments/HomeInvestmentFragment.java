package com.dragoneye.money.activity.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.InvestProjectActivity;
import com.dragoneye.money.config.PullRefreshConfig;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.Project;
import com.dragoneye.money.dao.ProjectDao;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.model.ProjectDetailModel;
import com.dragoneye.money.protocol.GetProjectListProtocol;
import com.dragoneye.money.view.GridViewWithHeaderAndFooter;
import com.dragoneye.money.view.RefreshableView;
import com.dragoneye.money.view.TopTabButton;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by happysky on 15-6-19.
 * 主界面-投资
 */
public class HomeInvestmentFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = HomeInvestmentFragment.class.getSimpleName();


    private TopTabButton mIncomingButton, mHotProjectButton, mSearchButton, mCurrentSelectedButton;
    private RefreshableView refreshableView;
    private GridViewWithHeaderAndFooter mGridView;
    private ArrayList<Project> mDataArrays = new ArrayList<>();
    private InvestmentListViewAdapter mAdapter;
    private Handler handler = new Handler();
    private View mListViewFooter;
    private Boolean mIsLoadingMore;
    private ArrayList<ProjectDetailModel> mProjectList = new ArrayList<>();

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

        mIncomingButton = new TopTabButton(getActivity());
        mIncomingButton.imageView = (ImageView)getActivity().findViewById(R.id.function_switch_bottom_button_investment_imageView);
        mIncomingButton.textView = (TextView)getActivity().findViewById(R.id.function_switch_bottom_button_investment_textView);
        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.function_switch_bottom_button_investment);
        linearLayout.setOnClickListener(tabButtonOnClickListener);
        tabButtonOnClickListener.onClick(linearLayout);

        mHotProjectButton = new TopTabButton(getActivity());
        mHotProjectButton.imageView = (ImageView)getActivity().findViewById(R.id.imageView4);
        mHotProjectButton.textView = (TextView)getActivity().findViewById(R.id.textView4);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayout4);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        mSearchButton = new TopTabButton(getActivity());
        mSearchButton.imageView = (ImageView)getActivity().findViewById(R.id.imageView5);
        mSearchButton.textView = (TextView)getActivity().findViewById(R.id.textView5);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayout5);
        linearLayout.setOnClickListener(tabButtonOnClickListener);

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_investment_refreshable_view);
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
        }, PullRefreshConfig.FRAGMENT_HOME_INVESTMENT);

        mGridView = (GridViewWithHeaderAndFooter)getActivity().findViewById(R.id.home_investment_grid_view);
        mListViewFooter = LayoutInflater.from(getActivity()).inflate(R.layout.loading_list_view_item, null, false);
        mIsLoadingMore = false;
        mGridView.addFooterView(mListViewFooter);
        mListViewFooter.setVisibility(View.GONE);
        mGridView.setOnItemClickListener(this);

        mDataArrays = new ArrayList<>();
        mAdapter = new InvestmentListViewAdapter(getActivity(), mProjectList);
        mGridView.setAdapter(mAdapter);



        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (!mIsLoadingMore && view.getLastVisiblePosition() == view.getCount() - 1) {
                        mIsLoadingMore = true;
                        mListViewFooter.setVisibility(View.VISIBLE);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                mIsLoadingMore = false;
                                mListViewFooter.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initData(){


//        ProjectDao projectDao = MyDaoMaster.getDaoSession().getProjectDao();
//        QueryBuilder queryBuilder = projectDao.queryBuilder();
//        queryBuilder.limit(7);
//        List<Project> list = queryBuilder.list();
//        mDataArrays.addAll(list);

        updateInvestmentList();
    }

    private void updateInvestmentList(){
        HttpParams params = new HttpParams();

        HttpClient.post(GetProjectListProtocol.URL_GET_PROJECT_LIST, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d(TAG, "update project list failure-> " + s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ProjectDetailModel> detailModels = jsonToProjectList(s);
                addNewProjectToList(detailModels);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addNewProjectToList(ArrayList<ProjectDetailModel> projectDetailModels){
        for( int i = 0; i < 8; i++ )
        mProjectList.addAll(projectDetailModels);
    }

    private ArrayList<ProjectDetailModel> jsonToProjectList(String json){
        ArrayList<ProjectDetailModel> projectDetailModels = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ProjectDetailModel detailModel = new ProjectDetailModel();
                detailModel.setName( jsonObject.getString("activityName") );
                detailModel.setActivityStageId(jsonObject.getString("activityStageId"));
                detailModel.setActivityId(jsonObject.getString("activityId"));
                detailModel.setActivityIntroduce(jsonObject.getString("activityIntroduce"));
                projectDetailModels.add(detailModel);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return projectDetailModels;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        ProjectDetailModel project = (ProjectDetailModel) mGridView.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), InvestProjectActivity.class);
        intent.putExtra(InvestProjectActivity.EXTRA_PROJECT_ID, project.getActivityId());
        startActivity(intent);
    }

    public class InvestmentListViewAdapter extends BaseAdapter {
        private List<ProjectDetailModel> data;
        private Context context;
        private LayoutInflater mInflater;

        public InvestmentListViewAdapter(Context context, List<ProjectDetailModel> data){
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
            ProjectDetailModel project = (ProjectDetailModel)getItem(position);

            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview_first, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.imageView);
                viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.textView);
                viewHolder.tvDay = (TextView)convertView.findViewById(R.id.textView5);
                viewHolder.tvHour = (TextView)convertView.findViewById(R.id.textView7);
                viewHolder.tvMinute = (TextView)convertView.findViewById(R.id.textView9);
                viewHolder.tvAwarding = (TextView)convertView.findViewById(R.id.textView2);
                viewHolder.tvAwardTarget = (TextView)convertView.findViewById(R.id.textView3);
                viewHolder.tvName = (TextView)convertView.findViewById(R.id.textView53);
                viewHolder.pbProjectProgress = (ProgressBar)convertView.findViewById(R.id.home_investment_list_view_item_pb_progress);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

//            ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();
//            QueryBuilder queryBuilder = projectImageDao.queryBuilder();
//            queryBuilder.where(ProjectImageDao.Properties.ProjectId.eq(project.getId()));
//            ProjectImage projectImage = (ProjectImage)queryBuilder.build().list().get(0);
//            if(projectImage != null){
//                try{
//                    viewHolder.ivLogo.setImageBitmap( MediaStore.Images.Media.getBitmap(context.getContentResolver(),
//                            Uri.parse(projectImage.getImageUrl())));
//                }catch (IOException e){
//
//                }
//            }
            viewHolder.tvDescription.setText(project.getActivityIntroduce());
            viewHolder.tvName.setText(project.getName());

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvDescription;
            TextView tvDay;
            TextView tvHour;
            TextView tvMinute;
            TextView tvAwarding;
            TextView tvAwardTarget;
            TextView tvName;
            ProgressBar pbProjectProgress;
        }
    }
}
