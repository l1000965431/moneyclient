package com.dragoneye.wjjt.protocol;

import com.dragoneye.wjjt.config.HttpUrlConfig;

/**
 * Created by happysky on 15-7-28.
 * 投资项协议
 */
public interface InvestProjectProtocol {
    /**
     *  投资类型，1:领投 2:跟投
     */
    int INVEST_TYPE_LEAD = 1;
    int INVEST_TYPE_FALLOW = 2;

    /******************************************************************************************
     *  投资项目                                                                               *
     ******************************************************************************************/
    String URL_INVEST_PROJECT = HttpUrlConfig.URL_ROOT + "/PurchaseInAdvance/PurchaseActivity";

    /** 参数名称 **/
    String INVEST_PROJECT_PARAM_USER_ID = "UserID"; // 用户ID
    String INVEST_PROJECT_PARAM_ACTIVITY_STAGE_ID = "InstallmentActivityID"; // 分期项目ID
    String INVEST_PROJECT_PARAM_INVEST_STAGE_NUM = "AdvanceNum";    // 购买的期数
    String INVEST_PROJECT_PARAM_INVEST_PRICE_NUM = "PurchaseNum";   // 购买的票数
    String INVEST_PROJECT_PARAM_INVEST_TYPE = "PurchaseType";   // 投资类型

    /** 返回值 **/
    int INVEST_RESULT_SUCCESS = 100;   // 投资成功
    int INVEST_RESULT_IMPROVE_INFO = 101;     // 需完善个人信息
    int INVEST_RESULT_FAILED = 102;    // 投资失败
    int INVEST_RESULT_MONEY_NOT_ENOUGH = 2;
    int INVEST_RESULT_TICKET_SOLD_OUT = 1;

    /*****************************************************************************************/

    /******************************************************************************************
     *  投资项目                                                                               *
     ******************************************************************************************/
    String URL_GET_INVEST_INFO = HttpUrlConfig.URL_ROOT + "/ActivityController/getActivityInvestInfo";

    String GET_INVEST_INFO_PARAM_USER_ID = "UserId";
    String GET_INVEST_INFO_PARAM_ACTIVITY_STAGE_ID = "ActivityStageId";

    String GET_INVEST_INFO_SUCCESS = "100";
    String GET_INVEST_INFO_FAILED = "101";
    /*****************************************************************************************/
}
