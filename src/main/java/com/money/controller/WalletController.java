package com.money.controller;

import com.google.gson.reflect.TypeToken;
import com.money.Service.Wallet.WalletService;
import com.money.Service.alipay.AlipayService;
import com.money.Service.alipay.TransactionData;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.model.UserModel;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.PingPlus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by happysky on 15-8-1.
 * 钱包接口
 */
@Controller
@RequestMapping("/Wallet")
public class WalletController extends ControllerBase {
    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    AlipayService alipayService;
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
        String token  = request.getParameter("token");

        if( !this.UserIsLand( userId,token ) ){
            return Config.LANDFAILED;
        }

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

        String json = this.getrequestReader(request);

        if (json == null) {
            return null;
        }

        Map<String, Object> mapJson = GsonUntil.jsonToJavaClass(json, new TypeToken<Map<String, Object>>() {
        }.getType());


        Map<String, Object> MapExtra = (Map<String, Object>) mapJson.get("extras");
        String UserID = (String) MapExtra.get("UserId");
        UserModel userModel = userService.getUserInfo(UserID);
        if (userModel == null) {
            return null;
        }
        double Lines = (Double) mapJson.get("amount");

        if (Lines <= 0) {
            return null;
        }

        String ChannelID = (String) mapJson.get("channel");
        String order_no = (String) mapJson.get("order_no");
        return PingPlus.CreateChargeParams(UserID, (int) Lines, ChannelID, "", "充值", "null", order_no);
    }


    /**
     * 是否已经绑定微信帐号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/IsBinding")
    @ResponseBody
    public boolean IsBinding(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        if (userId == null) {
            return false;
        }

        return userService.IsBinding(userId);
    }


    /**
     * 1:提现成功 0:提现错误 2:提现现金不足 3:没有绑定微信帐号 4:密码不正确
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/TransferWallet")
    @ResponseBody
    public int TransferWallet(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return 0;
        }


        String userId = mapData.get("userId");
        String orderId = mapData.get("orderId");
        String passWord = mapData.get("passWord");
        int Lines = Integer.valueOf(mapData.get("lines"));

        String openId = userService.getBindingOpenId(userId);
        if (userId == null || orderId == null || openId == null) {
            return 0;
        }

        if (userService.IsBinding(userId)) {
            return 3;
        }
        if (!walletService.IsWalletEnoughTransaction(userId, Lines)) {
            return 2;
        }

        if( userService.checkPassWord( userId,passWord ) == false ){
            return 4;
        }

        try {
            PingPlus.CreateTransferMap(Lines, openId, userId, orderId);
            return 1;
        } catch (UnsupportedEncodingException e) {
            return 0;
        } catch (InvalidRequestException e) {
            return 0;
        } catch (APIException e) {
            return 0;
        } catch (APIConnectionException e) {
            return 0;
        } catch (AuthenticationException e) {
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

    /**
     * ping++ 充值的回掉函数
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/TranferWebhooks")
    @ResponseBody
    public String TranferWebhooks(HttpServletRequest request, HttpServletResponse response) {
        try {
            PingPlus.Webhooks(request, response);
        } catch (IOException e) {
            return Config.SERVICE_FAILED;
        }
        return Config.SERVICE_SUCCESS;
    }

    @RequestMapping("/TestRechargeWallet")
    @ResponseBody
    public void TestRechargeWallet( HttpServletRequest request, HttpServletResponse response ) throws Exception {


        String userId = request.getParameter("userId");
        int lines = Integer.valueOf(request.getParameter("lines"));

        walletService.TestRechargeWallet( userId,lines );
    }

    /**
     *  测试提款
     */
    @RequestMapping("/TestTransaction")
    @ResponseBody
    public String TestTransaction( HttpServletRequest request, HttpServletResponse response ){
        TransactionData transactionData = new TransactionData();
        List<TransactionData> dataList = new ArrayList<TransactionData>();
        transactionData.setAccountId("l1000965431@126.com");
        transactionData.setAccountName("刘旻");
        transactionData.setComment("测试测试");
        transactionData.setPrice(0.01f);
        transactionData.setSerialNumber(String.valueOf(System.currentTimeMillis()));
        dataList.add(transactionData);

        return alipayService.requestTransaction(dataList);
    }

    /**
     *  提款结果
     */
    @RequestMapping("/TransactionResult")
    @ResponseBody
    public String TransactionResult( HttpServletRequest request, HttpServletResponse response ){
        int a = 0;
        return "success";
    }
}
