package model;

import javax.persistence.*;

/**
 * Created by liumin on 15/7/6.
 */

@Entity
@Table(name = "order")
public class OrderModel {

    @Id
    private Long orderID;

    private int activityID;

    private int userID;

    private String orderdate;

    private int orderlines;

    private int activitygroupID;

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public int getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(int orderlines) {
        this.orderlines = orderlines;
    }

    public int getActivitygroupID() {
        return activitygroupID;
    }

    public void setActivitygroupID(int activitygroupID) {
        this.activitygroupID = activitygroupID;
    }

    public int getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(int orderstate) {
        this.orderstate = orderstate;
    }

    private int orderstate;
}
