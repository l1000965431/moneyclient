package com.money.model;

/**
 *
 */

public class PurchaseInAdvanceModel extends BaseModel {
    /**
     *
     */
    String UserID;

    String PurchaseInAdvanceNumID;

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

    int PurchaseType;

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

    public int getPurchaseType() {
        return PurchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        PurchaseType = purchaseType;
    }

    public String getPurchaseInAdvanceNumID() {
        return PurchaseInAdvanceNumID;
    }

    public void setPurchaseInAdvanceNumID(String purchaseInAdvanceNumID) {
        PurchaseInAdvanceNumID = purchaseInAdvanceNumID;
    }
}
