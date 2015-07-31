package com.dragoneye.money.config;

/**
 * Created by happysky on 15-6-24.
 */
public interface PreferencesConfig {
    /**
     * 下拉刷新上次更新时间的字符串常量，用于作为SharedPreferences的键值
     */
    String REFRESH_VIEW_UPDATE_AT = "REFRESH_VIEW_UPDATE_AT";

    int FRAGMENT_HOME_INVESTMENT = 1;
    int FRAGMENT_HOME_RECORD = 2;


    /**
     *  记录上次登录的用户名和密码键值
     */
    String LAST_LOGIN_USER_ID = "LAST_LOGIN_USER_ID";
    String LAST_LOGIN_USER_PASSWORD = "LAST_LOGIN_USER_PASSWORD";
}
