package com.money.model;


import javax.persistence.*;
import java.io.Serializable;
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
public class ActivityDetailModel implements Serializable {

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

    /**
     *  项目组ID
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId", referencedColumnName = "id")
    ActivityGroupModel activityGroupModel;

    /**
     * 项目中大R投资与收益信息
     */
    @OneToMany(mappedBy = "activityDetailModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<BRInvestEarningModel> brInvestEarningModels = new HashSet<BRInvestEarningModel>();

    /**
     * 项目状态
     * {@link #ONLINE_ACTIVITY_START}
     */
    int status;

    /**
     * 筹资金额
     */
    int targetFund;

    /**
     * 筹资天数
     */
    int raiseDay;

    /**
     * 各金额总人数
     */
    String activityLinesPeoples;

    /**
     * 父项目ID
     */
    String fatherActivityID;

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

    public ActivityGroupModel getActivityGroupModel() {
        return activityGroupModel;
    }

    public void setActivityGroupModel(ActivityGroupModel activityGroupModel) {
        this.activityGroupModel = activityGroupModel;
    }

    public Set<BRInvestEarningModel> getBrInvestEarningModels() {
        return brInvestEarningModels;
    }

    public void setBrInvestEarningModels(Set<BRInvestEarningModel> brInvestEarningModels) {
        this.brInvestEarningModels = brInvestEarningModels;
    }

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

    public String getActivityLinesPeoples() {
        return activityLinesPeoples;
    }

    public void setActivityLinesPeoples(String activitylinespeoples) {
        this.activityLinesPeoples = activitylinespeoples;
    }

    public String getFatherActivityID() {
        return fatherActivityID;
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
}
