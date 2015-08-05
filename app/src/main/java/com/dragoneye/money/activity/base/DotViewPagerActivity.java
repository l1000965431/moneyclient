package com.dragoneye.money.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dragoneye.money.activity.ImageExplorerActivity;
import com.dragoneye.money.view.DotViewPager;

import java.io.IOException;
import java.util.ArrayList;

public abstract class DotViewPagerActivity extends BaseActivity {

    protected DotViewPager mDotViewPager;
    protected ArrayList<String> mImageUrl;
    protected ArrayList<View> viewContainer = new ArrayList<>();

    private class ImageViewPagerAdapter extends PagerAdapter {
        //viewpager中的组件数量
        @Override
        public int getCount() {
            return viewContainer.size();
        }
        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewContainer.get(position));
        }
        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(viewContainer.get(position));
            viewContainer.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DotViewPagerActivity.this, ImageExplorerActivity.class);
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (String url : mImageUrl) {
                        uris.add(Uri.parse(url));
                    }
                    intent.putExtra(ImageExplorerActivity.EXTRA_URI_ARRAY, uris);
                    intent.putExtra(ImageExplorerActivity.EXTRA_INDEX_TO_SHOW, position);
                    startActivity(intent);
                }
            });
            return viewContainer.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

    private void initViewPagerImages(){
        for(String url : mImageUrl){
            ImageView imageView = new ImageView(this);
            try{
                imageView.setImageBitmap( MediaStore.Images.Media.getBitmap(getContentResolver(),
                        Uri.parse(url) ) );
                viewContainer.add(imageView);
            }catch (IOException e){

            }
        }
        mDotViewPager.setAdapter(new ImageViewPagerAdapter());
    }

    protected abstract void initViewPager();
    protected abstract void initImageUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int resId){
        super.setContentView(resId);
        initViewPager();
        initImageUrl();
        initViewPagerImages();
    }
}
