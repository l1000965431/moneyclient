package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 邀请码存储
 * <p>User: 刘旻
 * <p>Date: 15-10-4
 * <p>Version: 1.0
 */

@Entity(name = "invitecode")
@Table
public class InviteCodeModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    /**
     * 邀请码
     */
    String inviteCode;

    /**
     * 用户ID
     */
    String userId="0";

    /**
     * 使用日期
     */
    Date useDate;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getUseDate() {
        return useDate;
    }

    public void setUseDate(Date useDate) {
        this.useDate = useDate;
    }
}
