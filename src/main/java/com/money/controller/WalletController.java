package com.money.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.money.Service.Wallet.WalletService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.model.UserModel;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Webhooks;
import com.sun.corba.se.spi.activation.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.PingPlus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by happysky on 15-8-1.
 * 钱包接口
 */
@Controller
@RequestMapping("/Wallet")
public class WalletController {
    @Autowired
    WalletService walletService;


    @Autowired
    UserService userService;

    /**
     * 获取钱包余额
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getWalletBalance")
    @ResponseBody
    public int getWalletBalance(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        return walletService.getWalletLines(userId);
    }

    /**
     * 钱包充值
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/RechargeWallet")
    @ResponseBody
    public String RechargeWallet(HttpServletRequest request, HttpServletResponse response) {

        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = request.getReader();
            String string;
            while ((string = reader.readLine()) != null) {
                buffer.append(string);
            }
            reader.close();
        } catch (IOException e) {
           return null;
        }
        String json = buffer.toString();

        Map<String,Object> mapJson = GsonUntil.jsonToJavaClass( json,new TypeToken<Map<String,Object>>(){}.getType());


        Map<String,Object> MapExtra = (Map<String,Object>)mapJson.get("extras");
        String UserID = (String)MapExtra.get( "UserId" );
        UserModel userModel = userService.getUserInfo(UserID);
        if (userModel == null ) {
             return null;
        }
        double Lines = (Double)mapJson.get("amount");
        if( Lines <= 0 ){
            return null;
        }

        String ChannelID = (String)mapJson.get("channel");
        String order_no = (String)mapJson.get("order_no");
        return PingPlus.CreateChargeParams(UserID,(int)Lines,ChannelID, "", "充值", "null",order_no);
    }

    /**
     * ping++ 充值的回掉函数
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/Webhooks")
    @ResponseBody
    public String Webhooks(HttpServletRequest request, HttpServletResponse response) {
        try {
            PingPlus.Webhooks(request, response);
        } catch (IOException e) {
            return null;
        }
        return Config.SERVICE_SUCCESS;
    }
}
