package com.dragoneye.wjjt.activity.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.tool.ToolMaster;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ImageMulSelectedActivity extends ActionBarActivity {
    public static String RESULT_IMAGE_PATH_ARRAY = "RESULT_IMAGE_PATH_ARRAY";

    private GridView mGridView;
    ArrayList<String> mDataArrays;
    ImageSelectAdapter mAdapter;
    HashSet<Integer> mSelectedImageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        initView();
        initData();
        getAllImagesFromGallery();
    }

    private void initView(){
        mGridView = (GridView)findViewById(R.id.image_select_main_grid_view);
    }

    private void initData(){
        mSelectedImageIndex = new HashSet<>();
        mDataArrays = new ArrayList<>();

        mAdapter = new ImageSelectAdapter(this, mDataArrays);

        mGridView.setAdapter(mAdapter);
    }

    private void getAllImagesFromGallery(){
        mDataArrays.clear();

        mDataArrays.addAll(ToolMaster.getAllShownImagesPath(this));

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_image_select_finish) {
            ArrayList<String> strings = new ArrayList<>();
            for( Integer index : mSelectedImageIndex ){
                strings.add( mDataArrays.get(index) );
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra(RESULT_IMAGE_PATH_ARRAY, strings);
            setResult( RESULT_OK, intent );
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public class ImageSelectAdapter extends BaseAdapter {
        private List<String> data;
        private Context context;
        private LayoutInflater mInflater;

        public ImageSelectAdapter(Context context, List<String> data){
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
        public View getView(final int position, View convertView, ViewGroup parent){
//            GalleryImage galleryImage = (GalleryImage)getItem(position);
            String imagePath = (String)getItem(position);
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.function_gallery_upload_grid_view_item_image, parent, false);
                viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.function_gallery_upload_grid_view_item_image_image);
                viewHolder.ckSelected = (CheckBox)convertView.findViewById(R.id.function_gallery_upload_grid_view_item_image_check_box);
                viewHolder.ckSelected.setVisibility(View.VISIBLE);
                viewHolder.ckSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mSelectedImageIndex.contains(position)){
                            mSelectedImageIndex.remove(position);
                        }else {
                            mSelectedImageIndex.add(position);
                        }
                    }
                });

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.ckSelected.setChecked( mSelectedImageIndex.contains(position) );
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.icon_albums)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

            ImageLoader.getInstance().displayImage(Uri.fromFile(new File(imagePath)).toString(),
                    viewHolder.ivImage, options);

            return convertView;
        }



        private class ViewHolder{
            public ImageView ivImage;
            public CheckBox ckSelected;
        }
    }
}
