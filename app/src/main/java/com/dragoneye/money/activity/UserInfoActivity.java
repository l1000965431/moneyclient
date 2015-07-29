package com.dragoneye.money.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dragoneye.money.R;
import com.dragoneye.money.user.CurrentUser;

public class UserInfoActivity extends ActionBarActivity {

    TextView mTVUserName;
    TextView mTVUserId;
    TextView mTVAddress;
    TextView mTVSexuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_self_group_pinfo);
        initView();
        initData();
    }

    private void initView(){
        mTVUserName = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout1_num);
        mTVUserId = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout2_num);
        mTVAddress = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout3_num);
        mTVSexuality = (TextView)findViewById(R.id.home_self_group_pinfo_linearLayout5_num);

        mTVUserName.setText(CurrentUser.getCurrentUser().getUserName());
        mTVUserId.setText(CurrentUser.getCurrentUser().getUserId());
        mTVAddress.setText(CurrentUser.getCurrentUser().getAddress());
        mTVSexuality.setText(CurrentUser.getCurrentUser().getSexualityString());
    }

    private void initData(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
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
