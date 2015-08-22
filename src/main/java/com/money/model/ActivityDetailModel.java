package com.money.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 项目已上线
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "activitydetails")
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActivityDetailModel extends BaseModel {

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
     *分期的项目ID
     */
    @Id
    String activityStageId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ActivityDynamicModel.class)
    private ActivityDynamicModel dynamicModel;

    /**
     * 父项目
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentActivityId", referencedColumnName = "activityId")
    ActivityVerifyCompleteModel activityVerifyCompleteModel;

    /**
     * 项目组中大R收益层次
     */
    @OneToMany(mappedBy = "activityDetailModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SREarningModel> srEarningModels = new HashSet<SREarningModel>();


    /**
     * 项目状态
     * {@link #ONLINE_ACTIVITY_START}
     */
    int status=0;

    /**
     *  项目期数
     */
    int stageIndex=0;

    /**
     *  组号
     */
    int groupId=0;

    /**
     * 小R的筹资金额
     */
    int targetFund=0;

    /**
     * 筹资天数
     */
    int raiseDay=0;

    /**
     * 大R中奖金额
     */
    int LocaltyrantsLotteryLines=0;

    /**
     * 发布时间
     * @return
     */
    Date activityStartTime;

    /**
     * 结束时间
     * @return
     */
    Date activityEndTime;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTargetFund() {
        return targetFund;
    }

    public void setTargetFund(int targetFund) {
        this.targetFund = targetFund;
    }

    public int getRaiseDay() {
        return raiseDay;
    }

    public void setRaiseDay(int raiseDay) {
        this.raiseDay = raiseDay;
    }

    public void setFatherActivityID(String fatherActivityID) {
        fatherActivityID = fatherActivityID;
    }

    public String getActivityStageId() {
        return activityStageId;
    }

    public void setActivityStageId(String activityID) {
        activityStageId = activityID;
    }

    public Date getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(Date activityStartTime) {
        activityStartTime = activityStartTime;
    }

    public Date getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(Date activityEndTime) {
        activityEndTime = activityEndTime;
    }

    public ActivityDynamicModel getDynamicModel() {
        return dynamicModel;
    }

    public void setDynamicModel(ActivityDynamicModel dynamicModel) {
        this.dynamicModel = dynamicModel;
    }

    public ActivityVerifyCompleteModel getActivityVerifyCompleteModel() {
        return activityVerifyCompleteModel;
    }

    public void setActivityVerifyCompleteModel(ActivityVerifyCompleteModel activityVerifyCompleteModel) {
        this.activityVerifyCompleteModel = activityVerifyCompleteModel;
    }

    public Set<SREarningModel> getSrEarningModels() {
        return srEarningModels;
    }

    public void setSrEarningModels(Set<SREarningModel> srEarningModels) {
        this.srEarningModels = srEarningModels;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getStageIndex() {
        return stageIndex;
    }

    public void setStageIndex(int stageIndex) {
        this.stageIndex = stageIndex;
    }

    public int getLocaltyrantsLotteryLines() {
        return LocaltyrantsLotteryLines;
    }

    public void setLocaltyrantsLotteryLines(int localtyrantsLotteryLines) {
        LocaltyrantsLotteryLines = localtyrantsLotteryLines;
    }


}
