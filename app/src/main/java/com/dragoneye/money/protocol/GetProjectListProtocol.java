package com.dragoneye.money.protocol;

import com.dragoneye.money.config.HttpUrlConfig;

/**
 * Created by happysky on 15-7-28.
 * 获取项目列表
 */
public interface GetProjectListProtocol {
    /******************************************************************************************
     *  获取项目列表                                                                            *
     ******************************************************************************************/
    String URL_GET_PROJECT_LIST = HttpUrlConfig.URL_ROOT + "ActivityController/getActivityDetails";


    /*****************************************************************************************/



    /******************************************************************************************
     *  获取投资过的项目列表                                                                     *
     ******************************************************************************************/
    String URL_GET_ORDER_LIST = HttpUrlConfig.URL_ROOT + "ActivityOrder/getOrderByUserID";

    String GET_ORDER_PARAM_USER_ID = "userID";
    String GET_ORDER_PARAM_TOKEN = "token";
    String GET_ORDER_PARAM_FIRST_PAGE = "firstPage";

    String GET_ORDER_RESULT_KEY = "response";
    String GET_ORDER_RESULT_SUCCESS = "100";
    String GET_ORDER_RESULT_NEED_LOGIN = "103";
    /*****************************************************************************************/
}
