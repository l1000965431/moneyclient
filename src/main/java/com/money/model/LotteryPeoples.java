package com.money.model;

/**
 * Created by liumin on 15/7/25.
 */
public class LotteryPeoples extends BaseModel {

    String TickID;

    /**
     * 中奖用户ID
     */
    String UserId;

    /**
     * 中奖金额
     */
    int LotteryLines;

    /**
     * 项目ID
     */
    String ActivityID;

    /**
     * 中奖类型
     */
    int PurchaseType;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getLotteryLines() {
        return LotteryLines;
    }

    public void setLotteryLines(int lotteryLines) {
        LotteryLines = lotteryLines;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public int getPurchaseType() {
        return PurchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        PurchaseType = purchaseType;
    }

    public String getTickID() {
        return TickID;
    }

    public void setTickID(String tickID) {
        TickID = tickID;
    }
}
