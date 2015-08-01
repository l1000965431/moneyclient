package com.money.controller;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.config.ServerReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumin on 15/7/27.
 */

@Controller
@RequestMapping("/PurchaseInAdvance")
public class PurchaseInAdvanceController extends ControllerBase implements IController {

    @Autowired
    UserService userService;

    @Autowired
    PurchaseInAdvance purchaseInAdvance;

    @RequestMapping("/PurchaseTest")
    @ResponseBody
    public void Test() {
        PurchaseInAdvance purchaseInAdvance = ServiceFactory.getService("PurchaseInAdvance");
        if (purchaseInAdvance == null) {

        } else {
            purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance("1", "1");
        }
    }

    @RequestMapping("/PurchaseActivity")
    @ResponseBody
    public int PurchaseActivity(HttpServletRequest request, HttpServletResponse response) {
        String UserID = request.getParameter("UserID");
        String InstallmentActivityID = request.getParameter("InstallmentActivityID");
        int PurchaseType = Integer.valueOf(request.getParameter("PurchaseType"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));

        if (!userService.IsPerfectInfo(UserID)) {
            return ServerReturnValue.PERFECTINFO;
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("InstallmentActivityID", InstallmentActivityID);
            map.put("PurchaseNum", PurchaseNum);
            map.put("AdvanceNum", AdvanceNum);
            map.put("UserID", UserID);
            map.put("PurchaseType", PurchaseType);
            String messageBody = GsonUntil.JavaClassToJson(map);
            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_ORDERINSERT_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_ORDERINSERT_TAG, messageBody, "1"));

            return ServerReturnValue.PERFECTSUCCESS;


        }
    }

}
