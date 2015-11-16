package com.dragoneye.wjjt.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragoneye.wjjt.R;

/**
 * Created by happysky on 15-7-1.
 */
public class TopTabButton {
    private Context context;
    public TopTabButton(Context context){
        this.context = context;
    }

    public ImageView ivLine;
    public TextView tvTitle;
    public ImageView ivNewDot;

    public void setChecked(boolean checked){
        if(checked){
            ivLine.setVisibility(View.VISIBLE);
            tvTitle.setTextColor(context.getResources().getColor(R.color.home_investment_top_button_selected));
        }else {
            ivLine.setVisibility(View.INVISIBLE);
            tvTitle.setTextColor(context.getResources().getColor(R.color.home_investment_top_button_unselected));
        }
    }

    public void setIsHaveNew(boolean b){
        if(ivNewDot != null ){
            ivNewDot.setVisibility( b ? View.VISIBLE : View.GONE);
        }
    }
}
