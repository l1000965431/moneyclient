package com.dragoneye.money.protocol;

import com.dragoneye.money.config.HttpUrlConfig;

/**
 * Created by happysky on 15-7-23.
 */
public interface UserProtocol {
    /**
     *  用户类型
     */
    int PROTOCOL_USER_TYPE_INVESTOR = 1;      // 投资者
    int PROTOCOL_USER_TYPE_ENTREPRENEUR = 2;  // 项目发布者

    /******************************************************************************************
     *  登录                                                                                  *
     ******************************************************************************************/
    String URL_LOGIN = HttpUrlConfig.URL_ROOT + "User/passWordLogin";

    String PASSWORD_LOGIN_PARAM_USER_ID = "userId";
    String PASSWORD_LOGIN_PARAM_USER_PASSWORD = "password";

    String PASSWORD_LOGIN_RESULT_KEY = "LoginResult";
    String PASSWORD_LOGIN_RESULT_INFO_KEY = "UserResponse";
    String PASSWORD_LOGIN_RESULT_SUCCESS = "100";
    String PASSWORD_LOGIN_RESULT_ERROR = "101";
    String PASSWORD_LOGIN_RESULT_CODE_ERROR = "102";
    String PASSWORD_LOGIN_RESULT_FAILED = "103";
    /*****************************************************************************************/




    /******************************************************************************************
     *  注册                                                                                   *
     ******************************************************************************************/
    String URL_REGISTER = HttpUrlConfig.URL_ROOT + "User/register";

    String REGISTER_RESULT_SUCCESS = "100";
    String REGISTER_RESULT_FAILED = "101";
    String REGISTER_RESULT_OCCUPIED = "102";
    String REGISTER_RESULT_FORMAT_INCORRECT = "103";
    String REGISTER_RESULT_CLOSED = "104";
    String REGISTER_RESULT_SECURITY_CODE_ERROR = "105";

    /**
     * 用户类型
     * @see UserProtocol#PROTOCOL_USER_TYPE_INVESTOR
     * @see UserProtocol#PROTOCOL_USER_TYPE_ENTREPRENEUR
     */
    String REGISTER_PARAM_USER_TYPE = "userType";

    String REGISTER_PARAM_USER_ID = "userId";
    String REGISTER_PARAM_USER_PASSWORD = "password";
    /*****************************************************************************************/


    /******************************************************************************************
     *  完善个人信息                                                                            *
     ******************************************************************************************/
    String URL_IMPROVE_USER_INFO = HttpUrlConfig.URL_ROOT + "User/perfectInfo";

    String IMPROVE_USER_INFO_PARAM_TOKEN = "token";
    String IMPROVE_USER_INFO_PARAM_INFO = "info";
    String IMPROVE_USER_INFO_PARAM_USER_TYPE = "userType";
    String IMPROVE_USER_INFO_PARAM_USER_ID = "userID";

    int IMPROVE_USER_INFO_RESULT_NEED_LOGIN = 0;            // 未登录
    int IMPROVE_USER_INFO_RESULT_SUCCESS = 1;                // 修改成功
    int IMPROVE_USER_INFO_RESULT_INFO_ERROR = 2;            // 信息不合法
    int IMPROVE_USER_INFO_RESULT_INFO_TOKEN_INCORRECT = 3;  // token不一致
    int IMPROVE_USER_INFO_RESULT_INFO_USER_TYPE_ERROR = 4;  // 用户类型错误
    /*****************************************************************************************/

    /******************************************************************************************
     *  发送注册验证码                                                                          *
     ******************************************************************************************/
    String URL_SEND_CODE = HttpUrlConfig.URL_ROOT + "User/SendUserCode";

    String SEND_CODE_PARAM_USER_ID = "userId";

    int SEND_CODE_RESULT_SUCCESS = 1;
    int SEND_CODE_RESULT_FAILED = 0;
    /*****************************************************************************************/

    /******************************************************************************************
     *  修改密码                                                                               *
     ******************************************************************************************/
    String URL_CHANGE_PASSWORD = HttpUrlConfig.URL_ROOT + "User/changPassword";

    String CHANGE_PASSWORD_PARAM_USER_ID = "userId";
    String CHANGE_PASSWORD_PARAM_OLD_PASSWORD = "oldPassword";
    String CHANGE_PASSWORD_PARAM_NEW_PASSWORD = "newPassword";
    String CHANGE_PASSWORD_PARAM_CODE = "code";

    int CHANGE_PASSWORD_RESULT_SUCCESS = 1;
    int CHANGE_PASSWORD_RESULT_FAILED = 0;
    int CHANGE_PASSWORD_RESULT_CODE_INCORRECT = 3;
    /*****************************************************************************************/

    /******************************************************************************************
     *  钱包余额                                                                               *
     ******************************************************************************************/
    String URL_GET_WALLET_BALANCE = HttpUrlConfig.URL_ROOT + "Wallet/getWalletBalance";


    /*****************************************************************************************/
}
