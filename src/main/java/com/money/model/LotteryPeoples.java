package com.money.model;

/**
 * Created by liumin on 15/7/25.
 */
public class LotteryPeoples extends BaseModel {

    /**
     * 中奖用户ID
     */
    String UserID;

    /**
     * 中奖金额
     */
    int LotteryLines;

    /**
     * 项目ID
     */
    String ActivityID;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
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
}
