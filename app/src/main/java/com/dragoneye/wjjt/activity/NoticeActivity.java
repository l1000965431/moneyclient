package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.dao.MessageBoxItem;
import com.dragoneye.wjjt.dao.MessageBoxItemDao;
import com.dragoneye.wjjt.dao.MyDaoMaster;
import com.dragoneye.wjjt.view.LoadingMoreFooterProxy;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by happysky on 15-10-10.
 */
public class NoticeActivity extends BaseActivity {
    ListView mListView;
    NoticeAdapter mAdapter;
    List<MessageBoxItem> mData = new ArrayList<>();

    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, NoticeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_listview);

        final MessageBoxItemDao dao = MyDaoMaster.getDaoSession().getMessageBoxItemDao();
        QueryBuilder queryBuilder = dao.queryBuilder();
        queryBuilder.build();

        mData.addAll(queryBuilder.list());

        Collections.sort(mData, new Comparator<MessageBoxItem>() {
            @Override
            public int compare(MessageBoxItem lhs, MessageBoxItem rhs) {
                return lhs.getId() > rhs.getId() ? -1 : 1;
            }
        });

        mListView = (ListView)findViewById(R.id.common_listview_listView);
        mAdapter = new NoticeAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.setDividerHeight(0);

        final LoadingMoreFooterProxy loadingMoreFooterProxy = new LoadingMoreFooterProxy(this, mListView);
        loadingMoreFooterProxy.setOnLoadingMoreListener(new LoadingMoreFooterProxy.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                loadingMoreFooterProxy.finishLoadingMore(true);
            }
        });
        loadingMoreFooterProxy.reset();
        loadingMoreFooterProxy.setBackgroundColor(0x96000000);
    }

    private View.OnClickListener urlOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String linkUrl = (String)v.getTag();
            AgreementActivity.OpenUrl(NoticeActivity.this, linkUrl, "");
        }
    };

    public class NoticeAdapter extends BaseAdapter {
        private List<MessageBoxItem> data;
        private Context context;
        private LayoutInflater mInflater;

        public NoticeAdapter(Context context, List<MessageBoxItem> data){
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

            MessageBoxItem item = (MessageBoxItem)getItem(position);
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.home_news, parent, false);
                viewHolder.ivImage1 = (ImageView)convertView.findViewById(R.id.home_news_Headlines_imageView);
                viewHolder.tvContent1 = (TextView)convertView.findViewById(R.id.home_news_Headlines_textView);
                viewHolder.ivImage2 = (ImageView)convertView.findViewById(R.id.home_news_Second_imageView);
                viewHolder.tvContent2 = (TextView)convertView.findViewById(R.id.home_news_Second_textView);
                viewHolder.ivImage3 = (ImageView)convertView.findViewById(R.id.home_news_Third_imageView);
                viewHolder.tvContent3 = (TextView)convertView.findViewById(R.id.home_news_Third_textView);

                viewHolder.vUrl1 = convertView.findViewById(R.id.home_news_Headlines);
                viewHolder.vUrl1.setOnClickListener(urlOnClickListener);
                viewHolder.vUrl2 = convertView.findViewById(R.id.home_news_Second);
                viewHolder.vUrl2.setOnClickListener(urlOnClickListener);
                viewHolder.vUrl3 = convertView.findViewById(R.id.home_news_Third);
                viewHolder.vUrl3.setOnClickListener(urlOnClickListener);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.vUrl2.setVisibility(View.GONE);
            viewHolder.vUrl3.setVisibility(View.GONE);
            String json = item.getMessageJson();
            try{
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(i == 0){
                        viewHolder.vUrl1.setTag(jsonObject.getString("link"));
                        ImageLoader.getInstance().displayImage(jsonObject.getString("imgUrl"), viewHolder.ivImage1);
                        viewHolder.tvContent1.setText(jsonObject.getString("messageContent"));
                    }else if(i == 1){
                        viewHolder.vUrl2.setVisibility(View.VISIBLE);
                        viewHolder.vUrl2.setTag(jsonObject.getString("link"));
                        ImageLoader.getInstance().displayImage(jsonObject.getString("imgUrl"), viewHolder.ivImage2);
                        viewHolder.tvContent2.setText(jsonObject.getString("messageContent"));
                    }else if(i == 2){
                        viewHolder.vUrl3.setVisibility(View.VISIBLE);
                        viewHolder.vUrl3.setTag(jsonObject.getString("link"));
                        ImageLoader.getInstance().displayImage(jsonObject.getString("imgUrl"), viewHolder.ivImage3);
                        viewHolder.tvContent3.setText(jsonObject.getString("messageContent"));
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }



            return convertView;
        }

        private class ViewHolder{
            ImageView ivImage1;
            TextView tvContent1;
            ImageView ivImage2;
            TextView tvContent2;
            ImageView ivImage3;
            TextView tvContent3;

            View vUrl1;
            View vUrl2;
            View vUrl3;
        }
    }
}
