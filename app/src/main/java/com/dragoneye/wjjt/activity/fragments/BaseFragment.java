package com.dragoneye.wjjt.activity.fragments;

import android.support.v4.app.Fragment;

import com.dragoneye.wjjt.view.TopTabButton;

/**
 * Created by happysky on 15-6-16.
 * Fragment 基类
 */
public class BaseFragment extends Fragment{


    public TopTabButton getTopButton() {
        return mTopButton;
    }

    public void setTopButton(TopTabButton mTopButton) {
        this.mTopButton = mTopButton;
    }

    private TopTabButton mTopButton;


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void onSelected(){

    }

    public void onShow(){

    }

    public void onHide(){

    }

    public void onNewMessage(String messageType){

    }
}
