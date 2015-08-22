package com.money.Service.PurchaseInAdvance;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.Wallet.WalletService;
import com.money.Service.order.OrderService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.PurchaseInAdvanceDAO.PurchaseInAdvanceDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.PurchaseInAdvanceModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 项目预购和购买服务
 * <p>User: liumin
 * <p>Date: 15-7-27
 * <p>Version: 1.0
 */

@Service("PurchaseInAdvance")
public class PurchaseInAdvance extends ServiceBase implements ServiceInterface {

    @Autowired
    PurchaseInAdvanceDAO purchaseInAdvanceDAO;

    @Autowired
    activityDAO activityInfoDAO;

    @Autowired
    WalletService walletService;

    @Autowired
    private OrderService orderService;

    /**
     * 项目预购
     *
     * @param InstallmentActivityID 分期项目ID
     * @param UserID
     * @param PurchaseNum           单期购买的数量
     * @param AdvanceNum            预购期数
     */
    public int PurchaseInAdvance(final String InstallmentActivityID, final String UserID, final int PurchaseNum, final int AdvanceNum,final String OrderID) {
        if( AdvanceNum == 0 ){
            return 0;
        }

        if (activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDetailModel activityDetailModel = activityInfoDAO.getActivityDetaillNoTransaction(InstallmentActivityID);
                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                String ActivityID = activityDetailModel.getActivityVerifyCompleteModel().getActivityId();

                int remainingNum = getInstallmentActivityRemainingTicket(InstallmentActivityID);

                if (remainingNum == 0) {
                    return false;
                }

                int costLines = PurchaseNum * AdvanceNum;
                int PurchaseInAdvanceNum = 0;
                if( remainingNum < PurchaseNum ){
                    if (!IsRemainingInstallment(ActivityID, AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLines(costLines+remainingNum)) {
                        return false;
                    }
                    PurchaseInAdvanceNum = AdvanceNum;
                }else{
                    if (!IsRemainingInstallment(ActivityID, AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLines(costLines)) {
                        return false;
                    }
                    //购买当前期
                    int PurchaseResult = PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum,OrderID);
                    switch ( PurchaseResult ){
                        case ServerReturnValue.SERVERRETURNERROR:
                            return false;
                   /* case ServerReturnValue.SERVERRETURNCONDITIONS:
                        costLines = PurchaseNum * (AdvanceNum - 1);*/

                        //减去购买当前期的次数  才是预购的次数
                    }

                    PurchaseInAdvanceNum = AdvanceNum - 1;
                }

                if (PurchaseInAdvanceNum > 0) {
                    purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, PurchaseInAdvanceNum, PurchaseInAdvanceNum, Config.PURCHASEPRICKSILK,OrderID);
                }

                //刷新总购买额度
                int curLines = activityVerifyCompleteModel.getCurFund();
                curLines += costLines;
                activityVerifyCompleteModel.setCurFund(curLines);

                //刷新小R
                int curLines1 = activityVerifyCompleteModel.getCurLines();
                curLines1 += costLines;
                activityVerifyCompleteModel.setCurLines( curLines1 );

                if (!walletService.CostLines(UserID, costLines )) {
                    return false;
                }

                orderService.createOrder(UserID, InstallmentActivityID,PurchaseNum*AdvanceNum,PurchaseNum,AdvanceNum,Config.PURCHASEPRICKSILK,OrderID);

                return true;
            }
        })== Config.SERVICE_SUCCESS ){
            return 1;
        }
        return 0;
    }

    /**
     * 跟投单期购买项目
     *
     * @param InstallmentActivityID 项目ID
     * @param UserID
     * @param PurchaseNum           购买票的数量
     * @return
     */

    public int PurchaseActivity(String InstallmentActivityID, String UserID, int PurchaseNum,String OrderID) throws Exception {
        if (PurchaseNum > getInstallmentActivityRemainingTicket(InstallmentActivityID)) {
            return ServerReturnValue.SERVERRETURNERROR;
        }

        return purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum, Config.PURCHASEPRICKSILK);

    }

    /**
     * 领投单次购买
     *
     * @param InstallmentActivityID
     * @param UserID
     * @return
     */
    public int LocalTyrantsPurchase(String InstallmentActivityID, String UserID ,int TotalLinePeoples,String OrderID) throws Exception {
        if (!IsEnoughLocalTyrantsTickets(InstallmentActivityID)) {
            return ServerReturnValue.SERVERRETURNCONDITIONS;
        }
        purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, 1, Config.PURCHASELOCALTYRANTS);
        //orderService.createOrder(UserID, InstallmentActivityID, TotalLinePeoples, 0, 0, Config.PURCHASELOCALTYRANTS, OrderID);
        return ServerReturnValue.SERVERRETURNCOMPELETE;
    }

    /**
     * 批量购买
     *
     * @param list 购买人员列表
     * @return
     */
    public int BatchPurchaseActivity(final List<PurchaseInAdvanceModel> list, final String InstallmentActivityID, final String ActivityID) throws Exception {
        if (list == null) {
           return ServerReturnValue.SERVERRETURNERROR;
        }

        ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModelNoTransaction( InstallmentActivityID );

        for (PurchaseInAdvanceModel it : list) {
            if( activityDynamicModel.IsEnough() ){
                return ServerReturnValue.SERVERRETURNCOMPELETE;
            }

            int PurchaseNum = it.getPurchaseNum();
            //单词购买
            int Result = 0;
            switch ( it.getPurchaseType() ){
                case Config.PURCHASEPRICKSILK:
                    Result = this.PurchaseActivity(InstallmentActivityID, it.getUserID(), PurchaseNum, UUID.randomUUID().toString());
                    break;
                case Config.PURCHASELOCALTYRANTS:
                    Result = this.LocalTyrantsPurchase(InstallmentActivityID, it.getUserID(), PurchaseNum, UUID.randomUUID().toString());
                    break;
            }

            if( Result == ServerReturnValue.SERVERRETURNERROR ){
               continue;
            }

            //刷新预购次数
            int CurPurchaseActivityNum = it.getCurPurchaseInAdvanceNum();
            CurPurchaseActivityNum++;
            purchaseInAdvanceDAO.UpdatePurchaseActivityNum(it.getId(),it.getUserID(), ActivityID, CurPurchaseActivityNum);
        }

        return ServerReturnValue.SERVERRETURNCOMPELETE;
    }


    /**
     * 从预购列表里购买当前期
     *
     * @param ActivityID            父项目ID
     * @param InstallmentActivityID 分期项目ID
     * @return
     */
    public int PurchaseActivityFromPurchaseInAdvance(String ActivityID, String InstallmentActivityID) throws Exception {
        int page = 0;
        while (true) {
            List<PurchaseInAdvanceModel> list = purchaseInAdvanceDAO.PurchaseInAdvanceCompelete(ActivityID,Config.FINDPAGENUM,page);
            if (list == null || list.size() == 0) {
                return 0;
            }

            BatchPurchaseActivity(list, InstallmentActivityID, ActivityID);

            page+=Config.FINDPAGENUM;
        }
    }

    /**
     * 跟投是否还有剩余的期
     *
     * @param ActivityID
     * @param AdvanceNum 购买的期数
     * @return
     */
    public boolean IsRemainingInstallment(String ActivityID, int AdvanceNum) {
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityInfoDAO.getActivityVerifyCompleteModelNoTransaction(ActivityID);
        return activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum);
    }

    /**
     * 跟投是否还有剩余的票数
     *
     * @param ActivityID  项目ID
     * @param PurchaseNum 购买的票数
     * @return
     */
    public boolean IsRemainingTickets(String ActivityID, int PurchaseNum) {
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityInfoDAO.getActivityVerifyCompleteModelNoTransaction(ActivityID);
        return activityVerifyCompleteModel.IsEnoughFund(PurchaseNum);
    }

    /**
     * 获得分期项目剩余票数
     *
     * @param InstallmentActivityID
     * @return
     */
    public int getInstallmentActivityRemainingTicket(String InstallmentActivityID) {
        ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModelNoTransaction(InstallmentActivityID);
        return activityDynamicModel.getRemainingTicket();
    }

    /**
     * 领投是否还有剩余的期
     *
     * @param ActivityID
     * @param AdvanceNum 购买的期数
     * @return
     */
    public boolean IsRemainingLocalTyrantsInstallment(String ActivityID, int AdvanceNum) {
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityInfoDAO.getActivityVerifyCompleteModelNoTransaction(ActivityID);
        return activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum);
    }

    /**
     * 领投是否还有剩余的票数
     *
     * @param InstallmentActivityID 分期项目ID
     * @return
     */
    public boolean IsEnoughLocalTyrantsTickets(String InstallmentActivityID) {
        ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModelNoTransaction(InstallmentActivityID);
        return activityDynamicModel.IsEnoughLinesPeoples(activityDynamicModel.getActivityTotalLinesPeoples());
    }


    /**
     * 领投购买项目
     *
     * @param InstallmentActivityID 分期项目ID
     * @param UserID                用户ID
     * @param AdvanceNum            购买的期数
     * @return
     */
    public int LocalTyrantsPurchaseActivity(final String InstallmentActivityID, final String UserID, final int AdvanceNum,final String OrderID) {

        if( AdvanceNum == 0 ){
            return 0;
        }

        if( activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModelNoTransaction(InstallmentActivityID);
                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();
                String ActivityID = activityDynamicModel.getActivityVerifyCompleteModel().getActivityId();

                int Lines = activityDynamicModel.getActivityTotalLinesPeoples() * AdvanceNum;
                int TempAdvanceNum;
                if ( !IsEnoughLocalTyrantsTickets(InstallmentActivityID) ) {
                    if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLinePoples( Lines+activityDynamicModel.getActivityTotalLinesPeoples()) ) {
                        return false;
                    }
                    TempAdvanceNum = AdvanceNum;
                }else {
                    if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLinePoples(Lines)) {
                        return false;
                    }

                    int PurchaseResult = LocalTyrantsPurchase(InstallmentActivityID, UserID,activityDynamicModel.getActivityTotalLinesPeoples(),OrderID);
                    if (PurchaseResult == ServerReturnValue.SERVERRETURNERROR) {
                        return false;
                    }
                    TempAdvanceNum = AdvanceNum - 1;
                }

                if (TempAdvanceNum > 0) {
                    purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, activityDynamicModel.getActivityTotalLinesPeoples(), TempAdvanceNum, Config.PURCHASELOCALTYRANTS,OrderID);
                }

                //刷新总购买额度
                int curLines = activityVerifyCompleteModel.getCurFund();
                curLines += Lines;
                activityVerifyCompleteModel.setCurFund(curLines);
                //刷新大R
                int curLinePeoples = activityVerifyCompleteModel.getCurLinePeoples();
                curLinePeoples += Lines;
                activityVerifyCompleteModel.setCurLinePeoples( curLinePeoples );

                //钱包花费
                if (!walletService.CostLines(UserID, Lines)) {
                    return false;
                }

                orderService.createOrder(UserID, InstallmentActivityID, Lines, 0, AdvanceNum, Config.PURCHASELOCALTYRANTS, OrderID);
                return true;
            }
        })==Config.SERVICE_SUCCESS){
            return 1;
        }

        return 0;
    }


}
