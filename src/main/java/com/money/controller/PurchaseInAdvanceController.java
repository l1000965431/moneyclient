package com.money.controller;

import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public void Test(){
        PurchaseInAdvance purchaseInAdvance = ServiceFactory.getService("PurchaseInAdvance");
        if( purchaseInAdvance == null ){

        }else{
            purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance( "1","1" );
        }
    }

    @RequestMapping("/PurchaseActivity")
    @ResponseBody
    public int PurchaseActivity( HttpServletRequest request, HttpServletResponse response ) {
        String UserID = request.getParameter("UserID");
        String InstallmentActivityID = request.getParameter("InstallmentActivityID");
        int PurchaseType = Integer.valueOf(request.getParameter("PurchaseType"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));

        if (!userService.IsPerfectInfo(UserID)) {
            return ServerReturnValue.PERFECTINFO;
        } else {
            String ActivityID = "1";

            switch (PurchaseType) {
                case Config.PURCHASEPRICKSILK:
                    if (purchaseInAdvance.PurchaseInAdvance(InstallmentActivityID, ActivityID, UserID, PurchaseNum, AdvanceNum) == 0) {
                        return ServerReturnValue.PERFECTSUCCESS;
                    } else {
                        return ServerReturnValue.PERFECTFAILED;
                    }
                case Config.PURCHASELOCALTYRANTS:
                    if( purchaseInAdvance.LocalTyrantsPurchaseActivity( InstallmentActivityID,ActivityID,UserID,AdvanceNum ) == 0 ){
                        return ServerReturnValue.PERFECTSUCCESS;
                    } else {
                        return ServerReturnValue.PERFECTFAILED;
                    }
                default:
                    return 0;
            }
        }
    }
}
