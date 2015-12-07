package com.dragoneye.wjjt.activity.base;

import android.os.Handler;
import android.os.Message;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.config.PreferencesConfig;
import com.dragoneye.wjjt.tool.PreferencesHelper;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2015/12/7.
 */
public class TimeCounterActivity extends BaseActivity {
    private static final int MESSAGE_TICK = 1;
    private static class MyHandler extends Handler {
        private final WeakReference<TimeCounterActivity> mRef;


        public MyHandler(TimeCounterActivity ref){
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_TICK:
                    long originMillis = PreferencesHelper.getTimeCounterOriginMillis(mRef.get(), PreferencesConfig.TIME_COUNTER_ID + mRef.get().preferencesId);
                    long currentMillis = System.currentTimeMillis();
                    long millisLeft = mRef.get().counterMillis - (currentMillis - originMillis);
                    if(millisLeft <= 0){
                        mRef.get().onCountFinished();
                    }else {
                        mRef.get().onTick(millisLeft);
                        mRef.get().handler.sendMessageDelayed(mRef.get().handler.obtainMessage(MESSAGE_TICK), mRef.get().counterInterval);
                    }
                    break;
            }
        }
    }
    private MyHandler handler = new MyHandler(this);


    public long getCounterMillis() {
        return counterMillis;
    }

    public void setCounterMillis(long counterMillis) {
        this.counterMillis = counterMillis;
    }

    public int getCounterInterval() {
        return counterInterval;
    }

    public void setCounterInterval(int counterInterval) {
        this.counterInterval = counterInterval;
    }

    public int getPreferencesId() {
        return preferencesId;
    }

    public void setPreferencesId(int preferencesId) {
        this.preferencesId = preferencesId;
    }

    private int counterInterval = 1;
    private long counterMillis = 600000;
    private int preferencesId;

    protected void initCounter(int preferencesId, int counterInterval){
        this.preferencesId = preferencesId;
        this.counterInterval = counterInterval;
    }

    public void startCount(long counterMillis){
        PreferencesHelper.setTimeCounterOriginMillis(this, PreferencesConfig.TIME_COUNTER_ID + preferencesId, System.currentTimeMillis());
        this.counterMillis = counterMillis;
        handler.sendMessage(handler.obtainMessage(MESSAGE_TICK));
    }

    public void continueCount(long counterMillis){
        this.counterMillis = counterMillis;
        handler.sendMessage(handler.obtainMessage(MESSAGE_TICK));
    }

    protected void onTick(long millisLeft){

    }

    protected void onCountFinished(){

    }
}
