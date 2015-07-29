package com.money.config;

/**
 * 服务器返回错误代码 错误代码全局唯一
 * <p>User: 刘旻
 * <p>Date: 15-7-21
 * <p>Version: 1.0
 */

public class ServerReturnValue {

    /**
     * 结果错误
     */
    public static final int SERVERRETURNERROR= -1;

    /**
     * 结果正确
     */
    public static final int SERVERRETURNCOMPELETE = 0;

    /**
     * 用户登录失败
     */
    public static final int USERLANDERROR = 1;

    /**
     * 用户未登录
     */
    public static final int USERNOTLAND = 2;

    /**
     * 用户没有完善信息
     */
    public static final int USERNOTPERFECTINFO = 3;

    /**
     * 项目ID错误
     */
    public static final int ACTIVITYIDERROR = 4;

    /**
     * 项目状态错误
     */
    public static final int ACTIVITYSTATEERROR = 5;

    /**
     * 注册成功
     */
    public static final int REQISTEREDSUCCESS = 100;

    /**
     * 注册失败
     */
    public static final int REQISTEREDFAILED = 101;

    /**
     * 用户名重复
     */
    public static final int REQISTEREDUSERNAMEREPEAT = 102;

    /**
     * 用户名密码格式错误
     */
    public static final int REQISTEREDUSERNAMEERROR = 103;

    /**
     * 注册关闭
     */
    public static final int REQISTEREDUSERNAMESHUTDOWN = 104;


    /**
     * 验证码错误
     */
    public static final int REQISTEREDCODEERROR = 105;

    /**
     * 登录成功
     */
    public static final String LANDSUCCESS = "100";

    /**
     * 用户名或密码错误
     */
    public static final String LANDUSERERROR = "101";

    /**
     * 验证码错误
     */
    public static final String LANDCODEERROR = "102";

    /**
     * 登录失败
     */
    public static final String LANDFAILED = "103";

    /**
     * 完善信息
     */
    public static final int PERFECTINFO = 101;

    /**
     * 购买成功
     */
    public static final int PERFECTSUCCESS = 100;

    /**
     * 购买失败
     */
    public static final int PERFECTFAILED = 102;
}
