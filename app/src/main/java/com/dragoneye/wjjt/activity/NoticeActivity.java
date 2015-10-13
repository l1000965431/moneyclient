package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happysky on 15-10-10.
 */
public class NoticeActivity extends BaseActivity {
    ListView mListView;
    NoticeAdapter mAdapter;
    List<String> mData = new ArrayList<>();

    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, NoticeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_listview);

        mData.add("");

        mListView = (ListView)findViewById(R.id.common_listview_listView);
        mAdapter = new NoticeAdapter(this, mData);
        mListView.setAdapter(mAdapter);
    }

    public class NoticeAdapter extends BaseAdapter {
        private List<String> data;
        private Context context;
        private LayoutInflater mInflater;

        public NoticeAdapter(Context context, List<String> data){
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
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_news, parent, false);


                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            return convertView;
        }

        private class ViewHolder{
        }
    }
}
