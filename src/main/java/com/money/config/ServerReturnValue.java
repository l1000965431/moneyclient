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
}
