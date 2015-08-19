package com.dragoneye.wjjt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragoneye.wjjt.R;

import java.util.zip.Inflater;

/**
 * Created by happysky on 15-8-17.
 */
public class ActivityLoadingProxy {
    private View mRootView;
    private ProgressBar mProgressBar;
    private TextView mTVLoadingState;
    private TextView mTVRetry;

    public interface OnRetryListener{
        void onRetry();
    }
    OnRetryListener mOnRetryListener;

    public View createView(Context context, ViewGroup root){
        mRootView = LayoutInflater.from(context).inflate(R.layout.activity_loading, root);

        mProgressBar = (ProgressBar)mRootView.findViewById(R.id.activity_loading_progressBar);
        mTVLoadingState = (TextView)mRootView.findViewById(R.id.activity_loading_tv_loadingState);
        mTVRetry = (TextView)mRootView.findViewById(R.id.activity_loading_tv_retry);
        mTVRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnRetryListener != null){
                    mOnRetryListener.onRetry();
                    setLoading();
                }
            }
        });

        return mRootView;
    }

    public void setOnRetryListener(OnRetryListener onRetryListener){
        mOnRetryListener = onRetryListener;
    }

    public void setLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
        mTVLoadingState.setText("载入中");
        mTVRetry.setVisibility(View.GONE);
    }

    public void setLoadingFailed(){
        mProgressBar.setVisibility(View.GONE);
        mTVLoadingState.setText("无法连接到服务器，请检查网络或稍后再试");
        mTVRetry.setVisibility(View.VISIBLE);
    }

    public View getView(){
        return mRootView;
    }
}
