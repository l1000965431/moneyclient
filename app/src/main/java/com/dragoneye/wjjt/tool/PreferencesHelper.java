package com.dragoneye.wjjt.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dragoneye.wjjt.config.PreferencesConfig;

/**
 * Created by happysky on 15-8-25.
 */
public class PreferencesHelper {
    public static void setIsHaveEarningMessage(Context context, boolean b){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PreferencesConfig.IS_HAVE_NEW_EARNING_MESSAGE, b).apply();
    }

    public static boolean isHaveEarningMessage(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferencesConfig.IS_HAVE_NEW_EARNING_MESSAGE, false);
    }

    public static void setIsHaveNewMessageBoxMessage(Context context, boolean b){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PreferencesConfig.IS_HAVE_NEW_MESSAGE_BOX_MESSAGE, b).apply();
    }

    public static boolean isHaveNewMessageBoxMessage(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferencesConfig.IS_HAVE_NEW_MESSAGE_BOX_MESSAGE, false);
    }
}
