package com.dragoneye.money.model;

/**
 * Created by happysky on 15-7-29.
 */
public class OrderModel {
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
     * 项目名称
     */
    private String projectName;

    /**
     * 项目状态
     */
    private int projectStatus;

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public int getAdvanceNum() {
        return AdvanceNum;
    }

    public void setAdvanceNum(int advanceNum) {
        AdvanceNum = advanceNum;
    }

    public int getPurchaseNum() {
        return PurchaseNum;
    }

    public void setPurchaseNum(int purchaseNum) {
        PurchaseNum = purchaseNum;
    }

    public int getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(int orderLines) {
        this.orderLines = orderLines;
    }

    public int getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(int projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
