package com.dragoneye.money.tool;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by happysky on 15-6-23.
 */
public class UIHelper {
    public static void toast(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
