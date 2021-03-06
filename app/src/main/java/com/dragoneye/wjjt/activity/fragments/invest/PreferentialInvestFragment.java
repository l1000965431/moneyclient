package com.dragoneye.wjjt.activity.fragments.invest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.ProjectDetailActivity;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.BroadcastConfig;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.dao.InvestRecordDao;
import com.dragoneye.wjjt.dao.MyDaoMaster;
import com.dragoneye.wjjt.dao.NewPreferentialActivity;
import com.dragoneye.wjjt.dao.NewPreferentialActivityDao;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.EarningModel;
import com.dragoneye.wjjt.model.PreferentialModel;
import com.dragoneye.wjjt.tool.PreferencesHelper;
import com.dragoneye.wjjt.tool.ShakeListener;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.dragoneye.wjjt.tool.UIHelper;
import com.dragoneye.wjjt.user.UserBase;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.dragoneye.wjjt.view.RefreshableView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by happysky on 15-10-13.
 *
 */
public class PreferentialInvestFragment extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, SensorEventListener{

    private RefreshableView refreshableView;
    private ListView mListView;
    private LoadingMoreFooterProxy mLoadingMoreProxy;
    private Handler handler = new Handler();
    private int mCurPageIndex = -1;
    private PreferentialInvestAdapter mAdapter;
    private List<PreferentialModel> mData = new ArrayList<>();

    ProgressDialog progressDialog;

    AlertDialog mMainAlertDialog;
    AlertDialog mRushResultAlertDialog;

    SensorManager sensorManager = null;
    Vibrator vibrator = null;
    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHRESHOLD = 3000;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 70;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;
    private int mAutoCloseCount;

    PreferentialModel mCurSelectedModel;
    private boolean mIsWaittingResult = false;

