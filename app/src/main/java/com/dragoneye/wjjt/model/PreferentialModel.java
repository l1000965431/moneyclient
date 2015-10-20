package com.dragoneye.wjjt.model;

import java.util.Date;
import java.util.List;

/**
 * Created by happysky on 15-10-15.
 */
public class PreferentialModel {
    public static final int STATE_START = 5;
    public static final int STATE_FINISH = 6;
    public static final int STATE_WAITING = 7;

    String activityId;

    public String getActivityCompleteId() {
        return activityCompleteId;
    }

    public void setActivityCompleteId(String activityCompleteId) {
        this.activityCompleteId = activityCompleteId;
    }

    String activityCompleteId;
    String imageUrl;
    String name;
    String summary;
    int bonusPool;
    int exp;
    int state;
    Date date;

    public List<EarningModel> getEarnings() {
        return earnings;
    }

    public void setEarnings(List<EarningModel> earnings) {
        this.earnings = earnings;
    }

    List<EarningModel> earnings;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getBonusPool() {
        return bonusPool;
    }

    public void setBonusPool(int bonusPool) {
        this.bonusPool = bonusPool;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
