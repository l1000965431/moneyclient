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
    public static final int FAILUER_TIME=1800;

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

    //项目票的辅助表名称前缀
    public static final String ACTIVITYGROUPTICKETNAME = "Ticket_";

    //项目预售的辅助表名称前缀
    public static final String ACTIVITYPURCHASE = "Purchase_";

    /**
     * 项目状态
     */
    //尚未发布
    public static final int NOTYETRELEASE = 1;

    //第几期众筹中
    public static final int INSTALLMENTCRWWDFUNDING = 2;

    //第几期众筹完成
    public static final int INSTALLMENTCRWWDFUNDED = 3;

    //众筹成功
    public static final int CRWWDFUNDCOMPELETE = 4;

    //众筹失败
    public static final int CRWWDFUNDFAILED = 5;

    //众筹分红
    public static final int DIVIDEND = 6;

    /**
     * 分期项目池状态
     */
    //分期项目尚未开始
     public static final int NOINSTALLMENTACTIVITYSTART = 1;

    //分期项目进行中
    public static final int INSTALLMENTACTIVITYDOING = 2;

    //分期项目结束
    public static final int INSTALLMENTACTIVITYEND = 3;

    //投资者
    public static final int INVESTOR = 1;

    //发布者
    public static final int BORROWER = 2;

    /**
     * 土豪发奖
     */
    public static final int PURCHASELOCALTYRANTS = 1;

    /**
     * 屌丝发奖
     */
    public static final int PURCHASEPRICKSILK = 2;
}
