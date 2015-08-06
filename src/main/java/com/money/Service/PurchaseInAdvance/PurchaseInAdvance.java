package com.money.Service.PurchaseInAdvance;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.Wallet.WalletService;
import com.money.Service.activity.ActivityService;
import com.money.Service.order.OrderService;
import com.money.config.Config;
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
    public int PurchaseInAdvance(final String InstallmentActivityID, final String UserID, final int PurchaseNum, final int AdvanceNum) {

        activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDetailModel activityDetailModel = activityInfoDAO.getActivityDetaillNoTransaction(InstallmentActivityID);
                String ActivityID = activityDetailModel.getActivityVerifyCompleteModel().getActivityId();

                if (!IsRemainingInstallment(ActivityID, AdvanceNum) || !IsRemainingTickets(ActivityID, PurchaseNum * AdvanceNum)) {
                    return false;
                }

                int remainingNum = getInstallmentActivityRemainingTicket(InstallmentActivityID);

                if (remainingNum == 0) {
                    return false;
                }

                int tempPurchaseNum = remainingNum < PurchaseNum ? remainingNum : PurchaseNum;

                //改为同一个事务
                if (!walletService.CostLines(UserID, (tempPurchaseNum + (PurchaseNum * AdvanceNum - 1)))) {
                    return false;
                }


                orderService.createOrder( UserID,InstallmentActivityID,AdvanceNum*PurchaseNum,PurchaseNum,AdvanceNum );

                //购买当前期
                PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum);

                //减去购买当前期的次数  才是预购的次数
                int PurchaseInAdvanceNum = AdvanceNum - 1;

                if (PurchaseInAdvanceNum > 0) {
                    purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, PurchaseNum, PurchaseInAdvanceNum, Config.PURCHASEPRICKSILK);
                }

                return true;
            }
        });

        return 0;
    }

    /**
     * 单次购买项目购买
     *
     * @param InstallmentActivityID 项目ID
     * @param UserID
     * @param PurchaseNum           购买票的数量
     * @return
     */

    public int PurchaseActivity(String InstallmentActivityID, String UserID, int PurchaseNum) throws Exception {
        if (!IsRemainingTickets(InstallmentActivityID, PurchaseNum)) {
            return 0;
        }

        purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum, Config.PURCHASEPRICKSILK);
        return 0;
    }

    /**
     * 领投单次购买
     *
     * @param InstallmentActivityID
     * @param UserID
     * @return
     */
    public int LocalTyrantsPurchase(String InstallmentActivityID, String UserID) throws Exception {
        purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, 1, Config.PURCHASELOCALTYRANTS);
        return 0;
    }

    /**
     * 批量购买
     *
     * @param list 购买人员列表
     * @return
     */
    public int BatchPurchaseActivity(final List<PurchaseInAdvanceModel> list, final String InstallmentActivityID, final String ActivityID) {
        if (list == null) {
            return 0;
        }

        activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                for (PurchaseInAdvanceModel it : list) {

                    int PurchaseNum = it.getPurchaseNum();
                    int CurPurchaseActivityNum = it.getCurPurchaseInAdvanceNum();
                    //单词购买
                    PurchaseActivity(InstallmentActivityID, it.getUserID(), PurchaseNum);

                    //刷新预购次数
                    CurPurchaseActivityNum++;
                    purchaseInAdvanceDAO.UpdatePurchaseActivityNum(it.getUserID(), ActivityID, CurPurchaseActivityNum);
                }

                return true;
            }
        });

        return 0;
    }


    /**
     * 从预购列表里购买当前期
     *
     * @param ActivityID            父项目ID
     * @param InstallmentActivityID 分期项目ID
     * @return
     */
    public int PurchaseActivityFromPurchaseInAdvance(String ActivityID, String InstallmentActivityID) {
        int page = 0;
        while (true) {
            List<PurchaseInAdvanceModel> list = purchaseInAdvanceDAO.PurchaseInAdvanceCompelete(ActivityID, 1000, page);
            if (list == null || list.size() == 0) {
                return 0;
            }

            BatchPurchaseActivity(list, InstallmentActivityID, ActivityID);
            page++;
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
        return activityVerifyCompleteModel.IsEnouthFund(PurchaseNum);
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
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityInfoDAO.getActivityVerifyCompleteModel(ActivityID);
        return activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum);
    }

    /**
     * 领投是否还有剩余的票数
     *
     * @param InstallmentActivityID 分期项目ID
     * @return
     */
    public boolean IsRemainingLocalTyrantsTickets(String InstallmentActivityID) {
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
    public int LocalTyrantsPurchaseActivity(final String InstallmentActivityID, final String UserID, final int AdvanceNum) {

        activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModel(InstallmentActivityID);
                String ActivityID = activityDynamicModel.getActivityVerifyCompleteModel().getActivityId();

                if (!IsRemainingLocalTyrantsInstallment(InstallmentActivityID, AdvanceNum) || !IsRemainingLocalTyrantsTickets(InstallmentActivityID)) {
                    return false;
                }

                int Lines = activityDynamicModel.getActivityTotalLinesPeoples() * AdvanceNum;

                //钱包花费
                if (!walletService.CostLines(UserID,Lines )) {
                    return false;
                }

                orderService.createOrder( UserID,InstallmentActivityID,Lines,1,AdvanceNum );

                LocalTyrantsPurchase(InstallmentActivityID, UserID);
                int TempAdvanceNum = AdvanceNum - 1;
                if (TempAdvanceNum > 0) {
                    purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, 1, AdvanceNum, Config.PURCHASELOCALTYRANTS);
                }

                return true;
            }
        });

        return 0;
    }


}
