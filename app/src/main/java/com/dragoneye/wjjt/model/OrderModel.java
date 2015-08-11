package com.dragoneye.wjjt.model;

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

    private String activityStageId;

    private String activityId;

    private String activityName;

    private String summary;

    private int targetFund;

    private int status;

    private String imageUrl;

    private int currentFund;

    private int currentStage;

    private int totalStage;


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

    public String getActivityStageId() {
        return activityStageId;
    }

    public void setActivityStageId(String activityStageId) {
        this.activityStageId = activityStageId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTargetFund() {
        return targetFund;
    }

    public void setTargetFund(int targetFund) {
        this.targetFund = targetFund;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCurrentFund() {
        return currentFund;
    }

    public void setCurrentFund(int currentFund) {
        this.currentFund = currentFund;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public int getTotalStage() {
        return totalStage;
    }

    public void setTotalStage(int totalStage) {
        this.totalStage = totalStage;
    }
}
