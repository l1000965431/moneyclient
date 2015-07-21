package com.money.Service.user;

import until.MoneyServerMd5Utils;
import until.MoneySeverRandom;

/**
 * Created by fisher on 2015/7/11.
 */
public class Token
{
    static MoneyServerMd5Utils md5=new MoneyServerMd5Utils();
    public static String creat(String userName)
    {
        //获取当前时间
        Long orderTime = System.currentTimeMillis();
        String time=orderTime.toString();
        int random = MoneySeverRandom.getRandomNum(1, 10000);
        String forToken=userName+time+random;
        String tokenData=md5.hash( forToken );
        return tokenData;
    }
}
