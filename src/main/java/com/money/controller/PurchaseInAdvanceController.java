package com.money.controller;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.Wallet.WalletService;
import com.money.Service.activity.ActivityService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.config.ServerReturnValue;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import org.hibernate.Session;
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

    @Autowired
    activityDAO activityInfoDAO;

    @Autowired
    WalletService walletService;

    @Autowired
    ActivityService activityService;

    @RequestMapping("/PurchaseTest")
    @ResponseBody
    public void Test() {
        PurchaseInAdvance purchaseInAdvance = ServiceFactory.getService("PurchaseInAdvance");

    }

    @RequestMapping("/PurchaseActivity")
    @ResponseBody
    //1:期或票不够 2:钱不够 100:支付成功
    public int PurchaseActivity(HttpServletRequest request, HttpServletResponse response) {
        final String UserID = request.getParameter("UserID");
        final String InstallmentActivityID = request.getParameter("InstallmentActivityID");
        final int PurchaseType = Integer.valueOf(request.getParameter("PurchaseType"));
        final int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        final int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));

        if (!userService.IsPerfectInfo(UserID)) {
            return ServerReturnValue.PERFECTINFO;
        } else {
            //项目检查
            final int[] state = {0};
            if (activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
                public boolean callback(Session session) throws Exception {
                    ActivityDetailModel activityDetailModel = activityInfoDAO.getActivityDetaillNoTransaction(InstallmentActivityID);
                    ActivityDynamicModel activityDynamicModel = activityDetailModel.getDynamicModel();
                    ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                    String ActivityID = activityDetailModel.getActivityVerifyCompleteModel().getActivityId();

                    int costLines = 0;

                    switch ( PurchaseType ){
                        case Config.PURCHASEPRICKSILK:
                            int remainingNum = purchaseInAdvance.getInstallmentActivityRemainingTicket(InstallmentActivityID);

                            if (remainingNum == 0) {
                                state[0] = 1;
                                return false;
                            }

                            int tempPurchaseNum = remainingNum < PurchaseNum ? remainingNum : PurchaseNum;
                            costLines = tempPurchaseNum + (PurchaseNum * (AdvanceNum - 1));
                            if (!purchaseInAdvance.IsRemainingInstallment(ActivityID, AdvanceNum) ||
                                    activityVerifyCompleteModel.IsEnoughLines(costLines)) {
                                state[0] = 1;
                                return false;
                            }
                            break;
                        case Config.PURCHASELOCALTYRANTS:
                            int Lines = activityDynamicModel.getActivityTotalLinesPeoples() * AdvanceNum;

                            if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                                    activityVerifyCompleteModel.IsEnoughLinePoples(Lines) ) {
                                state[0] = 1;
                                return false;
                            }
                            break;
                    }

                    if( !walletService.IsWalletEnough( UserID,costLines ) ){
                        state[0] = 2;
                        return false;
                    }
                    state[0] = 0;
                    return true;
                }
            })!=Config.SERVICE_SUCCESS){
                return state[0];
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("InstallmentActivityID", InstallmentActivityID);
            map.put("PurchaseNum", PurchaseNum);
            map.put("AdvanceNum", AdvanceNum);
            map.put("UserID", UserID);
            map.put("PurchaseType", PurchaseType);
            String messageBody = GsonUntil.JavaClassToJson(map);


            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_ACTIVITYBUY_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_ACTIVITYBUY_TAG, messageBody, "1"));

            return ServerReturnValue.PERFECTSUCCESS;
        }
    }

    @RequestMapping("/PurchaseActivityNum")
    @ResponseBody
    public String PurchaseActivityNum( HttpServletRequest request, HttpServletResponse response ){
        final String InstallmentActivityID = request.getParameter("installmentActivityID");
        return activityService.GetInstaInstallmentActivityInfo( InstallmentActivityID );
    }

}
