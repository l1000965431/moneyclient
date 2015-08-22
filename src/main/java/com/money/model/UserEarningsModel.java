package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liumin on 15/8/13.
 */
@Entity
@Table(name = "UserEarnings")
public class UserEarningsModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    String UserID;

    Date UserEarningsDate;

    int UserEarningLines;

    String ActivityStageId;

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
}
