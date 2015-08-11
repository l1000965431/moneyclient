package com.dragoneye.wjjt.activity.base;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dragoneye.wjjt.R;

public class ProgressActionBarActivity extends BaseActivity {

    MenuItem mProgressMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_progress_action_bar, menu);
        mProgressMenu = menu.findItem(R.id.refresh_loading);
        onMenuCreated();
        return true;
    }

    protected void onMenuCreated(){

    }

    public void setLoadingState(boolean refreshing) {
        if (mProgressMenu != null) {
            if (refreshing) {
                mProgressMenu
                        .setActionView(R.layout.actionbar_indeterminate_progress);
                mProgressMenu.setVisible(true);
            } else {
                mProgressMenu.setVisible(false);
                mProgressMenu.setActionView(null);
            }
        }
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
