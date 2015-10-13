package com.dragoneye.wjjt.protocol;

import com.dragoneye.wjjt.config.HttpUrlConfig;

/**
 * Created by happysky on 15-7-28.
 * 获取项目列表
 */
public interface GetProjectListProtocol {
    /******************************************************************************************
     *  获取项目列表                                                                            *
     ******************************************************************************************/
    String URL_GET_PROJECT_LIST = HttpUrlConfig.URL_ROOT + "ActivityController/getActivityDetailsTest";


    /*****************************************************************************************/



    /******************************************************************************************
     *  获取投资过的项目列表                                                                     *
     ******************************************************************************************/
    String URL_GET_ORDER_LIST = HttpUrlConfig.URL_ROOT + "ActivityController/GetActivityHasInvestment";

    String GET_ORDER_PARAM_USER_ID = "userID";
    String GET_ORDER_PARAM_TOKEN = "token";
    String GET_ORDER_PARAM_PAGE_INDEX = "page";
    String GET_ORDER_PARAM_NUM_PER_PAGE = "findNum";

    /*****************************************************************************************/


    /******************************************************************************************
     *  获取项目详情                                                                            *
     ******************************************************************************************/
    String URL_GET_PROJECT_INFO = HttpUrlConfig.URL_ROOT + "ActivityController/GetActivityInformation";

    String GET_PROJECT_INFO_RESULT_KEY = "response";
    String GET_PROJECT_INFO_SUCCESS = "100";
    String GET_PROJECT_INFO_NO_PROJECT = "101";
    /*****************************************************************************************/
}
