package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单表
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Entity(name = "activityorder")
@Table
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
    String orderId;

    private String userId;

    private Date orderDate;

    /**
     * 订单状态
     */
    private int purchaseType;

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
     * 购买订单开始的期数
     */
    private int orderStartAdvance;

    /**
     * 项目信息
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    /*@JoinColumn( name = "orderactivityId", referencedColumnName = "activityStageId")*/
    private ActivityDetailModel activityDetailModel;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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

    public ActivityDetailModel getActivityDetailModel() {
        return activityDetailModel;
    }

    public void setActivityDetailModel(ActivityDetailModel activityDetailModel) {
        this.activityDetailModel = activityDetailModel;
    }

    public int getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        this.purchaseType = purchaseType;
    }

    public int getOrderStartAdvance() {
        return orderStartAdvance;
    }

    public void setOrderStartAdvance(int orderStartAdvance) {
        this.orderStartAdvance = orderStartAdvance;
    }
}
