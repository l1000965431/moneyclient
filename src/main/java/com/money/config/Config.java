package com.money.config;

/**
 * Created by liumin on 15/7/8.
 */
public class Config {

    public static final String MESSAGE_SEND_SUCCESS = "SUCCESS";

    public static final String MESSAGE_SEND_FAILED = "FAILED";

    public static final String SERVICE_SUCCESS = "SUCCESS";

    public static final String SERVICE_FAILED = "FAILED";

    public static final String MESSAFE_PRODUCERID = "PID_MoneySever_1";

    public static final String MESSAFE_CONSUMERID = "CID_MoneySever_1";

    public static final String MESSAFE_ACCESSKEY = "lAj7wlmAwQF8l3o7";

    public static final String MESSAFE_SECRETKEY = "2f3BFhlGVSh6I3LJDIIL1D1t5YVDlW";
    //缓存失效时间，单位为秒
    public static final int FAILUER_TIME=5*60;

    public static final int MODIFYINFO_SUCCESS =1;

    public static final int MODIFYINFO_FAILED=2;

    public static final int SENDCODE_SUCESS=1;

    public static final int SENDCODE_FAILED=0;

    public static final int TOKENLAND_SUCESS=2;

    public static final int TOKENLAND_FAILED=0;

    public static final int USER_IS_REGISTER=2;

    public static final int PASSWORD_ILLEGAL=3;

    public static final int ALREADLAND=1;

    public static final int USEPASSWORD=0;

    public static final int NOT_LAND=0;

    public static final int TOKEN_FAILED=3;

    public static final int USERTYPE_FAILED=4;
    //修改密码发送验证码 3,密码不正确;2,新密码不合法；2短信未发送成功；3成功
    public static final int PASSWORD_NOTRIGHT=3;

    public static final int NEWPASSWORD_FAILED=2;

    //单表最大行数
    public static final int MAXDBROWNUM = 800000;

    public static final int RETURNERROR = -1;

    //几率的放大倍数  方便计算
    public static final int PROBABILITYAMPLIFICATION = 1000000;

    //几率的放大倍数  方便计算
    public static final String ACTIVITYGROUPTICKETNAME = "Ticket_";
}
