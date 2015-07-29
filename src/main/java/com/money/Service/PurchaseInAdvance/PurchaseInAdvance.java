package com.money.Service.PurchaseInAdvance;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.dao.PurchaseInAdvanceDAO.PurchaseInAdvanceDAO;
import com.money.model.PurchaseInAdvanceModel;
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

    /**
     * 项目预购
     *
     * @param InstallmentActivityID 分期项目ID
     * @param ActivityID            活动ID
     * @param UserID
     * @param PurchaseNum           单期购买的数量
     * @param AdvanceNum            预购期数
     */
    public int PurchaseInAdvance(String InstallmentActivityID, String ActivityID, String UserID, int PurchaseNum, int AdvanceNum) {
        if (!IsRemainingInstallment(ActivityID, AdvanceNum) || !IsRemainingTickets(InstallmentActivityID, PurchaseNum)) {
            return 0;
        }
        //购买当前期
        PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum);

        //减去购买当前期的次数  才是预购的次数
        int PurchaseInAdvanceNum = PurchaseNum - 1;

        if( PurchaseInAdvanceNum > 0 ){
            purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, PurchaseInAdvanceNum, AdvanceNum, Config.PURCHASEPRICKSILK);
        }

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
    public int PurchaseActivity(String InstallmentActivityID, String UserID, int PurchaseNum) {
        purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, PurchaseNum,Config.PURCHASEPRICKSILK);
        return 0;
    }

    /**
     * 领投单次购买
     * @param InstallmentActivityID
     * @param UserID
     * @return
     */
    public int LocalTyrantsPurchase( String InstallmentActivityID, String UserID ){
        purchaseInAdvanceDAO.PurchaseActivity(InstallmentActivityID, UserID, 1,Config.PURCHASELOCALTYRANTS);
        return 0;
    }

    /**
     * 批量购买
     *
     * @param list 购买人员列表
     * @return
     */
    public int BatchPurchaseActivity(List<PurchaseInAdvanceModel> list, String InstallmentActivityID, String ActivityID) {
        if (list == null) {
            return 0;
        }

        for (PurchaseInAdvanceModel it : list) {

            int PurchaseNum = it.getPurchaseNum();
            int CurPurchaseActivityNum = it.getCurPurchaseInAdvanceNum();
            //单词购买
            PurchaseActivity(InstallmentActivityID, it.getUserID(), PurchaseNum);

            //刷新预购次数
            CurPurchaseActivityNum++;
            purchaseInAdvanceDAO.UpdatePurchaseActivityNum(it.getUserID(), ActivityID, CurPurchaseActivityNum);
        }

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

        return true;
    }

    /**
     * 跟投是否还有剩余的票数
     *
     * @param InstallmentActivityID 分期项目ID
     * @param PurchaseNum           购买的票数
     * @return
     */
    public boolean IsRemainingTickets(String InstallmentActivityID, int PurchaseNum) {

        return true;
    }


    /**
     * 领投是否还有剩余的期
     *
     * @param ActivityID
     * @param AdvanceNum 购买的期数
     * @return
     */
    public boolean IsRemainingLocalTyrantsInstallment(String ActivityID, int AdvanceNum) {

        return true;
    }

    /**
     * 领投是否还有剩余的票数
     *
     * @param InstallmentActivityID 分期项目ID
     * @return
     */
    public boolean IsRemainingLocalTyrantsTickets(String InstallmentActivityID) {

        return true;
    }




    /**
     * 领投购买项目
     * @param InstallmentActivityID   分期项目ID
     * @param UserID                  用户ID
     * @param AdvanceNum              购买的期数
     * @return
     */
    public int LocalTyrantsPurchaseActivity(String InstallmentActivityID,String ActivityID ,String UserID, int AdvanceNum) {
        if( !IsRemainingLocalTyrantsInstallment( InstallmentActivityID,AdvanceNum ) || !IsRemainingLocalTyrantsTickets( InstallmentActivityID ) ){
            return 0;
        }

        LocalTyrantsPurchase( InstallmentActivityID,UserID );
        int TempAdvanceNum = AdvanceNum-1;
        if( TempAdvanceNum > 0 ){
            purchaseInAdvanceDAO.InsertPurchaseInAdvance(ActivityID, UserID, 1, AdvanceNum, Config.PURCHASELOCALTYRANTS);
        }

        return 0;
    }
}
