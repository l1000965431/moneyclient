package com.money.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 项目动态
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Entity
@Table(name = "activitydynamic")
public class ActivityDynamicModel extends BaseModel {

    //上线项目开始
    public final static int ONLINE_ACTIVITY_START = 1;

    //上线项目完成
    public final static int ONLINE_ACTIVITY_COMPLETE = 2;

    //上线项目结算
    public final static int ONLINE_ACTIVITY_SETTLEMENT = 3;

    //上线项目错误
    public final static int ONLINE_ACTIVITY_ERROR = 4;

    @Id
    String activityStageId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ActivityDetailModel.class)
    ActivityDetailModel activityDetailModel;

//    /**
//     *  项目组ID
//     */
//    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "groupId", referencedColumnName = "id")
//    ActivityGroupModel activityGroupModel;

    /**
     * 父项目
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentActivityId", referencedColumnName = "activityId")
    ActivityVerifyCompleteModel activityVerifyCompleteModel;

    /**
     *  组号
     */
    int groupId;

    /**
     * 项目总金额
     */
    int activityTotalAmount;

    /**
     * 项目状态
     */
    int activityState;

    /**
     * 父项目ID
     */
    int activityParentId;

    /**
     * 项目组ID
    */
    int activityGroup;

    /**
     * 项目当前金额
     */
    int activityCurLines;

    /**
     * 项目当前金额的人数
     */
    int activityCurLinesPeoples;

    public ActivityDetailModel getActivityDetailModel() {
        return activityDetailModel;
    }

    public void setActivityDetailModel(ActivityDetailModel activityDetailModel) {
        this.activityDetailModel = activityDetailModel;
    }

//    public ActivityGroupModel getActivityGroupModel() {
//        return activityGroupModel;
//    }
//
//    public void setActivityGroupModel(ActivityGroupModel activityGroupModel) {
//        this.activityGroupModel = activityGroupModel;
//    }

    public int getActivityCurLines() {
        return activityCurLines;
    }

    public void setActivityCurLines(int activityCurLines) {
        this.activityCurLines = activityCurLines;
    }

    public String getActivityStageId() {
        return activityStageId;
    }

    public void setActivityStageId(String activityStageId) {
        this.activityStageId = activityStageId;
    }

    public int getActivityTotalAmount() {
        return activityTotalAmount;
    }

    public void setActivityTotalAmount(int activityTotalAmount) {
        this.activityTotalAmount = activityTotalAmount;
    }

    public int getActivityState() {
        return activityState;
    }

    public void setActivityState(int activityState) {
        this.activityState = activityState;
    }

    public int getActivityParentId() {
        return activityParentId;
    }

    public void setActivityParentId(int activityParentId) {
        this.activityParentId = activityParentId;
    }

    public int getActivityGroup() {
        return activityGroup;
    }

    public void setActivityGroup(int activityGroup) {
        this.activityGroup = activityGroup;
    }

    public int getActivityCurLinesPeoples() {
        return activityCurLinesPeoples;
    }

    public void setActivityCurLinesPeoples(int activityCurLinesPeoples) {
        this.activityCurLinesPeoples = activityCurLinesPeoples;
    }

    public ActivityVerifyCompleteModel getActivityVerifyCompleteModel() {
        return activityVerifyCompleteModel;
    }

    public void setActivityVerifyCompleteModel(ActivityVerifyCompleteModel activityVerifyCompleteModel) {
        this.activityVerifyCompleteModel = activityVerifyCompleteModel;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
