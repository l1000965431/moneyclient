package com.dragoneye.wjjt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dragoneye.wjjt.R;

/**
 * Created by happysky on 15-8-12.
 */
public class LoadingMoreFooterProxy {
    private LinearLayout mLLIsLoadingMore;
    private LinearLayout mLLNoMore;
    private View mFooter;
    private AbsListView mListView;
    private Boolean mIsLoadingMore;
    private Boolean mIsNoMore;
    private OnLoadingMoreListener onLoadingMoreListener;

    public LoadingMoreFooterProxy(Context context, AbsListView listView){
        mListView = listView;
        mFooter = LayoutInflater.from(context).inflate(R.layout.loading_list_view_item, listView, false);

        mLLIsLoadingMore = (LinearLayout)mFooter.findViewById(R.id.loading_list_view_item_panelLoadingMore);
        mLLNoMore = (LinearLayout)mFooter.findViewById(R.id.loading_list_view_item_panelNoMore);

        if( listView instanceof ListView ){
            ((ListView)listView).addFooterView(mFooter);
        }else if( listView instanceof GridViewWithHeaderAndFooter ){
            ((GridViewWithHeaderAndFooter)listView).addFooterView(mFooter);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if ( !isNoMore() && !isLoadingMore() && view.getLastVisiblePosition() == view.getCount() - 1) {
                        setIsLoadingMore();
                        if( onLoadingMoreListener != null ){
                            onLoadingMoreListener.onLoadingMore();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public void setIsLoadingMore(){
        mIsLoadingMore = true;
        mFooter.setVisibility(View.VISIBLE);
        mLLIsLoadingMore.setVisibility(View.VISIBLE);
        mLLNoMore.setVisibility(View.GONE);
    }

    public boolean isLoadingMore(){
        return mIsLoadingMore;
    }

    public void finishLoadingMore(boolean isNoMore){
        mIsLoadingMore = false;
        if( isNoMore ){
            setIsNoMore();
        }
    }

    public void setLoadingFailed(){
        reset();
    }

    public void setIsNoMore(){
        mIsNoMore = true;
        mFooter.setVisibility(View.VISIBLE);
        mLLIsLoadingMore.setVisibility(View.GONE);
        mLLNoMore.setVisibility(View.VISIBLE);
    }

    public boolean isNoMore(){
        return mIsNoMore;
    }

    public void reset(){
        mIsLoadingMore = false;
        mIsNoMore = false;
        mFooter.setVisibility(View.INVISIBLE);
    }

    public interface OnLoadingMoreListener {
        public void onLoadingMore();
    }
}