    private int mExp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_invest_preferential, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        refreshableView = (RefreshableView)getActivity().findViewById(R.id.home_invest_preferential_refreshableView);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        PreferencesHelper.setIsHaveNewMessage(getActivity(), false, PreferencesConfig.IS_HAVE_NEW_PREFERENTIAL_ACTIVITY);
                        if(getTopButton() != null){
                            getTopButton().setIsHaveNew(false);
                        }
                        mLoadingMoreProxy.reset();
                        mCurPageIndex = -1;
                        handler.post(getWalletBalance_r);
                    }
                });
            }
        }, PreferencesConfig.FRAGMENT_HOME_INVESTMENT_PREFERENTIAL);
        refreshableView.setTextColor(Color.WHITE);
        refreshableView.setArrowColor(Color.WHITE);


        mListView = (ListView)getActivity().findViewById(R.id.home_invest_preferential_listView);
        mLoadingMoreProxy = new LoadingMoreFooterProxy(getActivity(), mListView);
        mLoadingMoreProxy.setOnLoadingMoreListener(new LoadingMoreFooterProxy.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                handler.post(onUpdateProjectList_r);
            }
        });
        mLoadingMoreProxy.reset();
        mListView.setDividerHeight(0);

        mListView.setOnItemClickListener(this);
        mAdapter = new PreferentialInvestAdapter(getActivity(), mData);
        mListView.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(getActivity());

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        mCurPageIndex = -1;
    }

    private IntentFilter intentFilter = new IntentFilter(BroadcastConfig.NEW_PREFERENTIAL_RESULT_MESSAGE);
    private BroadcastReceiver earnPreferentialReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.dismiss();
            if(mIsWaittingResult && mCurSelectedModel != null){
                String activityId = intent.getStringExtra("activityId");
                int earningPrice = intent.getIntExtra("earningPrice", 0);
                int priceLeft = intent.getIntExtra("priceLeft", 0);
                if( earningPrice > 0 ){
                    rushSuccess(earningPrice, mCurSelectedModel);
                }else {
                    rushFailure(priceLeft, mCurSelectedModel);
                }
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
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
            NewPreferentialActivityDao dao = MyDaoMaster.getDaoSession().getNewPreferentialActivityDao();

            QueryBuilder queryBuilder = dao.queryBuilder();
            queryBuilder.where(InvestRecordDao.Properties.IsRead.eq(false));

            List<NewPreferentialActivity> unReadList = queryBuilder.list();
            for(NewPreferentialActivity newPreferentialActivity : unReadList){
                newPreferentialActivity.setIsRead(true);
            }
            dao.updateInTx(unReadList);
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
        getActivity().unregisterReceiver(earnPreferentialReceiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        getActivity().registerReceiver(earnPreferentialReceiver, intentFilter);
    }

    Runnable getWalletBalance_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();

            MyApplication application = (MyApplication)getActivity().getApplication();
            params.put("userId", application.getCurrentUser(getActivity()).getUserId());
            params.put("token", application.getToken(getActivity()));

            HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "User/getUserSetInfo", params, new HttpClient.MyHttpHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    refreshableView.finishRefreshing(RefreshableView.REFRESH_RESULT_FAILED);
                    if (mLoadingMoreProxy.isLoadingMore()) {
                        mLoadingMoreProxy.setLoadingFailed();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    refreshableView.finishRefreshing(RefreshableView.REFRESH_RESULT_SUCCESS);
                    if (s == null) {
                        UIHelper.toast(getActivity(), getString(R.string.http_server_exception));
                        return;
                    }
                    int exp = 0;

                    try {
                        JSONObject object = new JSONObject(s);
                        exp = object.getInt("Exp");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mExp = exp;
                    handler.post(onUpdateProjectList_r);
                }
            });
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //当传感器精度改变时回调该方法，Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER)
        {
            if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math
                    .abs(values[2]) > 14))
            {
                if( mMainAlertDialog == null || !mMainAlertDialog.isShowing() )
                    return;

                //摇动手机后，再伴随震动提示~~
                if( mRushResultAlertDialog != null && mRushResultAlertDialog.isShowing() )
                    return;

                vibrator.vibrate(500);
                startRush(mCurSelectedModel);
            }
        }
    }

    private void startRush(final PreferentialModel preferentialModel){
        if(!isConditionEnough(preferentialModel)){
            return;
        }

        mMainAlertDialog.dismiss();
        progressDialog.show();
        mIsWaittingResult = false;
        final HttpParams params = new HttpParams();

        UserBase userBase = ((MyApplication)getActivity().getApplication()).getCurrentUser(getActivity());

        params.put("userId", userBase.getUserId());
        params.put("activityId", preferentialModel.getActivityId());

        HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "ActivityPreferentialController/JoinActivityPreferentialInfo",
                params, new HttpClient.MyHttpHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        progressDialog.dismiss();
                        UIHelper.toast(getActivity(), getString(R.string.http_can_not_connect_to_server));
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        progressDialog.dismiss();
                        int result = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String activityId = jsonObject.getString("ActivityId");
                            int earningPrice = jsonObject.getInt("Lines");
                            int priceLeft = jsonObject.getInt("RemainingBonus");
                            if (earningPrice > 0) {
                                rushSuccess(earningPrice, preferentialModel);
                            } else {
                                rushFailure(priceLeft, preferentialModel);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            UIHelper.toast(getActivity(), getString(R.string.http_server_exception));
                        }
                        mIsWaittingResult = true;
                    }
                });
