package com.money.model;

import javax.persistence.*;

/**
 * 项目组包含的项目关系表
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "activitygrouprelationship")
public class ActivityGroupRelationshipModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long groupId;

    /**
     *  项目ID
     */
    Long activityId;

    /**
     * 存储大R投资与收益情况，格式为json二维数组
     * [?][0] 投资额度
     * [?][1] 回报金额
     * [?][2] 数量
     */
    @Column(columnDefinition = "TEXT")
    String brTickets;

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

    public String getBrTickets() {
        return brTickets;
    }

    public void setBrTickets(String brTickets) {
        this.brTickets = brTickets;
    }
}
