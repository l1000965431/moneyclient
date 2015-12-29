package com.dragoneye.wjjt.activity.fragments.invest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.InvestProjectActivity;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.dao.MyDaoMaster;
import com.dragoneye.wjjt.dao.NewNormalActivity;
import com.dragoneye.wjjt.dao.NewNormalActivityDao;
import com.dragoneye.wjjt.dao.NewPreferentialActivityDao;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.ProjectDetailModel;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.tool.PreferencesHelper;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.view.GridViewWithHeaderAndFooter;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.dragoneye.wjjt.view.RefreshableView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by happysky on 15-10-13.
 */
public class NormalInvestFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RefreshableView refreshableView;
    private GridViewWithHeaderAndFooter mGridView;
    private InvestmentListViewAdapter mAdapter;

    private ArrayList<ProjectDetailModel> mProjectList = new ArrayList<>();
    private int mCurPageIndex;

    private Handler handler = new Handler(Looper.getMainLooper());

    LoadingMoreFooterProxy mLoadingMoreProxy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_invest_normal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_invest_normal_refreshableView);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        PreferencesHelper.setIsHaveNewMessage(getActivity(), false, PreferencesConfig.IS_HAVE_NEW_NORMAL_ACTIVITY);
                        if(getTopButton() != null){
                            getTopButton().setIsHaveNew(false);
                        }
                        mLoadingMoreProxy.reset();
                        mCurPageIndex = -1;
                        handler.post(updateInvestmentList_r);
                    }
                });
            }
        }, PreferencesConfig.FRAGMENT_HOME_INVESTMENT_NORMAL);
        refreshableView.setTextColor(Color.WHITE);
        refreshableView.setArrowColor(Color.WHITE);

        mGridView = (GridViewWithHeaderAndFooter)getActivity().findViewById(R.id.home_investment_normal_grid_view);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mLoadingMoreProxy = new LoadingMoreFooterProxy(getActivity(), mGridView);
        mLoadingMoreProxy.setOnLoadingMoreListener(new LoadingMoreFooterProxy.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                handler.post(updateInvestmentList_r);
            }
        });
        mLoadingMoreProxy.reset();
        mGridView.setOnItemClickListener(this);

        mAdapter = new InvestmentListViewAdapter(getActivity(), mProjectList);
        mGridView.setAdapter(mAdapter);

        mCurPageIndex = -1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshableView.doRefreshImmediately();
            }
        }, 100);
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if( refreshableView != null ){
                refreshableView.doRefreshImmediately();
            }
        }else {
            setNewRead();
        }
    }

    @Override
    public void onShow(){
        if( refreshableView != null ){
            refreshableView.doRefreshImmediately();
        }
    }

    @Override
    public void onHide(){
        setNewRead();
    }

    private void setNewRead(){
        if(refreshableView != null){
            NewNormalActivityDao dao = MyDaoMaster.getDaoSession().getNewNormalActivityDao();

            QueryBuilder queryBuilder = dao.queryBuilder();
            queryBuilder.where(NewNormalActivityDao.Properties.IsRead.eq(false));

            List<NewNormalActivity> unReadList = queryBuilder.list();
            for(NewNormalActivity newNormalActivity : unReadList){
                newNormalActivity.setIsRead(true);
            }
            dao.updateInTx(unReadList);
        }
    }

    Runnable updateInvestmentList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();
            params.put("userId", ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserId());
            params.put("pageIndex", mCurPageIndex + 1);
            params.put("numPerPage", 10);

            HttpClient.atomicPost(getActivity(), GetProjectListProtocol.URL_GET_PROJECT_LIST, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onPosting() {
                    refreshableView.finishRefreshing(RefreshableView.REFRESH_RESULT_SUCCESS);
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    refreshableView.finishRefreshing(RefreshableView.REFRESH_RESULT_FAILED);
                    if (mLoadingMoreProxy.isLoadingMore()) {
                        mLoadingMoreProxy.setLoadingFailed();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    ArrayList<ProjectDetailModel> detailModels = jsonToProjectList(s);
                    addToRecord(detailModels);
                    mCurPageIndex += 1;
                    if (mLoadingMoreProxy.isLoadingMore()) {
                        loadMoreProjectToList(detailModels);
                    } else {
                        reloadProjectList(detailModels);
                    }
                    refreshableView.finishRefreshing(mProjectList.size() > 0 ? RefreshableView.REFRESH_RESULT_SUCCESS : RefreshableView.REFRESH_RESULT_NO_CONTENT);
                }
            });
        }
    };

    private void addToRecord(List<ProjectDetailModel> list){
        ArrayList<NewNormalActivity> records = new ArrayList<>();
        for(ProjectDetailModel projectDetailModel : list){
            NewNormalActivity newNormalActivity = new NewNormalActivity();
            newNormalActivity.setId(projectDetailModel.getActivityId());
            newNormalActivity.setIsRead(false);
            records.add(newNormalActivity);
        }

        ArrayList<NewNormalActivity> recordsToInsert = new ArrayList<>();
        NewNormalActivityDao dao = MyDaoMaster.getDaoSession().getNewNormalActivityDao();
        for(NewNormalActivity earningRecord : records){
            if(dao.load(earningRecord.getId()) == null){
                recordsToInsert.add(earningRecord);
            }
        }

        dao.insertInTx(recordsToInsert);
    }

    private void loadMoreProjectToList(ArrayList<ProjectDetailModel> projectDetailModels){
        if(projectDetailModels.isEmpty()){
            mLoadingMoreProxy.finishLoadingMore(true);
        }else {
            mProjectList.addAll(projectDetailModels);
            mAdapter.notifyDataSetChanged();
            mLoadingMoreProxy.finishLoadingMore(false);
        }
    }

    private void reloadProjectList(ArrayList<ProjectDetailModel> projectDetailModels){
        mLoadingMoreProxy.reset();
        mProjectList.clear();
        mProjectList.addAll(projectDetailModels);
        mAdapter.notifyDataSetChanged();
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
        if( position >= mProjectList.size() ){
            return;
        }
        ProjectDetailModel project = (ProjectDetailModel) mGridView.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), InvestProjectActivity.class);
        intent.putExtra(InvestProjectActivity.EXTRA_PROJECT_MODEL, project);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                refreshableView.doRefreshImmediately();
            }
        }
    }


    private class InvestmentListViewAdapter extends BaseAdapter {
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
                viewHolder.tvNew = (TextView)convertView.findViewById(R.id.home_investment_list_view_item_tv_new);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            String summary = project.getSummary();
            if( summary.length() > 20 ){
                summary = summary.substring(0, 20) + "...";
            }
            viewHolder.tvSummary.setText(summary);
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

            NewNormalActivityDao dao = MyDaoMaster.getDaoSession().getNewNormalActivityDao();
            NewNormalActivity normalActivity = dao.load(project.getActivityId());
            if(normalActivity != null){
                viewHolder.tvNew.setVisibility( normalActivity.getIsRead() ? View.GONE : View.VISIBLE);
            }


            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvNew;
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
