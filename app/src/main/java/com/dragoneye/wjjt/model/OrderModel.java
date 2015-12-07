package com.dragoneye.wjjt.model;

import java.util.Date;

/**
 * Created by happysky on 15-7-29.
 */
public class OrderModel {
    //上线项目未开始
    public final static int ONLINE_ACTIVITY_NOSTART = 0;

    //上线项目开始
    public final static int ONLINE_ACTIVITY_START = 1;

    //上线项目完成
    public final static int ONLINE_ACTIVITY_COMPLETE = 2;

    //上线项目结算
    public final static int ONLINE_ACTIVITY_SETTLEMENT = 3;

    //上线项目错误
    public final static int ONLINE_ACTIVITY_ERROR = 4;

    //上线项目错误
    public final static int ONLINE_ACTIVITY_FAILED = 5;

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
     * 领投还是跟投
     */
    private int purchaseType;


    private int orderStartAdvance;

    /***
     * 中奖信息缓存
     */
    private String proportionInfo;

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

    private Date orderDate;

    private String id;

    public int getVirtualSecurities() {
        return virtualSecurities;
    }

    public void setVirtualSecurities(int virtualSecurities) {
        this.virtualSecurities = virtualSecurities;
    }

    private int virtualSecurities;


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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getProportionInfo() {
        return proportionInfo;
    }

    public void setProportionInfo(String proportionInfo) {
        this.proportionInfo = proportionInfo;
    }

    public String getStatusString(){
        switch (status){
            case ONLINE_ACTIVITY_COMPLETE:
                return "众筹已结束";
            default:
                return "众筹中";
        }
    }

    public int getOrderStartAdvance() {
        return orderStartAdvance;
    }

    public void setOrderStartAdvance(int orderStartAdvance) {
        this.orderStartAdvance = orderStartAdvance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
