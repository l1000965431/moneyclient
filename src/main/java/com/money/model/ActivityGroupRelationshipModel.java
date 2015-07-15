package com.money.model;

import javax.persistence.*;

/**
 * Created by happysky on 15-7-15.
 */
@Entity
@Table(name = "activitygrouprelationship")
public class ActivityGroupRelationshipModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long groupId;

    Long activityId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
