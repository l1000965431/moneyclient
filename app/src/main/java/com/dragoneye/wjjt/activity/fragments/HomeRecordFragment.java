package com.dragoneye.wjjt.activity.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.OrderModel;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.view.RefreshableView;
import com.dragoneye.wjjt.view.TopTabButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-6-19.
 */
public class HomeRecordFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TopTabButton mIncomingButton, mInvestmentButton, mCurrentSelectButton;
    private RefreshableView refreshableView;
    private ListView mListView;
    private ArrayList<OrderModel> mInvestedProjects = new ArrayList<>();
    private ArrayList<String> mEarningProjects = new ArrayList<>();
    private InvestmentListViewAdapter mInvestAdapter;
    private EarningProjectListViewAdapter mEarningAdapter;
    private Handler handler = new Handler();

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
                        updateInvestProjectList();
                        break;
                    case R.id.home_record_top_tab_ll_investment:
                        mInvestmentButton.setChecked(true);
                        mCurrentSelectButton = mInvestmentButton;
                        updateEarningProjectList();
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

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_record_refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                if( mListView.getAdapter() instanceof InvestmentListViewAdapter){
                    handler.post(onUpdateOrderList_r);
                }else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshableView.finishRefreshing();
                        }
                    }, 2000);
                }
            }
        }, PreferencesConfig.FRAGMENT_HOME_RECORD);

        mListView = (ListView)getActivity().findViewById(R.id.home_record_list_view);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);

        mInvestAdapter = new InvestmentListViewAdapter(getActivity(), mInvestedProjects);
        mEarningAdapter = new EarningProjectListViewAdapter(getActivity(), mEarningProjects);

        tabButtonOnClickListener.onClick(linearLayout);
    }

    private void initData(){
//        InvestedProjectDao investedProjectDao = MyDaoMaster.getDaoSession().getInvestedProjectDao();
//        mInvestedProjects.addAll(investedProjectDao.loadAll());




    }

    private void updateInvestProjectList(){
        mListView.setAdapter(mInvestAdapter);
        mInvestAdapter.notifyDataSetChanged();
        handler.post(onUpdateOrderList_r);
    }

    private void updateEarningProjectList(){
        mListView.setAdapter(mEarningAdapter);
        mEarningAdapter.notifyDataSetChanged();
    }

    Runnable onUpdateOrderList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();

            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_USER_ID, ((MyApplication)getActivity().getApplication()).getCurrentUser().getUserId());
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_PAGE_INDEX, 0);
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_TOKEN, ((MyApplication)getActivity().getApplication()).getToken());
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_NUM_PER_PAGE, 10);

            HttpClient.atomicPost(getActivity(), GetProjectListProtocol.URL_GET_ORDER_LIST, httpParams, new HttpClient.MyHttpHandler() {
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    refreshableView.finishRefreshing();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    refreshableView.finishRefreshing();
                    if (s == null) {
                        UIHelper.toast(getActivity(), "服务器繁忙");
                        return;
                    }
                    onUpdateOrderResult(s);
                }
            });
        }
    };

    private void onUpdateOrderResult(String result){
        if( result.isEmpty() ){

        }else {
            onUpdateOrderSuccess(result);
        }
    }

    private void onUpdateOrderSuccess(String response){
        ArrayList<OrderModel> orderModels = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                OrderModel orderModel = new OrderModel();
                orderModel.setOrderState(object.getInt("orderState"));
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

        mInvestedProjects.clear();
        mInvestedProjects.addAll(orderModels);
        mInvestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
        startActivity(intent);
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
                viewHolder.tvInvestAmount = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_amount);
                viewHolder.tvInvestPriceNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_price_num);
                viewHolder.tvInvestStageNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_stage_num);
                viewHolder.tvEarningProportion = (TextView)convertView.findViewById(R.id.home_record_listview_tv_earning_proportion);
                viewHolder.tvEarningProportion.setOnClickListener(HomeRecordFragment.this);
                viewHolder.tvTargetFund = (TextView)convertView.findViewById(R.id.home_record_listview_tv_targetFund);

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
                String url = jsonArray.getString(0);
                ImageLoader.getInstance().displayImage(url, viewHolder.ivLogo);
            }catch (Exception e){

            }

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

        }
    }

    public class EarningProjectListViewAdapter extends BaseAdapter {
        private List<String> data;
        private Context context;
        private LayoutInflater mInflater;

        public EarningProjectListViewAdapter(Context context, List<String> data){
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
                viewHolder.tvInvestAmount = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_amount);
                viewHolder.tvInvestPriceNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_price_num);
                viewHolder.tvInvestStageNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_invest_stage_num);
                viewHolder.tvEarningProportion = (TextView)convertView.findViewById(R.id.home_record_listview_tv_earning_proportion);
                viewHolder.tvEarningProportion.setOnClickListener(HomeRecordFragment.this);
                viewHolder.tvTargetFund = (TextView)convertView.findViewById(R.id.home_record_listview_tv_targetFund);

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
                String url = jsonArray.getString(0);
                ImageLoader.getInstance().displayImage(url, viewHolder.ivLogo);
            }catch (Exception e){

            }

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

        }
    }
}
