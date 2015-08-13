package com.money.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liumin on 15/8/13.
 */
@Entity
@Table(name = "UserEarnings")
public class UserEarningsModel extends BaseModel {

    @Id
   String UserID;

    @Column(columnDefinition="TEXT")
    String UserEarnings = "";


    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserEarnings() {
        return UserEarnings;
    }

    public void setUserEarnings(String userEarnings) {
        UserEarnings = userEarnings;
    }
}
