package com.dragoneye.money.protocol;

/**
 * Created by happysky on 15-6-23.
 *
 */
public interface ServerProtocol {
    String URL_ROOT = "http://192.168.0.182:8080/";

    /**
     * 登录
     */
    String URL_LOGIN = URL_ROOT + "ApkLongin/userlogin";

    int USER_ID_PASSWORD_INCORRECT = 104;

    /**
     *  注册
     */
    String URL_REGISTER = URL_ROOT + "ApkLongin/registered";

    int REGISTER_RESULT_FORMAT_INCORRECT = 106;
    int REGISTER_RESULT_OCCUPIED = 105;
    int REGISTER_RESULT_SUCCESS = 1;

    String REGISTER_PARAM_USER_ID = "username";
    String REGISTER_PARAM_USER_PASSWORD = "userpwd";

    /**
     *  获取项目列表
     */
    String URL_GET_PROJECT_LIST = URL_ROOT + "ApkActivity/FindActivity";
}
