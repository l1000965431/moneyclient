package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by liumin on 15/9/28.
 */

@Entity
@Table(name = "aliTransferNotify")
public class AliTransferNotifyModel extends BaseModel {

    /**
     * 批量付款批次号
     */
    @Id
    String Batchno;

    /**
     * 批量付款帐号
     */
    String Payuserid;

    /**
     * 批量付款帐号姓名
     */
    String Payusername;

    /**
     * 批量付款状态
     */
    int PayStates;

    Date PayDate;

    public String getBatchno() {
        return Batchno;
    }

    public void setBatchno(String batchno) {
        Batchno = batchno;
    }

    public String getPayuserid() {
        return Payuserid;
    }

    public void setPayuserid(String payuserid) {
        Payuserid = payuserid;
    }

    public String getPayusername() {
        return Payusername;
    }

    public void setPayusername(String payusername) {
        Payusername = payusername;
    }

    public int getPayStates() {
        return PayStates;
    }

    public void setPayStates(int payStates) {
        PayStates = payStates;
    }

    public Date getPayDate() {
        return PayDate;
    }

    public void setPayDate(Date payDate) {
        PayDate = payDate;
    }
}
