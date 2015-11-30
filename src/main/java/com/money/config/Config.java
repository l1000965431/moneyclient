package com.money.config;

/**
 * Created by liumin on 15/7/8.
 */
public class Config {

    public static final String MESSAGE_SEND_SUCCESS = "SUCCESS";

    public static final String MESSAGE_SEND_FAILED = "FAILED";

    public static final String SERVICE_SUCCESS = "SUCCESS";

    public static final String SERVICE_FAILED = "FAILED";

    /**
     * 测试参数
     */
    public static final String MESSAFE_PRODUCERID = "PID_MoneySever_1";

    public static final String MESSAFE_CONSUMERID = "CID_MoneySever_1";


    /**
     * 生产参数
     */
/*    public static final String MESSAFE_PRODUCERID = "PID_MoneySever_2";

    public static final String MESSAFE_CONSUMERID = "CID_MoneySever_2";*/

    public static final String MESSAFE_ACCESSKEY = "lAj7wlmAwQF8l3o7";

    public static final String MESSAFE_SECRETKEY = "2f3BFhlGVSh6I3LJDIIL1D1t5YVDlW";
    //缓存失效时间，单位为秒
    public static final int FAILUER_TIME = 1800;

    public static final int MODIFYINFO_SUCCESS = 1;

    public static final int MODIFYINFO_FAILED = 2;

    public static final int SENDCODE_SUCESS = 1;

    public static final int SENDCODE_FAILED = 0;

    public static final int TOKENLAND_SUCESS = 2;

    public static final int TOKENLAND_FAILED = 0;

    public static final int USER_IS_REGISTER = 2;

    public static final int PASSWORD_ILLEGAL = 3;

    public static final int ALREADLAND = 1;

    public static final int USEPASSWORD = 0;

    public static final int NOT_LAND = 0;

    public static final int TOKEN_FAILED = 3;

    public static final int USERTYPE_FAILED = 4;

    //修改密码发送验证码 3,密码不正确;2,新密码不合法；2短信未发送成功；3成功
    public static final int PASSWORD_NOTRIGHT = 3;

    public static final int NEWPASSWORD_FAILED = 2;

    //单表最大行数
    public static final int MAXDBROWNUM = 800000;

    public static final int RETURNERROR = -1;

    //几率的放大倍数  方便计算
    public static final int PROBABILITYAMPLIFICATION = 1000000;

    //项目票的辅助表名称前缀
    public static final String ACTIVITYGROUPTICKETNAME = "Auto_Ticket_";

    //项目预售的辅助表名称前缀
    public static final String ACTIVITYPURCHASE = "Auto_Purchase_";

    //项目预售的辅助表名称前缀
    public static final String CODE = "Code";

    //url超时时间
    public static final int URLTIMEOUT = 3000;

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

    /**
     * 特惠项目发奖
     */
    public static final int PURCHASEPREFERENTIAL = 3;

    /**
     * 短信验证码的超时时间
     */
    public static final int USERCODETIME = 120;

    public static final int FINDPAGENUM = 1000;

    /**
     * ping++ 测试key
     */
    public static final String PINGPLUSTESTID = "sk_test_aD4qnHSWTCmPvX1un5rXfjPK";

    /**
     * ping++ 生产key
     */
    public static final String PINGPLUSLIVEID = "sk_live_tAeZAe9UXtT3p3UVcm4ZbcgD";

    public static final String PINGPLUSLAPPID = "app_jvXfzPe9e90GeLWz";

    /**
     * 友盟推送的参数
     */
    public static final String UMENGAPPKEY = "jsyt1miaqkdj1upwujmbbixz1r5uejnj";

    //微信参数
    public static final String WXAPPID = "wx287d8a1f932dc864";

    public static final String WXAPPSECRET = "5e39c31e9e69105b90184db19c05b6e4";

    /**
     * 验证登录失败
     */
    public static final int LANDFAILED = -1;

    public static final String STRLANDFAILED = "LANDFAILED";

    /**
     * 特惠项目
     */

    //未发的红包队列
    public static final String PREFERENTIUNBLLLED = "PreferentialUnbilled_";

    //已发的红包队列
    public static final String PREFERENTIBLLLED = "PreferentialBilled_";

    //特惠项目信息
    public static final String PREFERENTIINFO= "PreferentialInfo_";

    //特惠项目奖金总数
    public static final String PREFERENTIBOUNDS= "PreferentialBonus_";

    //填写邀请ID时给邀请人的经验值
    public static int AddExpInvite = 0;

    //填写邀请ID时给邀请人的微卷值
    public static int AddVirtualSecuritiesInvite = 0;

    //项目购买时增加的经验值
    public static int AddExpPurchase = 0;

    //填写邀请ID时给自己增加的微卷值
    public static int AddVirtualSecuritiesSelf = 0;

    //个人拥有的微劵最大值
    public static int MaxVirtualSecurities = 0;

    //单次购买使用微劵的最小值
    public static int MaxVirtualSecuritiesBuy = 0;

    //红点的消息提示:新普通项目项目
    public static String RedPointNewActivity = "RedPointNewActivity";

    //红点的消息提示:新特惠项目
    public static String RedPointNewActivityPreferential = "RedPointNewActivityPreferential";

    //红点的消息提示:新收益提示
    public static String RedPointNewLottery = "RedPointNewLottery";

    //红点的消息提示:新投资项目
    public static String RedPointNewJoinActivity = "RedPointNewJoinActivity";
}


