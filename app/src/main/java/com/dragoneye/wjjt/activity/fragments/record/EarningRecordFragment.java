package com.dragoneye.wjjt.activity.fragments.record;

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
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.dao.DaoMaster;
import com.dragoneye.wjjt.dao.EarningRecord;
import com.dragoneye.wjjt.dao.EarningRecordDao;
import com.dragoneye.wjjt.dao.InvestRecord;
import com.dragoneye.wjjt.dao.InvestRecordDao;
import com.dragoneye.wjjt.dao.MyDaoMaster;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.MyEarningModel;
import com.dragoneye.wjjt.protocol.GetProjectListProtocol;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.dragoneye.wjjt.view.RefreshableView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by happysky on 15-8-17.
 * 收益记录
 */
public class EarningRecordFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private RefreshableView refreshableView;
    private ListView mListView;
    private LoadingMoreFooterProxy mLoadingMoreProxy;
    private Handler handler = new Handler();

    private EarningProjectListViewAdapter mAdapter;
    private ArrayList<MyEarningModel> mEarningProjects = new ArrayList<>();
    private int mCurEarningRecordPageIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_record_earning, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_record_earning_refreshableView);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingMoreProxy.reset();
                        mCurEarningRecordPageIndex = -1;
                        handler.post(onUpdateEarningList_r);
                    }
                });
            }
        }, PreferencesConfig.FRAGMENT_HOME_RECORD_EARNING);
        refreshableView.setTextColor(Color.WHITE);
        refreshableView.setArrowColor(Color.WHITE);


        mListView = (ListView)getActivity().findViewById(R.id.home_record_earning_listView);
        mLoadingMoreProxy = new LoadingMoreFooterProxy(getActivity(), mListView);
        mLoadingMoreProxy.setOnLoadingMoreListener(new LoadingMoreFooterProxy.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                handler.post(onUpdateEarningList_r);
            }
        });
        mLoadingMoreProxy.reset();
        mListView.setDividerHeight(0);

        mListView.setOnItemClickListener(this);
        mAdapter = new EarningProjectListViewAdapter(getActivity(), mEarningProjects);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if( position >= mEarningProjects.size() ){
            return;
        }

        MyEarningModel myEarningModel = (MyEarningModel)mListView.getItemAtPosition(position);
        ArrayList<String> img = new ArrayList<>();
        try{
            img = ToolMaster.gsonInstance().fromJson(myEarningModel.getImageUrl(),
                    new TypeToken<ArrayList<String>>(){}.getType());
        }catch (Exception e){

        }
        ProjectDetailActivity.CallProjectDetailActivity(getActivity(), myEarningModel.getActivityId(), myEarningModel.getActivityName(), img,
                1, 1);
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
            EarningRecordDao dao = MyDaoMaster.getDaoSession().getEarningRecordDao();

            QueryBuilder queryBuilder = dao.queryBuilder();
            queryBuilder.where(EarningRecordDao.Properties.IsRead.eq(false));

            List<EarningRecord> unReadList = queryBuilder.list();
            for(EarningRecord earningRecord : unReadList){
                earningRecord.setIsRead(true);
            }
            dao.updateInTx(unReadList);
        }
    }

    Runnable onUpdateEarningList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            params.put(GetProjectListProtocol.GET_ORDER_PARAM_USER_ID, ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity()).getUserId());
            params.put(GetProjectListProtocol.GET_ORDER_PARAM_PAGE_INDEX, mCurEarningRecordPageIndex + 1);
            params.put(GetProjectListProtocol.GET_ORDER_PARAM_TOKEN, ((MyApplication)getActivity().getApplication()).getToken(getActivity()));
            params.put(GetProjectListProtocol.GET_ORDER_PARAM_NUM_PER_PAGE, 10);
            params.put("token", ((MyApplication)getActivity().getApplication()).getToken(getActivity()));

            HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "ActivityController/getActivityEarnings", params, new HttpClient.MyHttpHandler() {
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

                    mCurEarningRecordPageIndex += 1;
                    ArrayList<MyEarningModel> earningModels = onUpdateEarningListSuccess(s);
                    addToRecord(earningModels);
                    if (mLoadingMoreProxy.isLoadingMore()) {
                        if (earningModels.isEmpty()) {
                            mLoadingMoreProxy.finishLoadingMore(true);
                        } else {
                            mEarningProjects.addAll(earningModels);
                            mAdapter.notifyDataSetChanged();
                            mLoadingMoreProxy.finishLoadingMore(false);
                        }
                    } else {
                        mLoadingMoreProxy.reset();
                        mEarningProjects.clear();
                        mEarningProjects.addAll(earningModels);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private void addToRecord(List<MyEarningModel> list){
        ArrayList<EarningRecord> records = new ArrayList<>();
        for(MyEarningModel myEarningModel : list){
            EarningRecord earningRecord = new EarningRecord();
            earningRecord.setId((long)myEarningModel.getId());
            earningRecord.setIsRead(false);
            records.add(earningRecord);
        }

        ArrayList<EarningRecord> recordsToInsert = new ArrayList<>();
        EarningRecordDao dao = MyDaoMaster.getDaoSession().getEarningRecordDao();
        for(EarningRecord earningRecord : records){
            if(dao.load(earningRecord.getId()) == null){
                recordsToInsert.add(earningRecord);
            }
        }

        dao.insertInTx(recordsToInsert);
    }



    private ArrayList<MyEarningModel> onUpdateEarningListSuccess(String response){
        ArrayList<MyEarningModel> earningModels = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                MyEarningModel earningModel = new MyEarningModel();
                earningModel.setActivityStageId(jsonArray1.getString(0));
                earningModel.setActivityName(jsonArray1.getString(1));
                earningModel.setActivityTotalStage(jsonArray1.getInt(2));
                earningModel.setActivityStageIndex(jsonArray1.getInt(3));
                earningModel.setActivityId(jsonArray1.getString(4));
                earningModel.setImageUrl(jsonArray1.getString(5));
                earningModel.setEarningPrice(jsonArray1.getInt(6));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try{
                    earningModel.setEarningDate(sdf.parse(jsonArray1.getString(7)));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                earningModel.setId(jsonArray1.getInt(8));
                earningModels.add(earningModel);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return earningModels;
    }

    private class EarningProjectListViewAdapter extends BaseAdapter {
        private List<MyEarningModel> data;
        private Context context;
        private LayoutInflater mInflater;

        public EarningProjectListViewAdapter(Context context, List<MyEarningModel> data){
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
            MyEarningModel myEarningModel = (MyEarningModel)getItem(position);


            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview_earnings, parent, false);
                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.home_record_earning_iv_projectLogo);
                viewHolder.tvProjectName = (TextView)convertView.findViewById(R.id.home_record_earning_tv_projectName);
                viewHolder.tvEarningDate = (TextView)convertView.findViewById(R.id.home_record_earning_tv_date);
                viewHolder.tvEarningPrice = (TextView)convertView.findViewById(R.id.home_record_earning_tv_earningPrice);
                viewHolder.tvStageIndex = (TextView)convertView.findViewById(R.id.home_record_earning_tv_stageIndex);
                viewHolder.tvNew = (TextView)convertView.findViewById(R.id.home_record_earning_tv_new);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvProjectName.setText(myEarningModel.getActivityName());
            viewHolder.tvEarningPrice.setText(String.format("您的收益:%s", ToolMaster.convertToPriceString(myEarningModel.getEarningPrice())));
            viewHolder.tvStageIndex.setText(String.format("第%d期", myEarningModel.getActivityStageIndex()));
            try{
                JSONArray jsonArray = new JSONArray(myEarningModel.getImageUrl());
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

            EarningRecordDao dao = MyDaoMaster.getDaoSession().getEarningRecordDao();
            EarningRecord earningRecord = dao.load((long)myEarningModel.getId());
            if(earningRecord != null){
                viewHolder.tvNew.setVisibility( earningRecord.getIsRead() ? View.INVISIBLE : View.VISIBLE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            viewHolder.tvEarningDate.setText(sdf.format(myEarningModel.getEarningDate()));

            return convertView;
        }

        private class ViewHolder{
            ImageView ivLogo;
            TextView tvProjectName;
            TextView tvStageIndex;
            TextView tvEarningDate;
            TextView tvEarningPrice;
            TextView tvNew;
        }
    }
}
