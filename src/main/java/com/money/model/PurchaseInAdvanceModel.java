package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 */
@Entity
public class PurchaseInAdvanceModel extends BaseModel {
    /**
     *
     */
    @Id
    String UserID;

    /**
     * 预购次数
     */
    int PurchaseInAdvanceNum;

    /**
     * 当前已经预购的次数
     */
    int CurPurchaseInAdvanceNum;

    /**
     * 购买次数
     */
    int PurchaseNum;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getPurchaseInAdvanceNum() {
        return PurchaseInAdvanceNum;
    }

    public void setPurchaseInAdvanceNum(int purchaseInAdvanceNum) {
        PurchaseInAdvanceNum = purchaseInAdvanceNum;
    }

    public int getCurPurchaseInAdvanceNum() {
        return CurPurchaseInAdvanceNum;
    }

    public void setCurPurchaseInAdvanceNum(int curPurchaseInAdvanceNum) {
        CurPurchaseInAdvanceNum = curPurchaseInAdvanceNum;
    }

    public int getPurchaseNum() {
        return PurchaseNum;
    }

    public void setPurchaseNum(int purchaseNum) {
        PurchaseNum = purchaseNum;
    }
}
