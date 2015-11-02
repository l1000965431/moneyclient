package com.money.controller;

import com.google.gson.reflect.TypeToken;
import com.money.Service.Wallet.WalletService;
import com.money.Service.alipay.AlipayService;
import com.money.Service.alipay.util.AlipayNotify;
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
import until.MoneyServerOrderID;
import until.PingPlus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * @return
     */
    @RequestMapping("/getWalletBalance")
    @ResponseBody
    public int getWalletBalance(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");

        if (!this.UserIsLand(userId, token)) {
            return Config.LANDFAILED;
        }

        return walletService.getWalletLines(userId);
    }

    /**
     * 钱包充值
     *
     * @param request
     * @return
     */
    @RequestMapping("/RechargeWallet")
    @ResponseBody
    public String RechargeWallet(HttpServletRequest request) {

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
     * @return
     */
    @RequestMapping("/IsBinding")
    @ResponseBody
    public boolean IsBinding(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        if (null == userId) {
            return false;
        }

        return userService.IsBinding(userId);
    }


    @RequestMapping("/IsalipayBinding")
    @ResponseBody
    public boolean IsalipayBinding(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        if (userId == null) {
            return false;
        }

        return userService.IsBindingalipayID(userId);
    }

    /**
     * 1:提现成功 0:提现错误 2:提现现金不足 3:没有绑定微信帐号 4:密码不正确
     *
     * @param request
     * @return
     */
    @RequestMapping("/TransferWallet")
    @ResponseBody
    public int TransferWallet(HttpServletRequest request) {
        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return 0;
        }


        String userId = mapData.get("userId");
        String orderId = MoneyServerOrderID.GetOrderID(userId);
        String passWord = mapData.get("passWord");
        int Lines = Integer.valueOf(mapData.get("lines"));

        String openId = userService.getBindingOpenId(userId);
        if (userId == null || orderId == null || openId == null) {
            return 0;
        }

        if (!userService.IsBinding(userId)) {
            return 3;
        }
        if (!walletService.IsWalletEnoughTransaction(userId, Lines)) {
            return 2;
        }

        if (userService.checkPassWord(userId, passWord) == false) {
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
        } catch (Exception e) {
            return null;
        }
        return Config.SERVICE_SUCCESS;
    }

    /**
     * ping++ 提现回掉函数
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
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
        return Config.SERVICE_SUCCESS;
    }

    @RequestMapping("/TestRechargeWallet")
    @ResponseBody
    public void TestRechargeWallet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        int lines = Integer.valueOf(request.getParameter("lines"));

        walletService.TestRechargeWallet(userId, lines);
    }

    @RequestMapping("/GetAliTranserOrder")
    @ResponseBody
    public String GetAliTranserOrder(HttpServletRequest request, HttpServletResponse response) {

        List list = walletService.GetAliTranserOrder();

        if (list == null) {
            return "";
        } else {
            return GsonUntil.JavaClassToJson(list);
        }
    }

    @RequestMapping("/GetAliTranserInfo")
    @ResponseBody
    public String GetAliTranserInfo(HttpServletRequest request, HttpServletResponse response) {
        int page = Integer.valueOf(request.getParameter("page"));
        List list = walletService.GetAliTranserInfo(page);

        if (list == null) {
            return "";
        } else {
            return GsonUntil.JavaClassToJson(list);
        }
    }

    /**
     * 测试提款
     */
    @RequestMapping("/TestTransaction")
    @ResponseBody
    public String TestTransaction(HttpServletRequest request) {
/*        TransactionData transactionData = new TransactionData();
        List<TransactionData> dataList = new ArrayList<TransactionData>();
        transactionData.setAccountId("l1000965431@126.com");
        transactionData.setAccountName("刘旻");
        transactionData.setComment("测试测试");
        transactionData.setPrice(0.01f);
        transactionData.setSerialNumber(String.valueOf(System.currentTimeMillis()));
        dataList.add(transactionData);*/

        int page = Integer.valueOf(request.getParameter("page"));
        List dataList = walletService.GetAliTranserInfo(page);

        return alipayService.requestTransaction(dataList);
    }

    /**
     * 提款结果
     */
    @RequestMapping("/TransactionResult")
    @ResponseBody
    public String TransactionResult(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String,String> map = AlipayNotify.getalipayInfo(request);
        if( !AlipayNotify.verify( map )){
            return "fail";
        }

        if(Objects.equals(walletService.alipayTransferNotify(map), Config.MESSAGE_SEND_SUCCESS)){
            return "success";
        }else {
            return "fail";
        }

/*        //批量付款数据中转账成功的详细信息
        String success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"),"UTF-8");
        //批量付款数据中转账失败的详细信息
        String fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"),"UTF-8");*/

    }

    /**
     * 绑定支付宝帐号
     *
     * @param request
     * @return
     */
    @RequestMapping("/BindingalipayId")
    @ResponseBody
    public String BindingalipayId(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String alipayId = request.getParameter("alipayId");
        String realName = request.getParameter( "realName" );

        alipayId = alipayId.replace( " ","" );
        realName = realName.replace( " ","" );
        return walletService.BindingalipayId( userId,alipayId,realName );
    }

    /**
     * 解除支付宝帐号绑定
     *
     * @param request
     * @return
     */
    @RequestMapping("/ClearalipayId")
    @ResponseBody
    public String ClearalipayId(HttpServletRequest request) {
        String userId = request.getParameter("userId");

        if( userId == null ){
            return Config.SERVICE_FAILED;
        }

        return walletService.ClearalipayId(userId);
    }

    /**
     * 支付宝提现
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/alipayTransfer")
    @ResponseBody
    //0:提现错误 1:提现申请已经提交 2:提现现金不足 3:没有绑定微信帐号 4:密码不正确
    public int alipayTransfer(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));
        if (mapData == null) {
            return 0;
        }

        String userId = mapData.get("userId");
        int Lines = Integer.valueOf(mapData.get("lines"));
        String passWord = mapData.get("passWord");

        if (!userService.checkPassWord(userId, passWord)) {
            return 4;
        }


        if( !userService.IsBindingalipayID(userId) ){
            return 3;
        }

        return walletService.alipayTransfer( userId,Lines );
    }


}
