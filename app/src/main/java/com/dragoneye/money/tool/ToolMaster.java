package com.dragoneye.money.tool;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by happysky on 15-6-19.
 */
public class ToolMaster {
    private static Gson gson = new Gson();
    public static Gson gsonInstance(){
        return gson;
    }

    /**
     * 格式化显示金额
     * @param price
     * @return
     */
    public static String convertToPriceString(int price){
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        return format.format(price);
    }
}
