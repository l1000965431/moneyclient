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

//    @Override
//    public void onPause(){
//        super.onPause();
//        handler.
//    }
//
//    @Override
//    public void onResume(){
//        super.onResume();
//    }

    private static final int MESSAGE_TICK = 1;
    private static class MyHandler extends Handler {
        private final WeakReference<TimeCounterActivity> mRef;

        public MyHandler(TimeCounterActivity ref){
            mRef = new WeakReference<>(ref);
        }
        @Override
        public void handleMessage(Message msg){
            if(mRef.get() == null){
                return;
            }
            switch (msg.what){
                case MESSAGE_TICK:
                    long currentMillis = System.currentTimeMillis();
                    long millisLeft = mRef.get().counterMillis - (currentMillis - mRef.get().originMillis);
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
    private long originMillis;

    public long getMillisLeft(){
        return this.counterMillis - (System.currentTimeMillis() - originMillis);
    }

    protected void initCounter(int preferencesId, int counterInterval){
        this.preferencesId = preferencesId;
        this.counterInterval = counterInterval;
        this.originMillis = PreferencesHelper.getTimeCounterOriginMillis(this, PreferencesConfig.TIME_COUNTER_ID + preferencesId);
    }

    public void startCount(long counterMillis){
        this.originMillis =  System.currentTimeMillis();
        PreferencesHelper.setTimeCounterOriginMillis(this, PreferencesConfig.TIME_COUNTER_ID + preferencesId, originMillis);
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
