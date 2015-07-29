package com.money.model;

/**
 * 项目票表
 * <p>User: 刘旻
 * <p>Date: 15-7-27
 * <p>Version: 1.0
 */

public class TicketModel {

    /**
     * 票ID
     */
    String TiceketID;

    /**
     * 项目ID
     */
    String ActivityID;

    /**
     * 用户ID
     */
    String UserId;

    public String getTiceketID() {
        return TiceketID;
    }

    public void setTiceketID(String ticeketID) {
        TiceketID = ticeketID;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
