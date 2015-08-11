package com.dragoneye.wjjt.activity.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.InvestProjectActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.dao.Project;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.ProjectDetailModel;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.view.GridViewWithHeaderAndFooter;
import com.dragoneye.wjjt.view.RefreshableView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-8-11.
 */
public class HomeEntrepreneurFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    private RefreshableView refreshableView;
    private GridViewWithHeaderAndFooter mGridView;
    private MyProjectListListViewAdapter mAdapter;
    private View mListViewFooter;
    private Boolean mIsLoadingMore;
    private ArrayList<ProjectDetailModel> mProjectList = new ArrayList<>();

    private Handler handler = new Handler();

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
        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_investment_refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(updateInvestmentList_r);
            }
        }, PreferencesConfig.FRAGMENT_HOME_INVESTMENT);
        refreshableView.setTextColor(Color.WHITE);
        refreshableView.setArrowColor(Color.WHITE);

        mGridView = (GridViewWithHeaderAndFooter)getActivity().findViewById(R.id.home_investment_grid_view);
        mListViewFooter = LayoutInflater.from(getActivity()).inflate(R.layout.loading_list_view_item, null, false);
        mIsLoadingMore = false;
        mGridView.addFooterView(mListViewFooter);
        mListViewFooter.setVisibility(View.GONE);
        mGridView.setOnItemClickListener(this);

        mAdapter = new MyProjectListListViewAdapter(getActivity(), mProjectList);
        mGridView.setAdapter(mAdapter);



        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (!mIsLoadingMore && view.getLastVisiblePosition() == view.getCount() - 1) {
                        mIsLoadingMore = true;
                        mListViewFooter.setVisibility(View.VISIBLE);
                        handler.post(updateInvestmentList_r);
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

        handler.post(updateInvestmentList_r);
    }

    Runnable updateInvestmentList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            HttpClient.atomicPost(getActivity(), GetProjectListProtocol.URL_GET_PROJECT_LIST, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Log.d("", "update project list failure-> " + s);
                    refreshableView.finishRefreshing();
                    if (mIsLoadingMore) {
                        mIsLoadingMore = false;
                        mListViewFooter.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    ArrayList<ProjectDetailModel> detailModels = jsonToProjectList(s);
                    addNewProjectToList(detailModels);
                    mAdapter.notifyDataSetChanged();
                    refreshableView.finishRefreshing();
                    if (mIsLoadingMore) {
                        mIsLoadingMore = false;
                        mListViewFooter.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    private void addNewProjectToList(ArrayList<ProjectDetailModel> projectDetailModels){
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
                detailModel.setSummary(jsonObject.getString("summary"));
                detailModel.setTargetFund(jsonObject.getInt("targetFund"));
                detailModel.setCurrentFund(jsonObject.getInt("currentFund"));
                detailModel.setCurrentStage(jsonObject.getInt("currentStage"));
                detailModel.setTotalStage(jsonObject.getInt("totalStage"));
                detailModel.setImageUrl(jsonObject.getString("imageUrl"));
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
        intent.putExtra(InvestProjectActivity.EXTRA_PROJECT_MODEL, project);
        startActivity(intent);
    }

    public class MyProjectListListViewAdapter extends BaseAdapter {
        private List<ProjectDetailModel> data;
        private Context context;
        private LayoutInflater mInflater;

        public MyProjectListListViewAdapter(Context context, List<ProjectDetailModel> data){
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
        @SuppressWarnings("unchecked")
        public View getView(int position, View convertView, ViewGroup parent){
            ProjectDetailModel project = (ProjectDetailModel)getItem(position);

            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview_first, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.home_investment_list_view_item_iv_logo);
                viewHolder.tvSummary = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_summary);
                viewHolder.tvTargetFundNum = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_currentFund);
                viewHolder.tvTargetFund = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_targetFund);
                viewHolder.tvName = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_projectName);
                viewHolder.pbProjectProgress = (ProgressBar)convertView.findViewById(R.id.home_investment_list_view_item_pb_progress);
                viewHolder.tvStageInfo = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_stageInfo);
                viewHolder.tvProgress = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_progress);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvSummary.setText(project.getSummary());
            viewHolder.tvName.setText(project.getName());

            String strCurrentStage = String.format(getString(R.string.project_list_item_stage_info,
                    (String.valueOf(project.getCurrentStage()) + "/" + project.getTotalStage())));
            viewHolder.tvStageInfo.setText(strCurrentStage);


            String strTargetFund = getString(R.string.project_list_item_target_fund);
            viewHolder.tvTargetFund.setText(strTargetFund);

            viewHolder.tvTargetFundNum.setText(ToolMaster.convertToPriceString(project.getTargetFund()));

            int progress = (int)((float)project.getCurrentFund() / (float)project.getTargetFund() * 100);
            viewHolder.pbProjectProgress.setProgress(progress);

            String strProgress = getString(R.string.project_list_item_progress) + "%" + progress;
            viewHolder.tvProgress.setText(strProgress);

            try{
                JSONArray jsonArray = new JSONArray(project.getImageUrl());
                if( jsonArray.length() > 0 ){
                    String url = jsonArray.getString(0);
                    do{
                        if( viewHolder.ivLogo.getTag() != null &&
                                ((String)viewHolder.ivLogo.getTag()).compareTo(url) == 0 ){
                            break;
                        }
                        ImageLoader.getInstance().displayImage(url, viewHolder.ivLogo);
                        viewHolder.ivLogo.setTag(url);
                    }while (false);
                }else {
                    viewHolder.ivLogo.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                            ((MyApplication) (context.getApplicationContext())).images.get(position % 7)));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }




            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvSummary;
            TextView tvTargetFundNum;
            TextView tvTargetFund;
            TextView tvStageInfo;
            TextView tvProgress;
            TextView tvName;
            ProgressBar pbProjectProgress;
        }
    }
}
