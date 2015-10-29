package com.dragoneye.wjjt.config;

/**
 * Created by happysky on 15-6-24.
 */
public interface PreferencesConfig {
    /**
     * 下拉刷新上次更新时间的字符串常量，用于作为SharedPreferences的键值
     */
    String REFRESH_VIEW_UPDATE_AT = "REFRESH_VIEW_UPDATE_AT";

    int FRAGMENT_HOME_INVESTMENT_NORMAL = 1;
    int FRAGMENT_HOME_RECORD_INVEST = 2;
    int FRAGMENT_HOME_RECORD_EARNING = 3;
    int FRAGMENT_HOME_ENTREPRENEUR_RECORD = 4;
    int FRAGMENT_HOME_INVESTMENT_PREFERENTIAL = 5;



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
     * 记录用户登录信息是否过期
     */
    String IS_USER_LOGIN_DATA_OUT_OF_DATE = "IS_USER_LOGIN_DATA_OUT_OF_DATE";

    /**
     *  记录最后一次的用户登录数据
     */
    String LAST_LOGIN_USER_DATA = "LAST_LOGIN_USER_DATA";


    /**
     *  记录是否有未读的收益消息
     */
    String IS_HAVE_NEW_EARNING_MESSAGE = "IS_HAVE_NEW_EARNING_MESSAGE";

    /**
     * 记录是否有未读的消息盒子消息
     */
    String IS_HAVE_NEW_MESSAGE_BOX_MESSAGE = "IS_HAVE_NEW_MESSAGE_BOX_MESSAGE";


    String SHARESDKAPPKEY = "98bbea40c2d7";
    String SHARESDKAPPSECRET = "79b44d7eba1b2d29fc660b450a004e12";
}
