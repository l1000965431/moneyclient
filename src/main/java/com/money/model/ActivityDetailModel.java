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
    public final static int ONLINEACTIVITY_START = 1;

    //上线项目完成
    public final static int ONLINEACTIVITY_COMPLETE = 2;

    //上线项目结算
    public final static int ONLINEACTIVITY_SETTLEMENT = 3;

    //上线项目错误
    public final static int ONLINEACTIVITY_ERROR = 4;

    //上线项目错误
    public final static int ONLINEACTIVITY_FAILED = 5;

    /**
     * 项目ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     *分期的项目ID
     */
    //@Id
    String ActivityID;

    public ActivityGroupModel getActivityGroupModel() {
        return activityGroupModel;
    }

    public void setActivityGroupModel(ActivityGroupModel activityGroupModel) {
        this.activityGroupModel = activityGroupModel;
    }

    /**
     *  项目组ID
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false)
    @JoinColumn(name = "groupId", referencedColumnName = "id")
    ActivityGroupModel activityGroupModel;

    /**
     * 项目中大R投资与收益信息
     */
    @OneToMany(mappedBy = "activityDetailModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<BRInvestEarningModel> brInvestEarningModels = new HashSet<BRInvestEarningModel>();

    /**
     * 项目状态
     * {@link #ONLINEACTIVITY_START}
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
    String FatherActivityID;

    /**
     * 发布时间
     * @return
     */
    Date ActivityStartTime;

    /**
     * 结束时间
     * @return
     */
    Date ActivityEndTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return FatherActivityID;
    }

    public void setFatherActivityID(String fatherActivityID) {
        FatherActivityID = fatherActivityID;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public Date getActivityStartTime() {
        return ActivityStartTime;
    }

    public void setActivityStartTime(Date activityStartTime) {
        ActivityStartTime = activityStartTime;
    }

    public Date getActivityEndTime() {
        return ActivityEndTime;
    }

    public void setActivityEndTime(Date activityEndTime) {
        ActivityEndTime = activityEndTime;
    }
}
