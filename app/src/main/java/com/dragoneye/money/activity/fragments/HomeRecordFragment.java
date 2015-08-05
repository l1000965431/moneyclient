package com.dragoneye.money.activity.fragments;

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

import com.dragoneye.money.R;
import com.dragoneye.money.activity.InvestProjectActivity;
import com.dragoneye.money.activity.ProjectDetailActivity;
import com.dragoneye.money.config.PreferencesConfig;
import com.dragoneye.money.dao.Project;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.http.HttpParams;
import com.dragoneye.money.model.OrderModel;
import com.dragoneye.money.protocol.GetProjectListProtocol;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.user.CurrentUser;
import com.dragoneye.money.view.RefreshableView;
import com.dragoneye.money.view.TopTabButton;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by happysky on 15-6-19.
 */
public class HomeRecordFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TopTabButton mIncomingButton, mInvestmentButton, mCurrentSelectButton;
    private RefreshableView refreshableView;
    private ListView mListView;
    private HashMap<Long, Project> mProjects = new HashMap<>();
    private ArrayList<OrderModel> mInvestedProjects = new ArrayList<>();
    private InvestmentListViewAdapter mAdapter;
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
                        break;
                    case R.id.home_record_top_tab_ll_investment:
                        mInvestmentButton.setChecked(true);
                        mCurrentSelectButton = mInvestmentButton;
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

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_record_refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(onUpdateOrderList_r);
            }
        }, PreferencesConfig.FRAGMENT_HOME_RECORD);

        mListView = (ListView)getActivity().findViewById(R.id.home_record_list_view);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);

        mInvestedProjects = new ArrayList<>();
        mAdapter = new InvestmentListViewAdapter(getActivity(), mInvestedProjects);
        mListView.setAdapter(mAdapter);
    }

    private void initData(){
//        InvestedProjectDao investedProjectDao = MyDaoMaster.getDaoSession().getInvestedProjectDao();
//        mInvestedProjects.addAll(investedProjectDao.loadAll());



        mAdapter.notifyDataSetChanged();
        handler.post(onUpdateOrderList_r);
    }

    Runnable onUpdateOrderList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();

            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_USER_ID, CurrentUser.getCurrentUser().getUserId());
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_FIRST_PAGE, 0);
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_TOKEN, CurrentUser.getToken());

            HttpClient.atomicPost(getActivity(), GetProjectListProtocol.URL_GET_ORDER_LIST, httpParams, new HttpClient.MyHttpHandler() {
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    UIHelper.toast(getActivity(), "网络异常");
                    refreshableView.finishRefreshing();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (s == null) {
                        UIHelper.toast(getActivity(), "服务器异常");
                        refreshableView.finishRefreshing();
                        return;
                    }
                    onUpdateOrderResult(HttpClient.getValueFromHeader(headers, GetProjectListProtocol.GET_ORDER_RESULT_KEY), s);
                }
            });
        }
    };

    private void onUpdateOrderResult(String result, String response){
        switch (result){
            case GetProjectListProtocol.GET_ORDER_RESULT_SUCCESS:
                UIHelper.toast(getActivity(), "更新成功");
                onUpdateOrderSuccess(response);
                refreshableView.finishRefreshing();
                break;
            case GetProjectListProtocol.GET_ORDER_RESULT_NEED_LOGIN:
                UIHelper.toast(getActivity(), "需要登录");
                refreshableView.finishRefreshing();
                break;
            default:
                break;
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
                orderModel.setProjectName(object.getString("ActivityName"));
                orderModel.setProjectStatus(object.getInt("ActivityStatus"));
                orderModels.add(orderModel);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        mInvestedProjects.addAll(orderModels);
        mAdapter.notifyDataSetChanged();
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

    public class InvestmentListViewAdapter extends BaseAdapter {
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

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvProjectName.setText(orderModel.getProjectName());
            viewHolder.tvInvestAmount.setText(String.format(getString(R.string.invest_project_invested_price), orderModel.getOrderLines()));
            viewHolder.tvInvestPriceNum.setText(String.format(getString(R.string.invest_project_invested_quantity), orderModel.getPurchaseNum()));
            viewHolder.tvInvestStageNum.setText(String.format(getString(R.string.invest_project_invested_installments), orderModel.getAdvanceNum()));
            viewHolder.tvEarningProportion.setTag(orderModel);


            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvProjectName;
            TextView tvInvestAmount;
            TextView tvInvestPriceNum;
            TextView tvInvestStageNum;
            TextView tvEarningProportion;

        }
    }
}
