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
    String TicketID;

    /**
     * 用户ID
     */
    String UserId;

    public String getTicketID() {
        return TicketID;
    }

    public void setTicketID(String ticketID) {
        TicketID = ticketID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
