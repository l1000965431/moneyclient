package com.money.model;

/**
 * 项目分期
 * <p>User: liumin
 * <p>Date: 15-7-22
 * <p>Version: 1.0
 */

public class ActivityInstallmentModel extends BaseModel {

    /**
     * 项目分期ID
     */
    String InstallmentID;

    /**
     * 项目ID 每个分期对应一个新的项目ID
     */
    String ActivityID;

    /**
     * 需筹的金额
     */
    int InstallmentLines;

    /**
     * 需筹的总金额
     */
    int TotalLines;

    /**
     * 发布时间
     */
    String StratTime;

    /**
     * 结束时间
     */
    String EndTime;

    /**
     * 分期项目状态
     */
    int State;

    public String getInstallmentID() {
        return InstallmentID;
    }

    public void setInstallmentID(String installmentID) {
        InstallmentID = installmentID;
    }

    public int getInstallmentLines() {
        return InstallmentLines;
    }

    public void setInstallmentLines(int installmentLines) {
        InstallmentLines = installmentLines;
    }

    public int getTotalLines() {
        return TotalLines;
    }

    public void setTotalLines(int totalLines) {
        TotalLines = totalLines;
    }

    public String getStratTime() {
        return StratTime;
    }

    public void setStratTime(String stratTime) {
        StratTime = stratTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }


    public boolean equals( Object obj ){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        ActivityInstallmentModel other = (ActivityInstallmentModel)obj;

        if( other.getActivityID() == this.getActivityID() ){
            return true;
        }else{
            return false;
        }
    }

}
