package com.dragoneye.money.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.BaseActivity;
import com.dragoneye.money.view.ZoomImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class ImageExplorerActivity extends BaseActivity {

    public static final String EXTRA_URI_ARRAY = "EXTRA_URI_ARRAY";
    public static final String EXTRA_INDEX_TO_SHOW = "EXTRA_INDEX_TO_SHOW";

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_explorer);

        Intent intent = getIntent();
        final ArrayList<Uri> uris = intent.getParcelableArrayListExtra(EXTRA_URI_ARRAY);
        int indexToShow = intent.getIntExtra(EXTRA_INDEX_TO_SHOW, 0);
        final ZoomImageView[] imageViews = new ZoomImageView[uris.size()];

        mViewPager = (ViewPager)findViewById(R.id.activity_image_explorer_view_pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                ZoomImageView imageView = null;
                try {
                    imageView = new ZoomImageView(ImageExplorerActivity.this);

//                    imageView.setImageBitmap( MediaStore.Images.Media.getBitmap(getContentResolver(),
//                            uris.get(position) ) );
                    ImageLoader.getInstance().displayImage( uris.get(position).toString(), imageView, null, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                            if (mViewPager.getCurrentItem() == position) {
                            }
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            WindowManager wm = (WindowManager) ImageExplorerActivity.this.getSystemService(Context.WINDOW_SERVICE);
                            imageViews[position].scaleToWindowSize(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageViews[position] = imageView;
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(imageViews[position]);
            }

            @Override
            public int getCount() {
                return uris.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        mViewPager.setCurrentItem(indexToShow);
    }
}
