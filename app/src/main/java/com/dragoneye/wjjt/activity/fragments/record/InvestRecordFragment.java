package com.dragoneye.wjjt.activity.fragments.record;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.InvestProjectActivity;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.OrderModel;
import com.dragoneye.wjjt.model.ProjectDetailModel;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.dragoneye.wjjt.view.RefreshableView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-8-17.
 * 收益记录
 */
public class InvestRecordFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private RefreshableView refreshableView;
    private ListView mListView;
    private LoadingMoreFooterProxy mLoadingMoreProxy;
    private Handler handler = new Handler();
    private int mCurInvestRecordPageIndex = -1;
    private ArrayList<OrderModel> mInvestedProjects = new ArrayList<>();
    private InvestmentListViewAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_record_invest, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_record_invest_refreshableView);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingMoreProxy.reset();
                        mCurInvestRecordPageIndex = -1;
                        mInvestedProjects.clear();
                        handler.post(onUpdateOrderList_r);
                    }
                });
            }
        }, PreferencesConfig.FRAGMENT_HOME_RECORD_INVEST);
        refreshableView.setTextColor(Color.WHITE);
        refreshableView.setArrowColor(Color.WHITE);


        mListView = (ListView)getActivity().findViewById(R.id.home_record_invest_listView);
        mLoadingMoreProxy = new LoadingMoreFooterProxy(getActivity(), mListView);
        mLoadingMoreProxy.setOnLoadingMoreListener(new LoadingMoreFooterProxy.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                handler.post(onUpdateOrderList_r);
            }
        });
        mLoadingMoreProxy.reset();

        mListView.setOnItemClickListener(this);
        mAdapter = new InvestmentListViewAdapter(getActivity(), mInvestedProjects);
        mListView.setAdapter(mAdapter);

        handler.post(onUpdateOrderList_r);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.home_record_listview_tv_earning_proportion:
                OrderModel orderModel = (OrderModel)v.getTag();
                onShowProportion(orderModel);
                break;
        }
    }

    private void onShowProportion(OrderModel orderModel){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage("fdasfksa\nfdafasdf\nfdasfdasf\nfdasfsadfasdf");
        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if( position >= mInvestedProjects.size() ){
            return;
        }
        OrderModel orderModel = mInvestedProjects.get(position);
        ArrayList<String> img = new ArrayList<>();
        try{
            img = ToolMaster.gsonInstance().fromJson(orderModel.getImageUrl(),
                    new TypeToken<ArrayList<String>>(){}.getType());
        }catch (Exception e){

        }
        ProjectDetailActivity.CallProjectDetailActivity(getActivity(), orderModel.getActivityId(), img,
                orderModel.getTargetFund(), orderModel.getCurrentFund());
    }

    Runnable onUpdateOrderList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();

            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_USER_ID, ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserId());
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_PAGE_INDEX, mCurInvestRecordPageIndex + 1);
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_TOKEN, ((MyApplication)getActivity().getApplication()).getToken(getActivity()));
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_NUM_PER_PAGE, 10);

            HttpClient.atomicPost(getActivity(), GetProjectListProtocol.URL_GET_ORDER_LIST, httpParams, new HttpClient.MyHttpHandler() {
                @Override
                public void onPosting() {
                    refreshableView.finishRefreshing();
                }

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

                    ArrayList<OrderModel> orderModels = onUpdateOrderSuccess(s);
                    mCurInvestRecordPageIndex += 1;

                    if (mLoadingMoreProxy.isLoadingMore()) {
                        if (orderModels.isEmpty()) {
                            mLoadingMoreProxy.finishLoadingMore(true);
                        } else {
                            mInvestedProjects.addAll(orderModels);
                            mAdapter.notifyDataSetChanged();
                            mLoadingMoreProxy.finishLoadingMore(false);
                        }
                    } else {
                        mLoadingMoreProxy.reset();
                        mInvestedProjects.clear();
                        mInvestedProjects.addAll(orderModels);
                        mAdapter.notifyDataSetChanged();
                    }

                }
            });
        }
    };

    private ArrayList<OrderModel> onUpdateOrderSuccess(String response){
        ArrayList<OrderModel> orderModels = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                OrderModel orderModel = new OrderModel();
                orderModel.setPurchaseNum(object.getInt("PurchaseNum"));
                orderModel.setAdvanceNum(object.getInt("AdvanceNum"));
                orderModel.setOrderLines(object.getInt("orderLines"));
                JSONObject detail = object.getJSONObject("activityDetailModel");
                orderModel.setActivityStageId(detail.getString("activityStageId"));
                orderModel.setActivityId(detail.getString("activityId"));
                orderModel.setActivityName(detail.getString("activityName"));
                orderModel.setSummary(detail.getString("summary"));
                orderModel.setTargetFund(detail.getInt("targetFund"));
                orderModel.setStatus(detail.getInt("status"));
                orderModel.setImageUrl(detail.getString("imageUrl"));
                orderModel.setCurrentFund(detail.getInt("currentFund"));
                orderModel.setCurrentStage(detail.getInt("currentStage"));
                orderModel.setTotalStage(detail.getInt("totalStage"));

                orderModels.add(orderModel);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return orderModels;
    }

    private class InvestmentListViewAdapter extends BaseAdapter {
        private List<OrderModel> data;
        private Context context;
        private LayoutInflater mInflater;

        public InvestmentListViewAdapter(Context context, List<OrderModel> data){
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
            OrderModel orderModel = (OrderModel)getItem(position);


            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.imageView);
                viewHolder.tvProjectName = (TextView)convertView.findViewById(R.id.home_record_listview_tv_project_name);
                viewHolder.tvInvestAmount = (TextView)convertView.findViewById(R.id.home_record_earning_tv_investPrice);
                viewHolder.tvInvestPriceNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_price_num);
                viewHolder.tvInvestStageNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_stage_num);
                viewHolder.tvEarningProportion = (TextView)convertView.findViewById(R.id.home_record_listview_tv_earning_proportion);
                viewHolder.tvEarningProportion.setOnClickListener(InvestRecordFragment.this);
                viewHolder.tvTargetFund = (TextView)convertView.findViewById(R.id.home_record_listview_tv_targetFund);
                viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.invest_record_list_view_item_progressbar);
                viewHolder.tvProgress = (TextView)convertView.findViewById(R.id.invest_record_list_view_tv_progress);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvProjectName.setText(orderModel.getActivityName());
            viewHolder.tvInvestAmount.setText(String.format(getString(R.string.invest_project_invested_price),
                    ToolMaster.convertToPriceString(orderModel.getOrderLines())));
            viewHolder.tvInvestPriceNum.setText(String.format(getString(R.string.invest_project_invested_quantity), orderModel.getPurchaseNum()));
            viewHolder.tvInvestStageNum.setText(String.format(getString(R.string.invest_project_invested_installments), orderModel.getAdvanceNum()));
            viewHolder.tvEarningProportion.setTag(orderModel);
            viewHolder.tvTargetFund.setText(ToolMaster.convertToPriceString(orderModel.getTargetFund()));

            try{
                JSONArray jsonArray = new JSONArray(orderModel.getImageUrl());
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

            int progress = (int)((float)orderModel.getCurrentFund() / orderModel.getTargetFund() * 100 + 0.5f);
            viewHolder.progressBar.setProgress(progress);
            viewHolder.tvProgress.setText(String.format("筹款进度: %d%%", progress));

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvProjectName;
            TextView tvInvestAmount;
            TextView tvInvestPriceNum;
            TextView tvInvestStageNum;
            TextView tvEarningProportion;
            TextView tvTargetFund;
            ProgressBar progressBar;
            TextView tvProgress;
        }
    }
}
