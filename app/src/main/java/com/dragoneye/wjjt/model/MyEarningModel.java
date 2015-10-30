package com.dragoneye.wjjt.model;

import java.util.Date;

/**
 * Created by happysky on 15-8-17.
 * 收益记录
 */
public class MyEarningModel {
    private String activityId;
    private String activityStageId;
    private int activityStageIndex;
    private int activityTotalStage;
    private String imageUrl;
    private int earningPrice;
    private String activityName;
    private Date earningDate;
    private int id;

    public static final int EARNING_TYPE_LEAD = 1;
    public static final int EARNING_TYPE_FALLOW = 2;
    public static final int EARNING_TYPE_PREFERENTIAL = 3;

    public int getEarningType() {
        return earningType;
    }

    public void setEarningType(int earningType) {
        this.earningType = earningType;
    }

    private int earningType;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityStageId() {
        return activityStageId;
    }

    public void setActivityStageId(String activityStageId) {
        this.activityStageId = activityStageId;
    }

    public int getActivityStageIndex() {
        return activityStageIndex;
    }

    public void setActivityStageIndex(int activityStageIndex) {
        this.activityStageIndex = activityStageIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getEarningPrice() {
        return earningPrice;
    }

    public void setEarningPrice(int earningPrice) {
        this.earningPrice = earningPrice;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Date getEarningDate() {
        return earningDate;
    }

    public void setEarningDate(Date earningDate) {
        this.earningDate = earningDate;
    }

    public int getActivityTotalStage() {
        return activityTotalStage;
    }

    public void setActivityTotalStage(int activityTotalStage) {
        this.activityTotalStage = activityTotalStage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
