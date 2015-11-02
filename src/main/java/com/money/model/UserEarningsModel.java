package com.money.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liumin on 15/8/13.
 */
@Entity(name = "UserEarnings")
@Table
@DynamicUpdate(true)
public class UserEarningsModel extends BaseModel {

    public static final int ACTIVITYTYPE = 1;
    public static final int ACTIVITYPREFERENTIALTYPE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    String UserID;

    Date UserEarningsDate;

    int UserEarningLines;

    String ActivityStageId;

    int UserEarningsType;

    int PurchaseType = 0;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public Date getUserEarningsDate() {
        return UserEarningsDate;
    }

    public void setUserEarningsDate(Date userEarningsDate) {
        UserEarningsDate = userEarningsDate;
    }

    public int getUserEarningLines() {
        return UserEarningLines;
    }

    public void setUserEarningLines(int userEarningLines) {
        UserEarningLines = userEarningLines;
    }

    public String getActivityStageId() {
        return ActivityStageId;
    }

    public void setActivityStageId(String activityStageId) {
        ActivityStageId = activityStageId;
    }

    public int getUserEarningsType() {
        return UserEarningsType;
    }

    public void setUserEarningsType(int userEarningsType) {
        UserEarningsType = userEarningsType;
    }

    public int getPurchaseType() {
        return PurchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        PurchaseType = purchaseType;
    }
}
