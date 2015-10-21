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
import java.util.Objects;
import java.util.UUID;

/**
 * Created by liumin on 15/7/27.
 */

@Controller
@RequestMapping("/PurchaseInAdvance")
public class PurchaseInAdvanceController extends ControllerBase implements IController {

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
    //-1:重新登录 1:期或票不够 2:钱不够 3:本期不够 预购后边的期 100:支付成功 103:支付成功客户端需要刷新 MessageType:1:判断本期 2:不判断本期
    public int PurchaseActivity(HttpServletRequest request) {
        final String UserID = request.getParameter("UserID");
        final String token = request.getParameter("token");
        final String InstallmentActivityID = request.getParameter("InstallmentActivityID");
        final int PurchaseType = Integer.valueOf(request.getParameter("PurchaseType"));
        final int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        final int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));
        final int MessageType = Integer.valueOf(request.getParameter("MessageType"));
        final int VirtualSecurities = Integer.valueOf( request.getParameter("VirtualSecurities") );
        final int[] Refresh = {0};

        if( !this.UserIsLand( UserID,token ) ){
            return Config.LANDFAILED;
        }


        if (!userService.IsPerfectInfo(UserID)) {
            return ServerReturnValue.PERFECTINFO;
        } else {
            //项目检查

            if( VirtualSecurities > 0 && VirtualSecurities < Config.MinVirtualSecuritiesBuy ){
                return ServerReturnValue.MINVIRTUALSECURITIESBUY;
            }

            final int[] state = {0};
            if (!Objects.equals(activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
                public boolean callback(Session session) throws Exception {
                    ActivityDetailModel activityDetailModel = activityInfoDAO.getActivityDetaillNoTransaction(InstallmentActivityID);
                    ActivityDynamicModel activityDynamicModel = activityDetailModel.getDynamicModel();
                    ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                    String ActivityID = activityVerifyCompleteModel.getActivityId();

                    int costLines = 0;

                    switch (PurchaseType) {
                        case Config.PURCHASEPRICKSILK:
                            int remainingNum = purchaseInAdvance.getInstallmentActivityRemainingTicket(InstallmentActivityID);
                            costLines = PurchaseNum * AdvanceNum;
                            if (remainingNum == PurchaseNum) {
                                Refresh[0] = 1;
                            }
                            if (remainingNum < PurchaseNum && MessageType == 1) {
                                state[0] = 3;
                                if (!purchaseInAdvance.IsRemainingInstallment(ActivityID, AdvanceNum) ||
                                        activityVerifyCompleteModel.IsEnoughLines(costLines + remainingNum)) {
                                    state[0] = 1;
                                }
                                return false;
                            } else {
                                state[0] = 0;
                                if (!purchaseInAdvance.IsRemainingInstallment(ActivityID, AdvanceNum) ||
                                        activityVerifyCompleteModel.IsEnoughLines(costLines)) {
                                    state[0] = 1;
                                    return false;
                                }
                                break;
                            }
                        case Config.PURCHASELOCALTYRANTS:
                            costLines = activityDynamicModel.getActivityTotalLinesPeoples() * AdvanceNum;
                            if (MessageType == 1 && !purchaseInAdvance.IsEnoughLocalTyrantsTickets(InstallmentActivityID)) {
                                state[0] = 3;
                                if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                                        activityVerifyCompleteModel.IsEnoughLinePoples(costLines + activityDynamicModel.getActivityTotalLinesPeoples())) {
                                    state[0] = 1;
                                }
                                return false;
                            } else {
                                if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                                        activityVerifyCompleteModel.IsEnoughLinePoples(costLines)) {
                                    state[0] = 1;
                                    return false;
                                }
                            }

                            Refresh[0] = 1;
                            break;
                    }

                    if (!walletService.IsWalletEnough(UserID, costLines) ||
                            walletService.IsvirtualSecuritiesEnough( UserID,VirtualSecurities ) ) {
                        state[0] = 2;
                        return false;
                    }


                    return true;
                }
            }), Config.SERVICE_SUCCESS)) {
                return state[0];
            }

            Map<String, Object> map = new HashMap();
            map.put("InstallmentActivityID", InstallmentActivityID);
            map.put("PurchaseNum", PurchaseNum);
            map.put("AdvanceNum", AdvanceNum);
            map.put("UserID", UserID);
            map.put("PurchaseType", PurchaseType);
            map.put("OrderID", UUID.randomUUID().toString());
            map.put("VirtualSecurities", VirtualSecurities);
            String messageBody = GsonUntil.JavaClassToJson(map);

            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_ACTIVITYBUY_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_ACTIVITYBUY_TAG, messageBody, UserID));

            if( Refresh[0] == 1 ){
                return ServerReturnValue.PERFECTREFRESH;
            }else{
                return ServerReturnValue.PERFECTSUCCESS;
            }
        }
    }

    @RequestMapping("/PurchaseActivityNum")
    @ResponseBody
    public String PurchaseActivityNum(HttpServletRequest request) {
        final String InstallmentActivityID = request.getParameter("installmentActivityID");
        return activityService.GetInstaInstallmentActivityInfo(InstallmentActivityID);
    }

}
