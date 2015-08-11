package com.dragoneye.wjjt.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.tool.UIHelper;

import java.util.ArrayList;

/**
 * Created by happysky on 15-7-1.
 *
 */
public class DotViewPager extends LinearLayout {
    private static final int AUTO_SCROLL_PAGE_INTERVAL = 6000;

    private ViewPager mViewPager;
    private LinearLayout mDotsRoot;
    private ArrayList<ImageView> mDotsViewArray;
    private ImageView mSelectedDot;
    private PagerAdapter mAdapter;
    private Handler mHandler;
    private boolean autoScroll = true;
    private int autoScrollInterval = AUTO_SCROLL_PAGE_INTERVAL;

    public static abstract class OnViewPageChangeListener{
        public abstract void onPageScrolled(int position);
    }

    public OnViewPageChangeListener getPageChangeListener() {
        return pageChangeListener;
    }

    public void setPageChangeListener(OnViewPageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener;
    }

    OnViewPageChangeListener pageChangeListener;

    public DotViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_view_dot_view_pager, null, true);
        mViewPager = (ViewPager)rootView.findViewById(R.id.custom_dots_pager_view_viewpager);
        mDotsRoot = (LinearLayout)rootView.findViewById(R.id.custom_dots_pager_view_dots_root);
        addView(rootView);
        initDots();
        mHandler = new Handler();


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(pageChangeListener != null){
                    pageChangeListener.onPageScrolled(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                setDotSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mHandler.postDelayed(scrollPage, autoScrollInterval);
    }

    final Runnable scrollPage = new Runnable() {
        @Override
        public void run() {
            if( isAutoScroll() ){
                int count = mViewPager.getAdapter().getCount();
                int currentIndex = mViewPager.getCurrentItem();
                if( currentIndex + 1 == count ){
                    mViewPager.setCurrentItem(0);
                }else {
                    mViewPager.setCurrentItem(currentIndex + 1);
                }
                mHandler.postDelayed(scrollPage, autoScrollInterval);
            }
        }
    };

    public void setAdapter(PagerAdapter adapter){
        mViewPager.setAdapter(adapter);
        resetDots(adapter.getCount());
    }

    public void refreshPager(){
        if( mViewPager.getAdapter() != null ){
            resetDots(mViewPager.getAdapter().getCount());
        }
    }

    private void resetDots(int dotCount){
        removeAllDots();
        mSelectedDot = null;
        for(int i = 0; i < dotCount; i++){
            ImageView view = new ImageView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(UIHelper.dip2px(getContext(), 10),
                    UIHelper.dip2px(getContext(), 10));
            view.setLayoutParams(layoutParams);
            view.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_poin_unchecked));

            addDots(view);
        }
        setDotSelected(0);
    }

    private void initDots(){
        mDotsViewArray = new ArrayList<>();
    }

    private void removeAllDots(){
        mDotsViewArray.clear();
        mDotsRoot.removeAllViews();
    }

    private void addDots(ImageView v){
        mDotsRoot.addView(v);
        mDotsViewArray.add(v);
    }

    private void setDotSelected(int position){
        if( position >= mDotsViewArray.size() ){
            return;
        }
        ImageView imageView = (ImageView)mDotsViewArray.get(position);

        if( mSelectedDot != null ){
            mSelectedDot.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_poin_unchecked));
        }

        if( imageView != null ){
            imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_poin_selected));
            mSelectedDot = imageView;
        }
    }

    private int getDotsCount(){
        return mDotsViewArray.size();
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public int getAutoScrollInterval() {
        return autoScrollInterval;
    }

    public void setAutoScrollInterval(int autoScrollInterval) {
        this.autoScrollInterval = autoScrollInterval;
    }
}