//        rushFailure();
    }

    private void rushSuccess(int price, PreferentialModel preferentialModel){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.home_investment_listview_earning_preferential, null);

        final TextView tvEarningPrice = (TextView)dialog.findViewById(R.id.rush_success_tv_earning_price);
        final TextView tvProjectName = (TextView)dialog.findViewById(R.id.rush_success_tv_activity_name);
        final View confirmButton = dialog.findViewById(R.id.rush_failure_tv_auto_close);


        tvEarningPrice.setText(ToolMaster.convertRMBPriceString(price));
        tvProjectName.setText(preferentialModel.getName());

        mRushResultAlertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialog)
                .create();
        mRushResultAlertDialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRushResultAlertDialog.dismiss();
            }
        });
    }

    private void rushFailure(int priceLeft, PreferentialModel preferentialModel){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.home_investment_listview_failure_preferential, null);

        final TextView tvAutoClose = (TextView)dialog.findViewById(R.id.rush_failure_tv_auto_close);
        final TextView tvActivityName = (TextView)dialog.findViewById(R.id.rush_failure_tv_activity_name);
        final TextView tvPriceLeft = (TextView)dialog.findViewById(R.id.rush_failure_tv_price_left);

        tvActivityName.setText(preferentialModel.getName());
        tvPriceLeft.setText("奖金剩余：" + ToolMaster.convertToPriceString(priceLeft));

        mRushResultAlertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialog)
                .create();
        mRushResultAlertDialog.show();

        mAutoCloseCount = 3;
        final Runnable count_r = new Runnable() {
            @Override
            public void run() {
                --mAutoCloseCount;
                if(mAutoCloseCount == 0){
                    mRushResultAlertDialog.dismiss();
                }
                tvAutoClose.setText(String.format("（%d）秒后自动关闭", mAutoCloseCount));

            }
        };
        handler.postDelayed(count_r, 1000);
        handler.postDelayed(count_r, 2000);
        handler.postDelayed(count_r, 3000);
    }

    Runnable onUpdateProjectList_r = new Runnable() {
        @Override
        public void run() {
            HttpParams params = new HttpParams();
            params.put("page", mCurPageIndex + 1);
            params.put("findNum", 5);

            HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "ActivityPreferentialController/getActivityPreferentialInfo",
                    params, new HttpClient.MyHttpHandler() {
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
                            ArrayList<PreferentialModel> detailModels = jsonToProjectList(s);
                            addToRecord(detailModels);
                            mCurPageIndex += 1;
                            if (mLoadingMoreProxy.isLoadingMore()) {
                                loadMoreProjectToList(detailModels);
                            } else {
                                reloadProjectList(detailModels);
                            }
                            refreshableView.finishRefreshing(mData.size() > 0 ? RefreshableView.REFRESH_RESULT_SUCCESS : RefreshableView.REFRESH_RESULT_NO_CONTENT);
                        }
                    });

        }
    };

    private void addToRecord(List<PreferentialModel> list){
        ArrayList<NewPreferentialActivity> records = new ArrayList<>();
        for(PreferentialModel preferentialModel : list){
            NewPreferentialActivity newNormalActivity = new NewPreferentialActivity();
            newNormalActivity.setId(preferentialModel.getActivityId());
            newNormalActivity.setIsRead(false);
            records.add(newNormalActivity);
        }

        ArrayList<NewPreferentialActivity> recordsToInsert = new ArrayList<>();
        NewPreferentialActivityDao dao = MyDaoMaster.getDaoSession().getNewPreferentialActivityDao();
        for(NewPreferentialActivity earningRecord : records){
            if(dao.load(earningRecord.getId()) == null){
                recordsToInsert.add(earningRecord);
            }
        }

        dao.insertInTx(recordsToInsert);
    }

    private void loadMoreProjectToList(ArrayList<PreferentialModel> projectDetailModels){
        if(projectDetailModels.isEmpty()){
            mLoadingMoreProxy.finishLoadingMore(true);
        }else {
            mData.addAll(projectDetailModels);
            mAdapter.notifyDataSetChanged();
            mLoadingMoreProxy.finishLoadingMore(false);
        }
    }

    private void reloadProjectList(ArrayList<PreferentialModel> projectDetailModels){
        mLoadingMoreProxy.reset();
        mData.clear();
        mData.addAll(projectDetailModels);
        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<PreferentialModel> jsonToProjectList(String json){
        ArrayList<PreferentialModel> arrayList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                PreferentialModel preferentialModel = jsonToPreferentialModel(object);
                arrayList.add(preferentialModel);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return arrayList;
    }

    private PreferentialModel jsonToPreferentialModel(JSONObject jsonObject) throws JSONException{
        PreferentialModel preferentialModel = new PreferentialModel();
        preferentialModel.setActivityId(jsonObject.getString("activityId"));
        preferentialModel.setImageUrl(jsonObject.getString("imageUrl"));
        preferentialModel.setName(jsonObject.getString("name"));
        preferentialModel.setState(jsonObject.getInt("activityState"));
        preferentialModel.setExp(jsonObject.getInt("userEXP"));
        preferentialModel.setBonusPool(jsonObject.getInt("activityLines"));
        preferentialModel.setSummary(jsonObject.getString("summary"));
        preferentialModel.setActivityCompleteId(jsonObject.getString("activityCompleteId"));

        String dateString = jsonObject.getString("activityStartTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try{
            date = sdf.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        preferentialModel.setDate(date);

        JSONObject earnings = jsonObject.getJSONObject("activitySREarning");
        Iterator<String> keys = earnings.keys();
        List<EarningModel> earningModelList = new ArrayList<>();
        while(keys.hasNext()){
            String key = keys.next();
            EarningModel earningModel = new EarningModel();
            earningModel.setPrice(Integer.parseInt(key));
            earningModel.setNum(earnings.getInt(key));
            earningModelList.add(earningModel);
        }
        Collections.sort(earningModelList, new Comparator<EarningModel>() {
            @Override
            public int compare(EarningModel lhs, EarningModel rhs) {
                return lhs.getPrice() > rhs.getPrice() ? -1 : 1;
            }
        });
        preferentialModel.setEarnings(earningModelList);

        return preferentialModel;
    }



    @Override
    public void onClick(View v){
        switch (v.getId()){

        }
    }

    // 是否有资格参加特惠项目
    private boolean isConditionEnough(PreferentialModel preferentialModel){
        return mExp >= preferentialModel.getExp();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if( position >= mData.size() ){
            return;
        }
        PreferentialModel preferentialModel = (PreferentialModel)mListView.getItemAtPosition(position);
//        if(preferentialModel.getState() != PreferentialModel.STATE_START){
//            return;
//        }
        showProjectDetailDialog(preferentialModel);
    }

    private void showProjectDetailDialog(PreferentialModel preferentialModel){
        mCurSelectedModel = preferentialModel;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.home_investment_listview_detail_preferential, null);

        final TextView tvName = (TextView)dialog.findViewById(R.id.home_investment_listview_detail_preferential_tv_name);
        tvName.setText(preferentialModel.getName() + "  特惠项目");

        final ImageView ivShake = (ImageView)dialog.findViewById(R.id.home_investment_listview_detail_preferential_iv_shake);
        final TextView tvShake = (TextView)dialog.findViewById(R.id.home_investment_listview_detail_preferential_tv_shake);
        if( isConditionEnough(preferentialModel) ){
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            ivShake.startAnimation(shake);
        }else {
            ivShake.setVisibility(View.GONE);
            tvShake.setText("经验值不足，无法参与该项目");
        }


        final LinearLayout llEarningsRoot = (LinearLayout)dialog.findViewById(R.id.home_investment_listview_detail_preferential_ll_earnings_root);
        for(int i = 0; i < preferentialModel.getEarnings().size(); i++){
            EarningModel earningModel = preferentialModel.getEarnings().get(i);
            String string = String.format("%d等奖：    %s     (%d名)", i + 1, ToolMaster.convertToPriceString(earningModel.getPrice()),
                    earningModel.getNum());
            TextView textView = new TextView(getActivity());
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(20);
            textView.setPadding( UIHelper.dip2px(getActivity(), 30), 0, 0, 0 );
            textView.setText(string);
            llEarningsRoot.addView(textView);
        }

        mMainAlertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialog)
                .create();
        mMainAlertDialog.show();
    }




    public class PreferentialInvestAdapter extends BaseAdapter {
        private List<PreferentialModel> data;
        private Context context;
        private LayoutInflater mInflater;

        public PreferentialInvestAdapter(Context context, List<PreferentialModel> data) {
            this.context = context;
            this.data = data;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PreferentialModel preferentialModel = (PreferentialModel)getItem(position);

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_investment_listview_preferential, parent, false);

                viewHolder.ivLogo = (ImageView)convertView.findViewById(R.id.home_invest_preferential_listview_item_iv_logo);
                viewHolder.tvName = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_project_name);
                viewHolder.tvSummary = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_summary);
                viewHolder.tvBonusPool = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_bonus_pool);
                viewHolder.tvExp = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_exp);
                viewHolder.tvMonth = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_month);
                viewHolder.tvDay = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_day);
                viewHolder.tvHour = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_hour);
                viewHolder.vBegin = convertView.findViewById(R.id.home_invest_preferential_listview_item_rl_begin);
                viewHolder.vFinish = convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_finish);
                viewHolder.ivShake = (ImageView)convertView.findViewById(R.id.home_invest_preferential_listview_item_iv_shake);
                viewHolder.tvNew = (TextView)convertView.findViewById(R.id.home_invest_preferential_listview_item_tv_new);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            switch (preferentialModel.getState()){
                case PreferentialModel.STATE_WAITING:
                    viewHolder.vBegin.setVisibility(View.GONE);
                    viewHolder.vFinish.setVisibility(View.GONE);
                    break;
                case PreferentialModel.STATE_START:
                    viewHolder.vFinish.setVisibility(View.GONE);
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    viewHolder.ivShake.startAnimation(shake);
                    viewHolder.vBegin.setVisibility(View.VISIBLE);
                    break;
                case PreferentialModel.STATE_FINISH:
                    viewHolder.vBegin.setVisibility(View.GONE);
                    viewHolder.vFinish.setVisibility(View.VISIBLE);
                    break;
            }

            final ArrayList<String> imageUrls = new ArrayList<>();
            try{
                JSONArray jsonArray = new JSONArray(preferentialModel.getImageUrl());
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
                for(int i = 0; i < jsonArray.length(); i++){
                    imageUrls.add(jsonArray.getString(i));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            viewHolder.ivLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectDetailActivity.CallProjectDetailActivity(getActivity(), preferentialModel.getActivityCompleteId(),
                            preferentialModel.getName(), imageUrls, 0, 0, false);
                }
            });

            viewHolder.tvName.setText(preferentialModel.getName());
            viewHolder.tvSummary.setText(preferentialModel.getSummary());
            viewHolder.tvBonusPool.setText(ToolMaster.convertRMBPriceString(preferentialModel.getBonusPool()));
            viewHolder.tvExp.setText(String.valueOf(preferentialModel.getExp()));
//            if(mExp < preferentialModel.getExp()){
//
//            }

            Date date = preferentialModel.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            viewHolder.tvMonth.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
            viewHolder.tvDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            viewHolder.tvHour.setText(String.format("%d:00", calendar.get(Calendar.HOUR_OF_DAY)));

            NewPreferentialActivityDao dao = MyDaoMaster.getDaoSession().getNewPreferentialActivityDao();
            NewPreferentialActivity preferentialActivity = dao.load(preferentialModel.getActivityId());
            if(preferentialActivity != null){
                viewHolder.tvNew.setVisibility( preferentialActivity.getIsRead() ? View.GONE : View.VISIBLE);
            }


            return convertView;
        }

        private class ViewHolder {
            ImageView ivLogo;
            TextView tvName;
            TextView tvSummary;
            TextView tvBonusPool;
            TextView tvExp;
            TextView tvMonth;
            TextView tvDay;
            TextView tvHour;
            View vBegin;
            View vFinish;
            ImageView ivShake;
            TextView tvNew;
        }
    }
}
