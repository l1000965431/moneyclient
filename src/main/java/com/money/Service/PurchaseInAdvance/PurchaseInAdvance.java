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
import com.money.dao.userDAO.UserDAO;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.PurchaseInAdvanceModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    private UserDAO userDAO;

    /**
     * 项目预购
     *
     * @param InstallmentActivityID 分期项目ID
     * @param UserID
     * @param PurchaseNum           单期购买的数量
     * @param AdvanceNum            预购期数
     */
    public int PurchaseInAdvance(final String InstallmentActivityID, final String UserID, final int PurchaseNum,
                                 final int AdvanceNum, final String OrderID, final int VirtualSecurities,StringBuffer out_ActivityName) {
        final String[] ActivityName = new String[1];
        if (AdvanceNum == 0) {
            return 0;
        }

        if (Objects.equals(activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDetailModel activityDetailModel = activityInfoDAO.getActivityDetaillNoTransaction(InstallmentActivityID);
                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                String ActivityID = activityVerifyCompleteModel.getActivityId();
                int OrderStartAndvance;
                int remainingNum = getInstallmentActivityRemainingTicket(InstallmentActivityID);
                ActivityName[0] = activityVerifyCompleteModel.getName();
                if (remainingNum == 0) {
                    return false;
                }

                int costLines = PurchaseNum * AdvanceNum;
                int PurchaseInAdvanceNum;
                if (remainingNum < PurchaseNum) {
                    if (!IsRemainingInstallment(ActivityID, AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLines(costLines + remainingNum)) {
                        return false;
                    }
                    PurchaseInAdvanceNum = AdvanceNum;

                    if (activityDetailModel.getStageIndex() != activityVerifyCompleteModel.getTotalInstallmentNum()) {
                        int activityTotalLines = activityVerifyCompleteModel.getTotalLines();
                        int activityInstallmentNum = activityVerifyCompleteModel.getTotalInstallmentNum();
                        int activityCurLines = activityVerifyCompleteModel.getCurLines();

                        int temp = activityTotalLines / activityInstallmentNum;
                        //+2因为要计算从哪一期开始的预购  activityCurLines/temp+1代表进行到了哪期 从下一期开始 (activityCurLines/temp + 1）+ 1
                        if (activityCurLines / temp < activityInstallmentNum - 1) {
                            OrderStartAndvance = activityCurLines / temp + 2;
                        } else {
                            OrderStartAndvance = activityCurLines / temp + 1;
                        }

                    } else {
                        OrderStartAndvance = 1;
                    }

                } else {
                    if (!IsRemainingInstallment(ActivityID, AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLines(costLines)) {
                        return false;
                    }

                    if (!walletService.CostLines(UserID, costLines)) {
                        return false;
                    }


                    //购买当前期
                    int PurchaseResult = PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum, PurchaseNum);

                    switch (PurchaseResult) {
                        case ServerReturnValue.SERVERRETURNERROR:
                            return false;
                   /* case ServerReturnValue.SERVERRETURNCONDITIONS:
                        costLines = PurchaseNum * (AdvanceNum - 1);*/

                        //减去购买当前期的次数  才是预购的次数
                    }

                    PurchaseInAdvanceNum = AdvanceNum - 1;
                    OrderStartAndvance = activityDetailModel.getStageIndex();
                }

                if (PurchaseInAdvanceNum > 0) {
                    purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID,
                            PurchaseNum, PurchaseInAdvanceNum, Config.PURCHASEPRICKSILK, OrderID);
                }else{
                    //购买的是单期要扣除微劵
                    walletService.virtualSecuritiesCost( UserID,VirtualSecurities );
                }


                if (purchaseInAdvanceDAO.updateActivityLines(ActivityID, costLines) == 0) {
                    return false;
                }

                //增加任务经验
                userDAO.AddUserExpByUserId( UserID,Config.AddExpPurchase*costLines );

                orderService.createOrder(UserID, InstallmentActivityID, PurchaseNum * AdvanceNum, PurchaseNum,
                        AdvanceNum, Config.PURCHASEPRICKSILK, OrderID, OrderStartAndvance);


                return true;
            }
        }), Config.SERVICE_SUCCESS)) {
            out_ActivityName.append(ActivityName[0]);
            return 1;
        }

        out_ActivityName.append(ActivityName[0]);
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

    public int PurchaseActivity(String InstallmentActivityID, String UserID, int PurchaseNum, int Lines) throws Exception {
        if (PurchaseNum > getInstallmentActivityRemainingTicket(InstallmentActivityID)) {
            return ServerReturnValue.SERVERRETURNERROR;
        }

        return purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum, Config.PURCHASEPRICKSILK,Lines);

    }

    /**
     * 领投单次购买
     *
     * @param InstallmentActivityID
     * @param UserID
     * @return
     */
    public int LocalTyrantsPurchase(String InstallmentActivityID, String UserID, int Lines) throws Exception {
        if (!IsEnoughLocalTyrantsTickets(InstallmentActivityID)) {
            return ServerReturnValue.SERVERRETURNCONDITIONS;
        }
        return purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, 1, Config.PURCHASELOCALTYRANTS,Lines);
        //orderService.createOrder(UserID, InstallmentActivityID, TotalLinePeoples, 0, 0, Config.PURCHASELOCALTYRANTS, OrderID);
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

        ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModelNoTransaction(InstallmentActivityID);
        for (PurchaseInAdvanceModel it : list) {
            if (activityDynamicModel.IsEnough()) {
                return ServerReturnValue.SERVERRETURNCOMPELETE;
            }

            int PurchaseNum = it.getPurchaseNum();
            //单词购买
            int Result = 0;
            switch (it.getPurchaseType()) {
                case Config.PURCHASEPRICKSILK:
                    Result = this.PurchaseActivity(InstallmentActivityID, it.getUserID(), PurchaseNum, PurchaseNum);
                    break;
                case Config.PURCHASELOCALTYRANTS:
                    Result = this.LocalTyrantsPurchase(InstallmentActivityID, it.getUserID(),activityDynamicModel.getActivityTotalLinesPeoples() );
                    break;
            }

            if (Result != ServerReturnValue.SERVERRETURNCOMPELETE) {
                continue;
            }

            //刷新预购次数
            if( purchaseInAdvanceDAO.UpdatePurchaseActivityNum(it.getPurchaseInAdvanceNumID(), it.getUserID(), ActivityID ) == 0 ){
                return ServerReturnValue.SERVERRETURNERROR;
            }
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
            List<PurchaseInAdvanceModel> list = purchaseInAdvanceDAO.PurchaseInAdvanceCompelete(ActivityID, Config.FINDPAGENUM, page);

            if (list == null) {
                return -1;
            }

            if (list.size() == 0) {
                return 0;
            }


            BatchPurchaseActivity(list, InstallmentActivityID, ActivityID);
            page += Config.FINDPAGENUM;
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
    public int LocalTyrantsPurchaseActivity(final String InstallmentActivityID,
                                            final String UserID, final int AdvanceNum,
                                            final String OrderID,
                                            StringBuffer out_ActivityName) {
        final String[] ActivityName = new String[1];
        if (AdvanceNum == 0) {
            return 0;
        }

        if (Objects.equals(activityInfoDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDynamicModel activityDynamicModel = activityInfoDAO.getActivityDynamicModelNoTransaction(InstallmentActivityID);
                ActivityDetailModel activityDetailModel = activityDynamicModel.getActivityDetailModel();
                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();
                String ActivityID = activityDynamicModel.getActivityVerifyCompleteModel().getActivityId();

                ActivityName[0] = activityVerifyCompleteModel.getName();
                int OrderStartAndvance;
                int Lines = activityDynamicModel.getActivityTotalLinesPeoples() * AdvanceNum;
                int TempAdvanceNum;
                if (!IsEnoughLocalTyrantsTickets(InstallmentActivityID)) {
                    if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLinePoples(Lines + activityDynamicModel.getActivityTotalLinesPeoples())) {
                        return false;
                    }
                    TempAdvanceNum = AdvanceNum;

                    if (activityDetailModel.getStageIndex() != activityVerifyCompleteModel.getTotalInstallmentNum()) {
                        int temp = activityVerifyCompleteModel.getTotalLinePeoples() / activityVerifyCompleteModel.getTotalInstallmentNum();
                        OrderStartAndvance = activityVerifyCompleteModel.getCurLinePeoples() / temp;
                    } else {
                        OrderStartAndvance = 1;
                    }

                } else {
                    if (!activityVerifyCompleteModel.IsEnoughAdvance(AdvanceNum) ||
                            activityVerifyCompleteModel.IsEnoughLinePoples(Lines)) {
                        return false;
                    }

                    int PurchaseResult = LocalTyrantsPurchase(InstallmentActivityID, UserID, activityDynamicModel.getActivityTotalLinesPeoples());
                    if (PurchaseResult == ServerReturnValue.SERVERRETURNERROR) {
                        return false;
                    }
                    TempAdvanceNum = AdvanceNum - 1;
                    OrderStartAndvance = activityDetailModel.getStageIndex();
                }

                if (TempAdvanceNum > 0) {
                    purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, activityDynamicModel.getActivityTotalLinesPeoples(), TempAdvanceNum, Config.PURCHASELOCALTYRANTS, OrderID);
                }

                //刷新总购买额度
/*                ActivityVerifyCompleteModel activityVerifyCompleteModel1 = activityDynamicModel.getActivityVerifyCompleteModel();
                int curLines = activityVerifyCompleteModel1.getCurFund();
                curLines += Lines;
                activityVerifyCompleteModel1.setCurFund(curLines);
                //刷新大R
                int curLinePeoples = activityVerifyCompleteModel1.getCurLinePeoples();
                curLinePeoples += Lines;
                activityVerifyCompleteModel1.setCurLinePeoples(curLinePeoples);
                purchaseInAdvanceDAO.updateNoTransaction(activityVerifyCompleteModel1);*/

                //钱包花费
                if (!walletService.CostLines(UserID, Lines)) {
                    return false;
                }

                if (purchaseInAdvanceDAO.updateActivityLinesPeoples(ActivityID, Lines) == 0) {
                    return false;
                }

                //增加人物经验
                userDAO.AddUserExpByUserId( UserID,Config.AddExpPurchase*Lines );

                orderService.createOrder(UserID, InstallmentActivityID, Lines, activityDynamicModel.getActivityTotalLinesPeoples(),
                        AdvanceNum, Config.PURCHASELOCALTYRANTS, OrderID, OrderStartAndvance);

/*                int a = activityVerifyCompleteModel1.getCurLinePeoples();

                int b = orderService.TestLingTou();

                if (a != b) {
                    return false;
                }*/


                return true;
            }
        }), Config.SERVICE_SUCCESS)) {
            out_ActivityName.append(ActivityName[0]);
            return 1;
        }

        out_ActivityName.append(ActivityName[0]);
        return 0;
    }






}
