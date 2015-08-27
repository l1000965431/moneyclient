package com.money.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.money.Service.Wallet.WalletService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.model.UserModel;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.InvalidRequestException;
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
public class WalletController extends ControllerBase{
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

        String json = this.getrequestReader( request );

        if( json == null ){
            return null;
        }

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
     * 是否已经绑定微信帐号
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/IsBinding")
    @ResponseBody
    public boolean IsBinding( HttpServletRequest request, HttpServletResponse response ){
        String userId = request.getParameter( "userId" );
        if( userId == null ){
            return false;
        }
        String openId = userService.IsBinding( userId );
        if( openId == null ||  openId.equals( "0" ) ){
            return false;
        }

        return true;
    }




    /**
     * 1:提现成功 0:提现错误 2:提现现金不足 3:没有绑定微信帐号
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/TransferWallet")
    @ResponseBody
    public int TransferWallet( HttpServletRequest request, HttpServletResponse response ){
        String userId = request.getParameter( "userId" );
        String orderId = request.getParameter( "orderId" );
        int Lines = Integer.valueOf(request.getParameter("lines"));
        String openId = userService.IsBinding( userId );
        if( userId == null || orderId == null || openId == null ){
            return 0;
        }

        if( openId.equals( "0" ) ){
            return 3;
        }
        if( !walletService.IsWalletEnough( userId,Lines ) ){
            return 2;
        }


        try {
            PingPlus.CreateTransferMap( Lines,openId,orderId );
            return 1;
        } catch (APIException e) {
            return 0;
        } catch (AuthenticationException e) {
            return 0;
        } catch (InvalidRequestException e) {
            return 0;
        } catch (APIConnectionException e) {
            return 0;
        }
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
