package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by liumin on 15/8/20.
 */

@Entity
@Table(name = "EarningsRecord")
public class EarningsRecordModel {

    @Id
    String activityStageId;

    Date EndDate;

    int TotalFund;

    int TotalPrize;

    String ActivityID;

    public String getActivityStageId() {
        return activityStageId;
    }

    public void setActivityStageId(String activityStageId) {
        this.activityStageId = activityStageId;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public int getTotalFund() {
        return TotalFund;
    }

    public void setTotalFund(int totalFund) {
        TotalFund = totalFund;
    }

    public int getTotalPrize() {
        return TotalPrize;
    }

    public void setTotalPrize(int totalPrize) {
        TotalPrize = totalPrize;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }
}
