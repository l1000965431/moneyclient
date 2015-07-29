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
public class OrderModel extends BaseModel {

    //订单未提交
    public final static int ORDER_STATE_NOSUBMITTED = 0;

    //订单正在提交
    public final static int ORDER_STATE_SUBMITTING = 1;

    //订单提交成功
    public final static int ORDER_STATE_SUBMITTSUCCESS = 2;

    //订单提交失败
    public final static int ORDER_STATE_SUBMITTEDFAIL = 3;

    //订单取消
    public final static int ORDER_STATE_SUBMITTECANEL = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long orderId;

    private String userId;

    private Date orderDate;

    /**
     * 订单状态
     */
    private int orderState;

    /**
     * 购买票数
     */
    private int PurchaseNum;

    /**
     * 购买期数
     */
    private int AdvanceNum;

    /**
     * 订单金额
     */
    private int orderLines;

    /**
     * 项目信息
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER)
    @JoinColumn( name = "activityId", referencedColumnName = "activityId")
    private ActivityVerifyCompleteModel activityVerifyCompleteModel;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getPurchaseNum() {
        return PurchaseNum;
    }

    public void setPurchaseNum(int purchaseNum) {
        PurchaseNum = purchaseNum;
    }

    public int getAdvanceNum() {
        return AdvanceNum;
    }

    public void setAdvanceNum(int advanceNum) {
        AdvanceNum = advanceNum;
    }

    public int getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(int orderLines) {
        this.orderLines = orderLines;
    }

    public ActivityVerifyCompleteModel getActivityVerifyCompleteModel() {
        return activityVerifyCompleteModel;
    }

    public void setActivityVerifyCompleteModel(ActivityVerifyCompleteModel activityVerifyCompleteModel) {
        this.activityVerifyCompleteModel = activityVerifyCompleteModel;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }
}
