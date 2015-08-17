package com.money.model;

import javax.persistence.*;

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

    /**
     * 父项目
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentActivityId", referencedColumnName = "activityId")
    ActivityVerifyCompleteModel activityVerifyCompleteModel;

    /**
     *  组号
     */
    int groupId=0;

    /**
     * 项目总金额
     */
    int activityTotalAmount=0;

    /**
     * 项目状态
     */
    int activityState=0;

    /**
     * 小R项目当前金额
     */
    int activityCurLines=0;

    /**
     * 小R的总金额
     */
    int activityTotalLines=0;

    /**
     * 大R当前的金额
     */
    int activityCurLinesPeoples=0;

    /**
     * 大R总金额
     */
    int activityTotalLinesPeoples=0;

    public ActivityDetailModel getActivityDetailModel() {
        return activityDetailModel;
    }

    public void setActivityDetailModel(ActivityDetailModel activityDetailModel) {
        this.activityDetailModel = activityDetailModel;
    }


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

    public int getActivityTotalLines() {
        return activityTotalLines;
    }

    public void setActivityTotalLines(int activityTotalLines) {
        this.activityTotalLines = activityTotalLines;
    }

    public int getActivityTotalLinesPeoples() {
        return activityTotalLinesPeoples;
    }

    public void setActivityTotalLinesPeoples(int activityTotalLinesPeoples) {
        this.activityTotalLinesPeoples = activityTotalLinesPeoples;
    }

    public int getRemainingTicket(){
        return activityTotalLines - activityCurLines;
    }

    public boolean IsEnoughLines( int Lines ){
        if( activityTotalLines - activityCurLines >= Lines ){
            return true;
        }else{
            return false;
        }
    }

    public boolean IsEnoughLinesPeoples( int Lines ){
        if( activityTotalLinesPeoples - activityCurLinesPeoples >= Lines ){
            return true;
        }else{
            return false;
        }
    }

    public boolean IsEnough(){
        return ( activityCurLines+activityCurLinesPeoples >= activityTotalAmount );
    }

}
