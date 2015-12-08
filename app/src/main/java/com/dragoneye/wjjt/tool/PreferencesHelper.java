package com.dragoneye.wjjt.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dragoneye.wjjt.config.PreferencesConfig;

/**
 * Created by happysky on 15-8-25.
 */
public class PreferencesHelper {
    public static void setIsHaveNewMessage(Context context, boolean b, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(key, b).apply();
    }

    public static boolean isHaveNewMessage(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public static long getTimeCounterOriginMillis(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(key, 0);
    }

    public static void setTimeCounterOriginMillis(Context context, String key, Long sec){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putLong(key, sec).apply();
    }
}
