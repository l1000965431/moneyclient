package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单表
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Entity
@Table(name = "activityorder")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long orderid;

    private int activityid;

    private int userid;

    private Date orderdate;

    private int orderlines;

    private int activitygroupid;

    private int orderstate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }

    public int getActivityid() {
        return activityid;
    }

    public void setActivityid(int activityid) {
        this.activityid = activityid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public int getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(int orderlines) {
        this.orderlines = orderlines;
    }

    public int getActivitygroupid() {
        return activitygroupid;
    }

    public void setActivitygroupid(int activitygroupid) {
        this.activitygroupid = activitygroupid;
    }

    public int getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(int orderstate) {
        this.orderstate = orderstate;
    }
}
