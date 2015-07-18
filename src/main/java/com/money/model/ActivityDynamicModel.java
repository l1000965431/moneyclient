package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 项目动态
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Entity
@Table(name = "activitydynamic")
public class ActivityDynamicModel implements Serializable {

    //上线项目开始
    public final static int ONLINEACTIVITY_START = 1;

    //上线项目完成
    public final static int ONLINEACTIVITY_COMPLETE = 2;

    //上线项目结算
    public final static int ONLINEACTIVITY_SETTLEMENT = 3;

    //上线项目错误
    public final static int ONLINEACTIVITY_ERROR = 4;

    @Id
    int activityprivateid;

    /**
     * 活动ID
     */
    int activityID;

    /**
     * 项目总金额
     */
    int activitytotalamount;

    /**
     * 项目状态
     */
    int activitystate;

    /**
     * 父项目ID
     */
    int activityparentid;

    /**
     * 项目组ID
    */
    int activitygroup;

    /**
     * 项目概率参数
     */
    String activityprobabilityparameters;

    /**
     * 项目当前金额
     */
    int activitycurlines;

    /**
     * 项目当前金额的人数
     */
    String activitycurlinespeoples;

    public int getActivitycurlines() {
        return activitycurlines;
    }

    public void setActivitycurlines(int activitycurlines) {
        this.activitycurlines = activitycurlines;
    }

    public int getActivityprivateid() {
        return activityprivateid;
    }

    public void setActivityprivateid(int activityprivateid) {
        this.activityprivateid = activityprivateid;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public int getActivitytotalamount() {
        return activitytotalamount;
    }

    public void setActivitytotalamount(int activitytotalamount) {
        this.activitytotalamount = activitytotalamount;
    }

    public int getActivitystate() {
        return activitystate;
    }

    public void setActivitystate(int activitystate) {
        this.activitystate = activitystate;
    }

    public int getActivityparentid() {
        return activityparentid;
    }

    public void setActivityparentid(int activityparentid) {
        this.activityparentid = activityparentid;
    }

    public int getActivitygroup() {
        return activitygroup;
    }

    public void setActivitygroup(int activitygroup) {
        this.activitygroup = activitygroup;
    }

    public String getActivityprobabilityparameters() {
        return activityprobabilityparameters;
    }

    public void setActivityprobabilityparameters(String activityprobabilityparameters) {
        this.activityprobabilityparameters = activityprobabilityparameters;
    }

    public String getActivitycurlinespeoples() {
        return activitycurlinespeoples;
    }

    public void setActivitycurlinespeoples(String activitycurlinespeoples) {
        this.activitycurlinespeoples = activitycurlinespeoples;
    }
}
