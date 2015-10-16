package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;

/**
 * Created by happysky on 15-10-15.
 *
 */
public class PreferentialDetailActivity extends BaseActivity implements SensorEventListener {
    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, PreferentialDetailActivity.class);
        activity.startActivity(intent);
    }


    SensorManager sensorManager = null;
    Vibrator vibrator = null;


    AlertDialog mRushResultAlertDialog;
    private int mAutoCloseCount;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_investment_listview_detail_preferential);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
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
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialog = inflater.inflate(R.layout.home_investment_listview_earning_preferential, null);

        mRushResultAlertDialog = new AlertDialog.Builder(this)
                .setView(dialog)
                .create();
        mRushResultAlertDialog.show();
    }

    private void rushFailure(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialog = inflater.inflate(R.layout.home_investment_listview_failure_preferential, null);

        final TextView tvAutoClose = (TextView)dialog.findViewById(R.id.rush_failure_tv_auto_close);

        mRushResultAlertDialog = new AlertDialog.Builder(this)
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
}
