package com.dragoneye.wjjt.config;

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

    /**
     * 记录是否显示过特性页
     */
    String IS_SHOWED_FEATURE = "IS_SHOWED_FEATURE";

    /**
     *  记录最后一次的用户登录数据
     */
    String LAST_LOGIN_USER_DATA = "LAST_LOGIN_USER_DATA";

    final String SHARESDKAPPKEY = "98bbea40c2d7";
    final String SHARESDKAPPSECRET = "79b44d7eba1b2d29fc660b450a004e12";
}
