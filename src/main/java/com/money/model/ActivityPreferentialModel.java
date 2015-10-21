package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 特惠项目表
 * <p>User: 刘旻
 * <p>Date: 15-10-14
 * <p>Version: 1.0
 */

@Entity(name = "activitypreferential")
@Table
public class ActivityPreferentialModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ActivityId;

    /**
     * 奖金池的总金额
     */
    int ActivityLines;

    /**
     * 当前金额
     */
    int CurLines = 0;

    /**
     * 项目状态
     */
    int ActivityState = 0;

    /**
     * 项目开始时间
     */
    Date ActivityStartTime;

    /**
     * 项目结束时间
     */
    Date ActivityEndTime;

    /**
     * 需要的用户经验值
     */
    int UserEXP=0;

    /**
     * 中奖几率
     */
    int WinningChance;

    /**
     * 项目详情介绍
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = ActivityVerifyCompleteModel.class)
    ActivityVerifyCompleteModel activityVerifyCompleteModel;

    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    public int getActivityLines() {
        return ActivityLines;
    }

    public void setActivityLines(int activityLines) {
        ActivityLines = activityLines;
    }

    public int getCurLines() {
        return CurLines;
    }

    public void setCurLines(int curLines) {
        CurLines = curLines;
    }

    public int getActivityState() {
        return ActivityState;
    }

    public void setActivityState(int activityState) {
        ActivityState = activityState;
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

    public ActivityVerifyCompleteModel getActivityVerifyCompleteModel() {
        return activityVerifyCompleteModel;
    }

    public void setActivityVerifyCompleteModel(ActivityVerifyCompleteModel activityVerifyCompleteModel) {
        this.activityVerifyCompleteModel = activityVerifyCompleteModel;
    }

    public int getUserEXP() {
        return UserEXP;
    }

    public void setUserEXP(int userEXP) {
        UserEXP = userEXP;
    }

    public int getWinningChance() {
        return WinningChance;
    }

    public void setWinningChance(int winningChance) {
        WinningChance = winningChance;
    }
}
