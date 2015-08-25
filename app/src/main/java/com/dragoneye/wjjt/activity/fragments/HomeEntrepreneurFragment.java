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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.EntrepreneurActivity;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.activity.ProjectEditActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.MyProjectModel;
import com.dragoneye.wjjt.protocol.GetMyProjectListProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.dragoneye.wjjt.view.RefreshableView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-8-11.
 */
public class HomeEntrepreneurFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    private RefreshableView refreshableView;
    private ListView mListView;
    private MyProjectListListViewAdapter mAdapter;
    private ArrayList<MyProjectModel> mProjectList = new ArrayList<>();

    private Handler handler = new Handler();

    LoadingMoreFooterProxy mLoadingMoreProxy;
    private int mCurPageIndex;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_entrepreneur, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        setHasOptionsMenu(true);
    }

    private void initView(){
        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_entrepreneur_refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadingMoreProxy.reset();
                mCurPageIndex = -1;
                handler.post(updateInvestmentList_r);
            }
        }, PreferencesConfig.FRAGMENT_HOME_ENTREPRENEUR_RECORD);
        refreshableView.setTextColor(Color.WHITE);
        refreshableView.setArrowColor(Color.WHITE);

        mListView = (ListView)getActivity().findViewById(R.id.home_entrepreneur_list_view);
        mLoadingMoreProxy = new LoadingMoreFooterProxy(getActivity(), mListView);
        mLoadingMoreProxy.setOnLoadingMoreListener(new LoadingMoreFooterProxy.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                handler.post(updateInvestmentList_r);
            }
        });
        mLoadingMoreProxy.reset();
        mListView.setOnItemClickListener(this);
        mListView.setDividerHeight(0);

        mAdapter = new MyProjectListListViewAdapter(getActivity(), mProjectList);
        mListView.setAdapter(mAdapter);
    }

    private void initData(){
        mCurPageIndex = -1;
        refreshableView.doRefreshImmediately();
    }

    Runnable updateInvestmentList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();
            params.put("pageIndex", mCurPageIndex + 1);
            params.put("numPerPage", 10);
            params.put("userId", ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserId());

            HttpClient.atomicPost(getActivity(), GetMyProjectListProtocol.URL_GET_MY_PROJECT_LIST, params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    refreshableView.finishRefreshing();
                    if (mLoadingMoreProxy.isLoadingMore()) {
                        mLoadingMoreProxy.setLoadingFailed();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    refreshableView.finishRefreshing();
                    ArrayList<MyProjectModel> detailModels = jsonToProjectList(s);
                    mCurPageIndex += 1;
                    if ( mLoadingMoreProxy.isLoadingMore() ) {
                        if(detailModels.isEmpty()){
                            mLoadingMoreProxy.finishLoadingMore(true);
                        }else {
                            mProjectList.addAll(detailModels);
                            mAdapter.notifyDataSetChanged();
                            mLoadingMoreProxy.finishLoadingMore(false);
                        }
                    } else {
                        mLoadingMoreProxy.reset();
                        mProjectList.clear();
                        mProjectList.addAll(detailModels);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private ArrayList<MyProjectModel> jsonToProjectList(String json){
        ArrayList<MyProjectModel> projectDetailModels = new ArrayList<>();
        try{
            ArrayList<MyProjectModel> list = ToolMaster.gsonInstance().fromJson(json, new TypeToken<ArrayList<MyProjectModel>>(){}.getType());
            projectDetailModels.addAll(list);
        }catch (Exception e){
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
        MyProjectModel project = (MyProjectModel) mListView.getItemAtPosition(position);

        ArrayList<String> img = new ArrayList<>();
        try{
            img = ToolMaster.gsonInstance().fromJson(project.getImageUrl(), new TypeToken<ArrayList<String>>(){}.getType());
        }catch (Exception e){

        }
        ProjectDetailActivity.CallProjectDetailActivityFullInfo(getActivity(), project.getName(), img, project.getTargetFund(), 0, project.getMarketAnalysis(),
                project.getProfitMode(), project.getTeamIntroduce(), project.getSummary(), project.getAddress(), project.getActivityIntroduce(),
                project.getCreateDate(), project.getCategory());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_entrepreneur, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_entrepreneur_create_project) {
            EntrepreneurActivity.startSubmitProjectActivity(getActivity(), ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class MyProjectListListViewAdapter extends BaseAdapter {
        private List<MyProjectModel> data;
        private Context context;
        private LayoutInflater mInflater;

        public MyProjectListListViewAdapter(Context context, List<MyProjectModel> data){
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
            final MyProjectModel project = (MyProjectModel)getItem(position);

            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview_developer, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.home_developer_listview_iv_logo);
                viewHolder.tvName = (TextView)convertView.findViewById(R.id.home_developer_listview_tv_name);
                viewHolder.tvParam = (TextView)convertView.findViewById(R.id.home_developer_listview_tv_param);
                viewHolder.tvStatus = (TextView)convertView.findViewById(R.id.home_developer_listview_tv_status);
                viewHolder.tvGotoEdit = (TextView)convertView.findViewById(R.id.home_developer_listview_tv_gotoEdit);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvGotoEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectEditActivity.CallActivity(getActivity(), project);
                }
            });

            viewHolder.tvName.setText(project.getName());

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
                            ((MyApplication)(context.getApplicationContext())).images.get(position % 7)));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            viewHolder.tvGotoEdit.setVisibility(View.INVISIBLE);
            viewHolder.tvParam.setVisibility(View.INVISIBLE);
            switch (project.getAuditorStatus()){
                case MyProjectModel.STATUS_FIRST_AUDITING:
                case MyProjectModel.STATUS_REVAMPED:
                    viewHolder.tvStatus.setText(getString(R.string.home_investment_listview_developer_auditing));
                    break;
                case MyProjectModel.STATUS_NEED_REVAMP:
                    viewHolder.tvStatus.setText(getString(R.string.home_investment_listview_developer_notpass));
                    viewHolder.tvGotoEdit.setVisibility(View.VISIBLE);
                    viewHolder.tvParam.setVisibility(View.VISIBLE);
                    viewHolder.tvParam.setText( "驳回原因: " + project.getNoaudireason());
                    break;
                case MyProjectModel.STATUS_AUDITOR_PASS_AND_KEEP:
                    viewHolder.tvStatus.setText(getString(R.string.home_investment_listview_developer_reservedwait));
                    break;
                case MyProjectModel.STATUS_WILL_BE_USE:
                    viewHolder.tvStatus.setText(getString(R.string.home_investment_listview_developer_sign));
                    break;
                case MyProjectModel.STATUS_START_RAISE:
                    viewHolder.tvStatus.setText(getString(R.string.home_investment_listview_developer_fundraising));
                    break;
                case MyProjectModel.STATUS_RAISE_FINISH:
                    viewHolder.tvStatus.setText(getString(R.string.home_investment_listview_developer_end));
                    break;
            }

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvName;
            TextView tvStatus;
            TextView tvParam;
            TextView tvGotoEdit;
        }
    }
}
