package com.money.model;

import java.util.*;

/**
 * Created by liumin on 15/10/15.
 */
public class ActivityPreferentiaInfo extends BaseModel {


    public ActivityPreferentiaInfo( ActivityVerifyCompleteModel activityVerifyCompleteModel ){
        this.activityCompleteId =  activityVerifyCompleteModel.getActivityId();

        this.name = activityVerifyCompleteModel.getName();

        this.imageUrl = activityVerifyCompleteModel.getImageUrl();

        this.summary = activityVerifyCompleteModel.getSummary();

        Set<SREarningModel> set = activityVerifyCompleteModel.getSrEarningModels();

        for (SREarningModel sREarningModel : set) {
            ActivitySREarning.put( sREarningModel.getEarningPrice(),sREarningModel.getNum() );
        }
    }

    String activityCompleteId;

    String name;

    String imageUrl;

    String summary;

    Map<Integer,Integer> ActivitySREarning = new HashMap<>();

    public String getActivityCompleteId() {
        return activityCompleteId;
    }

    public void setActivityCompleteId(String activityCompleteId) {
        this.activityCompleteId = activityCompleteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<Integer,Integer> getActivitySREarning() {
        return ActivitySREarning;
    }

    public void setActivitySREarning(Map<Integer,Integer> activitySREarning) {
        ActivitySREarning = activitySREarning;
    }
}
