package com.dragoneye.wjjt.activity.fragments.record;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.dao.InvestRecord;
import com.dragoneye.wjjt.dao.InvestRecordDao;
import com.dragoneye.wjjt.dao.MyDaoMaster;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.EarningModel;
import com.dragoneye.wjjt.model.OrderModel;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.protocol.InvestProjectProtocol;
import com.dragoneye.wjjt.tool.PreferencesHelper;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.dragoneye.wjjt.view.RefreshableView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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

    ProgressDialog progressDialog;

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
                        PreferencesHelper.setIsHaveNewMessage(getActivity(), false, PreferencesConfig.IS_HAVE_NEW_INVEST_MESSAGE);
                        getTopButton().setIsHaveNew(false);
                        mLoadingMoreProxy.reset();
                        mCurInvestRecordPageIndex = -1;
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
        mListView.setDividerHeight(0);

        mListView.setOnItemClickListener(this);
        mAdapter = new InvestmentListViewAdapter(getActivity(), mInvestedProjects);
        mListView.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            doRefresh();
        }else {
            setNewRead();
        }
    }

    @Override
    public void onShow(){
        doRefresh();
    }

    @Override
    public void onHide(){
        setNewRead();
    }

    private void doRefresh(){
        if( refreshableView != null ){
            refreshableView.doRefreshImmediately();
        }
    }

    private void setNewRead(){
        if(refreshableView != null){
            InvestRecordDao dao = MyDaoMaster.getDaoSession().getInvestRecordDao();

            QueryBuilder queryBuilder = dao.queryBuilder();
            queryBuilder.where(InvestRecordDao.Properties.IsRead.eq(false));

            List<InvestRecord> unReadList = queryBuilder.list();
            for(InvestRecord earningRecord : unReadList){
                earningRecord.setIsRead(true);
            }
            dao.updateInTx(unReadList);
        }
    }

    @Override
    public void onClick(View v){
        OrderModel orderModel;
        switch (v.getId()){
            case R.id.home_record_listview_tv_earning_proportion:
            case R.id.home_record_invest_listview_ll_proportion:
                orderModel = (OrderModel)v.getTag();
                onShowProportion(orderModel);
                break;
        }
    }

    private void onShowProportion(final OrderModel orderModel){
        if( orderModel.getProportionInfo() != null ){
            showProportionDialog(orderModel.getProportionInfo());
            return;
        }

        progressDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put(InvestProjectProtocol.GET_INVEST_INFO_PARAM_ACTIVITY_STAGE_ID, orderModel.getActivityStageId());

        HttpClient.atomicPost(getActivity(), InvestProjectProtocol.URL_GET_INVEST_INFO,
                httpParams, new HttpClient.MyHttpHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable){
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onSuccess(int i1, Header[] headers, String s) {
                        progressDialog.dismiss();
                        String response = HttpClient.getValueFromHeader(headers, "response");
                        if( response == null || s == null ){
                            return;
                        }
                        switch (response){
                            case InvestProjectProtocol.GET_INVEST_INFO_SUCCESS:
                                try{
                                    JSONObject object = new JSONObject(s);
                                    int leadInvestPrice = object.getInt("TotalLinePeoples");
                                    int fallowInvestPrice = object.getInt("TotalLines");;
                                    JSONArray srEarnings = object.getJSONArray("SREarning");
                                    if(orderModel.getPurchaseType() == 2){
                                        ArrayList<EarningModel> srEarningModels = new ArrayList<>();
                                        for(int i = 0; i < srEarnings.length(); i++){
                                            JSONObject object1 = srEarnings.getJSONObject(i);
                                            int earningType = object1.getInt("srEarningType");
                                            if( earningType == 2 ){
                                                EarningModel earningModel = new EarningModel();
                                                earningModel.setNum( srEarnings.getJSONObject(i).getInt("srEarningNum"));
                                                earningModel.setPrice(srEarnings.getJSONObject(i).getInt("srEarningPrice"));
                                                srEarningModels.add(earningModel);
                                            }
                                        }
                                        Collections.sort(srEarningModels, new Comparator<EarningModel>() {
                                            @Override
                                            public int compare(EarningModel lhs, EarningModel rhs) {
                                                return lhs.getPrice() > rhs.getPrice() ? -1 : 0;
                                            }
                                        });
                                        showSrProportion(orderModel, srEarningModels, fallowInvestPrice);

                                    }else if(orderModel.getPurchaseType() == 1){
                                        JSONArray brEarnings = object.getJSONArray("EarningPeoples");
                                        ArrayList<EarningModel> brEarningModels = new ArrayList<>();
                                        for( int i = 0; i < brEarnings.length(); i++ ){
                                            JSONObject object1 = brEarnings.getJSONObject(i);
                                            EarningModel earningModel = new EarningModel();
                                            earningModel.setNum(brEarnings.getJSONObject(i).getInt("num"));
                                            earningModel.setPrice(brEarnings.getJSONObject(i).getInt("earningPrice"));
                                            brEarningModels.add(earningModel);

                                        }
                                        showBrProportion(orderModel, brEarningModels);
                                    }


                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case InvestProjectProtocol.GET_INVEST_INFO_FAILED:
                                break;
                        }
                    }
                });
    }

    private void showSrProportion(OrderModel orderModel, ArrayList<EarningModel> models, int fallowInvestPrice){
        String proportionStr = "";
        for(EarningModel model : models){
            float proportion = (float)orderModel.getPurchaseNum() / fallowInvestPrice * model.getNum() * 100;
            String text = getProportionString(proportion, model.getPrice());
            proportionStr += (text + "\n");
        }
        orderModel.setProportionInfo(proportionStr);
        showProportionDialog(proportionStr);
    }

    private void showBrProportion(OrderModel orderModel, ArrayList<EarningModel> models){
        String proportionStr = "";
        for(EarningModel model : models){
            float proportion = 1.0f / models.size() * 100;
            String text = getProportionString(proportion, model.getPrice());
            proportionStr += (text + "\n");
        }
        orderModel.setProportionInfo(proportionStr);
        showProportionDialog(proportionStr);
    }

    private void showProportionDialog(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(text);
        alertDialog.show();
    }

    private String getProportionString(float proportion, int price){
        if( proportion > 99.99f ){
            proportion = 99.99f;
        }
        if( proportion == 0 ){
            proportion = 0.1f;
        }

        if( proportion < 1.0f ){
            int ip = (int)(100 / proportion);
            return String.format("1/%d几率获得%s元收益", ip, price);
        }else {
            return String.format("%.2f%%几率获得%s元收益", proportion, price);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if( position >= mInvestedProjects.size() ){
            return;
        }

    }

    Runnable onUpdateOrderList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams httpParams = new HttpParams();

            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_USER_ID, ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserId());
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_PAGE_INDEX, mCurInvestRecordPageIndex + 1);
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_TOKEN, ((MyApplication)getActivity().getApplication()).getToken(getActivity()));
            httpParams.put(GetProjectListProtocol.GET_ORDER_PARAM_NUM_PER_PAGE, 10);
            httpParams.put("token", ((MyApplication)getActivity().getApplication()).getToken(getActivity()));

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
                    if(s.compareTo("LANDFAILED") == 0){
                        ((MyApplication) getActivity().getApplication()).reLogin(getActivity());
                        return;
                    }

                    ArrayList<OrderModel> orderModels = onUpdateOrderSuccess(s);
                    addToRecord(orderModels);
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
                orderModel.setOrderStartAdvance(object.getInt("OrderStartAndvance"));
                orderModel.setOrderLines(object.getInt("orderLines"));
                orderModel.setVirtualSecurities(object.getInt("VirtualSecurities"));
                orderModel.setPurchaseType(object.getInt("purchaseType"));
                orderModel.setId(object.getString("Id"));
                String dateString = object.getString("orderDate");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try{
                    orderModel.setOrderDate(sdf.parse(dateString));
                }catch (ParseException e){
                    e.printStackTrace();
                }
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

    private void addToRecord(List<OrderModel> list){
        ArrayList<InvestRecord> records = new ArrayList<>();
        for(OrderModel orderModel : list){
            InvestRecord earningRecord = new InvestRecord();
            earningRecord.setId(orderModel.getId());
            earningRecord.setIsRead(false);
            records.add(earningRecord);
        }

        ArrayList<InvestRecord> recordsToInsert = new ArrayList<>();
        InvestRecordDao dao = MyDaoMaster.getDaoSession().getInvestRecordDao();
        for(InvestRecord investRecord : records){
            if(dao.load(investRecord.getId()) == null){
                recordsToInsert.add(investRecord);
            }
        }

        dao.insertInTx(recordsToInsert);
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
            final OrderModel orderModel = (OrderModel)getItem(position);


            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.imageView);
                viewHolder.tvProjectName = (TextView)convertView.findViewById(R.id.home_record_listview_tv_project_name);
                viewHolder.tvInvestAmount = (TextView)convertView.findViewById(R.id.home_record_earning_tv_investPrice);
                viewHolder.tvInvestStageNum = (TextView)convertView.findViewById(R.id.home_record_listview_tv_investStageNum);
                viewHolder.tvInvestTotalPrice = (TextView)convertView.findViewById(R.id.home_record_listview_tv_totalPrice);
                viewHolder.tvEarningProportion = (TextView)convertView.findViewById(R.id.home_record_listview_tv_earning_proportion);
                viewHolder.tvEarningProportion.setOnClickListener(InvestRecordFragment.this);
                viewHolder.tvTargetFund = (TextView)convertView.findViewById(R.id.home_record_listview_tv_targetFund);
                viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.invest_record_list_view_item_progressbar);
                viewHolder.tvProgress = (TextView)convertView.findViewById(R.id.invest_record_list_view_tv_progress);
                viewHolder.tvOrderDate = (TextView)convertView.findViewById(R.id.home_record_listview_tv_orderDate);
                viewHolder.tvStageInfo = (TextView)convertView.findViewById(R.id.home_record_listview_tv_stageInfo);
                viewHolder.tvStatus = (TextView)convertView.findViewById(R.id.home_record_listview_tv_status);
                viewHolder.llProportion = (LinearLayout)convertView.findViewById(R.id.home_record_invest_listview_ll_proportion);
                viewHolder.llProportion.setOnClickListener(InvestRecordFragment.this);
                viewHolder.tvNew = (TextView)convertView.findViewById(R.id.home_record_listview_tv_new);
                viewHolder.vLLTop = convertView.findViewById(R.id.honm_record_invest_ll_top);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvProjectName.setText(orderModel.getActivityName());

            String strCurrentStage = String.format("第%s期",
                    (String.valueOf(orderModel.getCurrentStage()) + "/" + orderModel.getTotalStage()));
            viewHolder.tvStageInfo.setText(strCurrentStage);
            viewHolder.tvStatus.setText(orderModel.getStatusString());

            if(orderModel.getPurchaseType() == 1){
                viewHolder.tvInvestAmount.setText(String.format("您已领投：%s", ToolMaster.convertToPriceString(orderModel.getPurchaseNum())));
            }else {
                viewHolder.tvInvestAmount.setText(String.format("您已跟投：%s", ToolMaster.convertToPriceString(orderModel.getPurchaseNum())));
            }

            if( orderModel.getAdvanceNum() > 1 ){
                viewHolder.tvInvestStageNum.setText(String.format("购买期数：%d - %d", orderModel.getOrderStartAdvance(),
                        orderModel.getOrderStartAdvance() + orderModel.getAdvanceNum() - 1));
            }else {
                viewHolder.tvInvestStageNum.setText("购买期数：1");
            }

            viewHolder.tvInvestTotalPrice.setText(String.format("总计：%s", ToolMaster.convertToPriceString(orderModel.getOrderLines() + orderModel.getVirtualSecurities())));
            viewHolder.tvEarningProportion.setTag(orderModel);
            viewHolder.llProportion.setTag(orderModel);
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
            viewHolder.vLLTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> img = new ArrayList<>();
                    try {
                        img = ToolMaster.gsonInstance().fromJson(orderModel.getImageUrl(),
                                new TypeToken<ArrayList<String>>() {
                                }.getType());
                    } catch (Exception e) {

                    }
                    ProjectDetailActivity.CallProjectDetailActivity(getActivity(), orderModel.getActivityId(), orderModel.getActivityName(), img,
                            orderModel.getTargetFund(), orderModel.getCurrentFund());
                }
            });

            int progress = (int)((float)orderModel.getCurrentFund() / orderModel.getTargetFund() * 100 + 0.5f);
            viewHolder.progressBar.setProgress(progress);
            viewHolder.tvProgress.setText(String.format("筹款进度: %d%%", progress));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            viewHolder.tvOrderDate.setText(sdf.format(orderModel.getOrderDate()));

            InvestRecordDao dao = MyDaoMaster.getDaoSession().getInvestRecordDao();
            InvestRecord investRecord = dao.load(orderModel.getId());
            if(investRecord != null){
                viewHolder.tvNew.setVisibility( investRecord.getIsRead() ? View.GONE : View.VISIBLE);
            }

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvProjectName;
            TextView tvInvestAmount;
            TextView tvInvestStageNum;
            TextView tvInvestTotalPrice;
            TextView tvEarningProportion;
            TextView tvTargetFund;
            TextView tvStageInfo;
            TextView tvStatus;
            TextView tvNew;
            ProgressBar progressBar;
            TextView tvProgress;
            TextView tvOrderDate;
            LinearLayout llProportion;
            View vLLTop;
        }
    }
}
