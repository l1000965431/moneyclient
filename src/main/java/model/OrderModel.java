package model;

/**
 * Created by liumin on 15/7/6.
 */
public class OrderModel {

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public int getActivityID() {
        return ActivityID;
    }

    public void setActivityID(int activityID) {
        ActivityID = activityID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getLines() {
        return Lines;
    }

    public void setLines(int lines) {
        Lines = lines;
    }

    public int getActivityGroupID() {
        return ActivityGroupID;
    }

    public void setActivityGroupID(int activityGroupID) {
        ActivityGroupID = activityGroupID;
    }

    private long orderID;

    private int ActivityID;

    private int UserID;

    private String OrderDate;

    private int Lines;

    private int ActivityGroupID;

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    private int orderState;
}
