package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户充值钱包表
 * <p>User: liumin
 * <p>Date: 15-7-24
 * <p>Version: 1.0
 */

/*@Entity
@Table(name = "recharge")*/
public class RechargeModel extends BaseModel{

    @Id
    String id;

    /**
     * 充值时间
     */
    Date RechargeTime;

    /**
     * 充值金额
     */
    int RechargeLines;

    /**
     * 充值用户ID
     */
    String RechargeUserID;

    /**
     * 充值方式
     */
    int RechargeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Date getRechargeTime() {
        return RechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        RechargeTime = rechargeTime;
    }

    public int getRechargeLines() {
        return RechargeLines;
    }

    public void setRechargeLines(int rechargeLines) {
        RechargeLines = rechargeLines;
    }

    public String getRechargeUserID() {
        return RechargeUserID;
    }

    public void setRechargeUserID(String rechargeUserID) {
        RechargeUserID = rechargeUserID;
    }

    public int getRechargeType() {
        return RechargeType;
    }

    public void setRechargeType(int rechargeType) {
        RechargeType = rechargeType;
    }
}
