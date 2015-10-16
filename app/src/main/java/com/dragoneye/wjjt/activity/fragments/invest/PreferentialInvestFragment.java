package com.dragoneye.wjjt.activity.fragments.invest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.fragments.BaseFragment;
import com.dragoneye.wjjt.application.MyApplication;
import com.dragoneye.wjjt.config.HttpUrlConfig;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.http.HttpClient;
import com.dragoneye.wjjt.http.HttpParams;
import com.dragoneye.wjjt.model.PreferentialModel;
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
import java.util.Date;
import java.util.List;

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
    private int mAutoCloseCount;


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
                        mLoadingMoreProxy.reset();
                        mCurPageIndex = -1;
                        handler.post(onUpdateProjectList_r);
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
    }

    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

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
            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
                    .abs(values[2]) > 17))
            {
                if( mMainAlertDialog == null || !mMainAlertDialog.isShowing() )
                    return;

                //摇动手机后，再伴随震动提示~~
                if( mRushResultAlertDialog != null && mRushResultAlertDialog.isShowing() )
                    return;

                vibrator.vibrate(500);
                startRush();
            }
        }
    }

    private void startRush(){
        rushFailure();
    }

    private void rushSuccess(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.home_investment_listview_earning_preferential, null);

        mRushResultAlertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialog)
                .create();
        mRushResultAlertDialog.show();
    }

    private void rushFailure(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.home_investment_listview_failure_preferential, null);

        final TextView tvAutoClose = (TextView)dialog.findViewById(R.id.rush_failure_tv_auto_close);

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
//            params.put("pageIndex", mCurPageIndex + 1);
//            params.put("numPerPage", 10);

            HttpClient.atomicPost(getActivity(), HttpUrlConfig.URL_ROOT + "ActivityPreferentialController/getActivityPreferentialInfo",
                    params, new HttpClient.MyHttpHandler() {
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
                            ArrayList<PreferentialModel> detailModels = jsonToProjectList(s);
                            mCurPageIndex += 1;
                            if (mLoadingMoreProxy.isLoadingMore()) {
                                loadMoreProjectToList(detailModels);
                            } else {
                                reloadProjectList(detailModels);
                            }
                        }
                    });

        }
    };

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

        String dateString = jsonObject.getString("activityStartTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try{
            date = sdf.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        preferentialModel.setDate(date);

        return preferentialModel;
    }



    @Override
    public void onClick(View v){
        switch (v.getId()){

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PreferentialModel preferentialModel = (PreferentialModel)mListView.getItemAtPosition(position);
        showProjectDetailDialog(preferentialModel);
    }

    private void showProjectDetailDialog(PreferentialModel preferentialModel){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.home_investment_listview_detail_preferential, null);

        final TextView tvName = (TextView)dialog.findViewById(R.id.home_investment_listview_detail_preferential_tv_name);
        tvName.setText(preferentialModel.getName() + "  特惠项目");

        final ImageView ivShake = (ImageView)dialog.findViewById(R.id.home_investment_listview_detail_preferential_iv_shake);
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        ivShake.startAnimation(shake);

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
            PreferentialModel preferentialModel = (PreferentialModel)getItem(position);

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
                    break;
                case PreferentialModel.STATE_FINISH:
                    viewHolder.vBegin.setVisibility(View.GONE);
                    break;
            }

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
            }catch (JSONException e){
                e.printStackTrace();
            }
            viewHolder.tvName.setText(preferentialModel.getName());
            viewHolder.tvSummary.setText(preferentialModel.getSummary());
            viewHolder.tvBonusPool.setText(String.valueOf(preferentialModel.getBonusPool()));
            viewHolder.tvExp.setText(String.valueOf(preferentialModel.getExp()));

            Date date = preferentialModel.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            viewHolder.tvMonth.setText(String.valueOf(calendar.get(Calendar.MONTH)));
            viewHolder.tvDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            viewHolder.tvHour.setText(String.format("%d:00", calendar.get(Calendar.HOUR_OF_DAY)));

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
        }
    }
}
