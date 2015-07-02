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
import android.widget.ImageView;

import com.dragoneye.money.R;
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


public class ProjectEditActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_project_launched);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_edit, menu);
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
