package com.dragoneye.money.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dragoneye.money.R;
import com.dragoneye.money.activity.base.DotViewPagerActivity;
import com.dragoneye.money.config.HttpUrlConfig;
import com.dragoneye.money.http.HttpClient;
import com.dragoneye.money.tool.ToolMaster;
import com.dragoneye.money.tool.UIHelper;
import com.dragoneye.money.view.DotViewPager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FeatureActivity extends DotViewPagerActivity {

    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        test();
    }

    @Override
    protected void initViewPager(){
        mDotViewPager = (DotViewPager)findViewById(R.id.feature_dot_viewpager);
        mDotViewPager.setAutoScroll(false);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadTest();
            }
        });
    }

    @Override
    protected void initImageUrl(){
        mImageUrl = new ArrayList<>();

        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_1).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_2).toString());
        mImageUrl.add(Uri.parse("android.resource://com.dragoneye.money/" + R.mipmap.projects_display001_3).toString());
    }

    private void test(){
        HttpClient.atomicPost(this, HttpUrlConfig.URL_ROOT + "ImageUploadController/getUploadToken",
                null, new HttpClient.MyHttpHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        UIHelper.toast(FeatureActivity.this, s);
                        token = s;
                    }
                });
    }

    private void onUploadTest(){
        UploadManager uploadManager = new UploadManager();

        Uri uri = Uri.parse(mImageUrl.get(0));
        String path = ToolMaster.getRealPathFromURI(this, uri);
        File file = new File(path);

        uploadManager.put(file, "moneyImageUploadTest", token, new UpCompletionHandler() {
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                UIHelper.toast(FeatureActivity.this, "上传成功");
            }
        }, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
