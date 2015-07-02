package com.dragoneye.money.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragoneye.money.R;

/**
 * Created by happysky on 15-7-1.
 */
public class TopTabButton {
    private Context context;
    public TopTabButton(Context context){
        this.context = context;
    }

    public ImageView imageView;
    public TextView textView;
    public void setChecked(boolean checked){
        if(checked){
            imageView.setVisibility(View.VISIBLE);
            textView.setTextColor(context.getResources().getColor(R.color.home_investment_top_button_selected));
        }else {
            imageView.setVisibility(View.INVISIBLE);
            textView.setTextColor(context.getResources().getColor(R.color.home_investment_top_button_unselected));
        }
    }
}
