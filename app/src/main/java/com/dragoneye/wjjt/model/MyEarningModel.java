package com.dragoneye.wjjt.model;

/**
 * Created by happysky on 15-8-17.
 * 收益记录
 */
public class MyEarningModel {
    private String activityId;
    private String activityStageId;
    private int actvityStageIndex;
    private String imageUrl;
    private int earningPrice;
    private String activityName;

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

    public int getActvityStageIndex() {
        return actvityStageIndex;
    }

    public void setActvityStageIndex(int actvityStageIndex) {
        this.actvityStageIndex = actvityStageIndex;
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
}
