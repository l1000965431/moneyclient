package com.dragoneye.money.tool;

import com.google.gson.Gson;

/**
 * Created by happysky on 15-6-19.
 */
public class ToolMaster {
    private static Gson gson = new Gson();
    public static Gson gsonInstance(){
        return gson;
    }
}
