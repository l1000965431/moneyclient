package com.dragoneye.money.activity;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.dao.InvestedProject;
import com.dragoneye.money.dao.InvestedProjectDao;
import com.dragoneye.money.dao.MyDaoMaster;
import com.dragoneye.money.dao.Project;
import com.dragoneye.money.dao.ProjectDao;
import com.dragoneye.money.dao.ProjectImage;
import com.dragoneye.money.dao.ProjectImageDao;
import com.dragoneye.money.view.DotViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class InvestProjectActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID";

    private DotViewPager mDotViewPager;
    private ArrayList<String> mImageUrl;
    ArrayList<View> viewContainer = new ArrayList<>();
    private TextView mConfirmTextView;
    private EditText mPriceEditText;
    private Project mProject;
    private InvestedProject mInvestedProject;
    private TextView mInvestPriceTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_investment_listview_detail);
        initView();
        initData();

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

    private void initView(){
        mDotViewPager = (DotViewPager)findViewById(R.id.investment_project_detail_dot_viewpager);

        mConfirmTextView = (TextView)findViewById(R.id.textView27);
        mConfirmTextView.setOnClickListener(this);

        mPriceEditText = (EditText)findViewById(R.id.invest_project_et_price);
        mPriceEditText.setOnClickListener(this);

        mInvestPriceTextView = (TextView)findViewById(R.id.invest_project_tv_invest_price);
    }

    private void initData(){
        mImageUrl = new ArrayList<>();

        Intent intent = getIntent();
        long projectId = intent.getLongExtra(EXTRA_PROJECT_ID, 1);

        ProjectDao projectDao = MyDaoMaster.getDaoSession().getProjectDao();
        mProject = projectDao.load(projectId);
        if( mProject == null ){
            finish();
            return;
        }

        ProjectImageDao projectImageDao = MyDaoMaster.getDaoSession().getProjectImageDao();
        QueryBuilder queryBuilder = projectImageDao.queryBuilder();
        queryBuilder.where(ProjectImageDao.Properties.ProjectId.eq(projectId));
        List<ProjectImage> projectImages = queryBuilder.build().list();

        for(ProjectImage projectImage : projectImages){
            mImageUrl.add(projectImage.getImageUrl());
        }

        InvestedProjectDao investedProjectDao = MyDaoMaster.getDaoSession().getInvestedProjectDao();
        queryBuilder = investedProjectDao.queryBuilder();
        queryBuilder.where(InvestedProjectDao.Properties.ProjectId.eq(mProject.getId()));
        mInvestedProject = (InvestedProject)queryBuilder.unique();
        updateInvestedPrice();

    }

    private void updateInvestedPrice(){
        if( mInvestedProject == null ){
            mInvestPriceTextView.setText(R.string.invest_project_no_invested_price);
        }else {
            String string = String.format(getString(R.string.invest_project_invested_price), mInvestedProject.getPrice());
            mInvestPriceTextView.setText(string);
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.textView27:
                onConfirm();
                break;
        }
    }

    private void onConfirm(){
        String investPrice = mPriceEditText.getText().toString();
        int price = Integer.parseInt(investPrice);
        if( price > 0 ){
            InvestedProjectDao dao = MyDaoMaster.getDaoSession().getInvestedProjectDao();
            if( mInvestedProject == null ){
                InvestedProject investedProject = new InvestedProject(null, mProject.getId(), (float)price);
                dao.insert(investedProject);
            }else{
                mInvestedProject.setPrice( mInvestedProject.getPrice() + price );
                dao.update(mInvestedProject);
            }
            finish();
        }
    }

    private class ImageViewPagerAdapter extends PagerAdapter{


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
                    Intent intent = new Intent(InvestProjectActivity.this, ImageExplorerActivity.class);
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (String url : mImageUrl) {
                        uris.add(Uri.parse(url));
                    }
                    intent.putExtra(ImageExplorerActivity.EXTRA_URI_ARRAY, uris);
                    intent.putExtra(ImageExplorerActivity.EXTR_INDEX_TO_SHOW, position);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invest_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_invest_project_detail) {
            Intent intent = new Intent(this, ProjectDetailActivity.class);
            intent.putExtra(EXTRA_PROJECT_ID, mProject.getId());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
