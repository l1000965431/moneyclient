package com.dragoneye.wjjt.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dragoneye.wjjt.activity.ImageExplorerActivity;
import com.dragoneye.wjjt.view.DotViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;

public abstract class DotViewPagerActivity extends BaseActivity {

    protected DotViewPager mDotViewPager;
    protected ArrayList<String> mImageUrl;
//    protected ArrayList<View> viewContainer = new ArrayList<>();
    private boolean isUseImageLoader = true;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;

    public boolean isUseImageLoader() {
        return isUseImageLoader;
    }

    public void setIsUseImageLoader(boolean isUseImageLoader) {
        this.isUseImageLoader = isUseImageLoader;
    }

    private class ImageViewPagerAdapter extends PagerAdapter {
        //viewpager中的组件数量
        @Override
        public int getCount() {
            return mImageUrl.size();
        }
        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((ImageView)object);
        }
        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = getImage(position);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewClick(position);
                }
            });
            return imageView;
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
//        for(String url : mImageUrl){
//            ImageView imageView = new ImageView(this);
//            if( isUseImageLoader ){
//                ImageLoader.getInstance().displayImage(url, imageView);
//            }else {
//                try{
//                    imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),
//                            Uri.parse(url)));
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//            viewContainer.add(imageView);
//        }
        mDotViewPager.setAdapter(new ImageViewPagerAdapter());
    }

    protected void setImageScaleType(ImageView.ScaleType type){
//        for( View view : viewContainer ){
//            if( view instanceof ImageView ){
//                ((ImageView)view).setScaleType(type);
//            }
//        }
        mScaleType = type;
    }

    protected abstract void initViewPager();
    protected abstract void initImageUrl();

    protected void onViewClick(int position){
        Intent intent = new Intent(DotViewPagerActivity.this, ImageExplorerActivity.class);
        ArrayList<Uri> uris = new ArrayList<>();
        for (String url : mImageUrl) {
            uris.add(Uri.parse(url));
        }
        ImageExplorerActivity.CallActivity(this, uris, position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int ResId){
        super.setContentView(ResId);
        initViewPager();
        initImageUrl();
        initViewPagerImages();
    }

    private ImageView getImage(int position){
        ImageView imageView = new ImageView(this);
        if( position >= mImageUrl.size() ){
            return imageView;
        }
        String url = mImageUrl.get(position);
        if( isUseImageLoader ){
            ImageLoader.getInstance().displayImage(url, imageView);
        }else {
            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),
                        Uri.parse(url)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setScaleType(mScaleType);
        return imageView;
    }
}
