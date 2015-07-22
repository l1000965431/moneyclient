package com.money.Service.user;

import until.MoneyServerMd5Utils;
import until.MoneySeverRandom;

/**
 * Created by fisher on 2015/7/11.
 */
public class Token
{
    public static String create(String userName)
    {
        //获取当前时间
        Long orderTime = System.currentTimeMillis();
        String time=orderTime.toString();
        int random = MoneySeverRandom.getRandomNum(1, 10000);
        String forToken=userName+time+random;
        String tokenData=MoneyServerMd5Utils.hash( forToken );
        return tokenData;
    }
}
