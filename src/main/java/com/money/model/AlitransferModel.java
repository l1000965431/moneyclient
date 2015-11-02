package com.money.model;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by liumin on 15/9/25.
 */

@Entity(name = "alitransfer")
@Table
public class AlitransferModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    /**
     * 用户ID
     */
    String UserId = "";

    /**
     * 批量付款是否失败
     */
    boolean IsFaliled = false;

    /**
     * 付款金额
     */
    int TransferLines = 0;

    /**
     * 付款最后时间
     */
    Date  AlitransferDate;

    /**
     * 真实姓名
     */
    String RealName;

    /**
     * 支付宝帐号
     */
    String AliEmail;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isFaliled() {
        return IsFaliled;
    }

    public void setIsFaliled(boolean isFaliled) {
        IsFaliled = isFaliled;
    }

    public int getLines() {
        return TransferLines;
    }

    public void setLines(int lines) {
        TransferLines = lines;
    }

    public Date getAlitransferDate() {
        return AlitransferDate;
    }

    public void setAlitransferDate(Date alitransferDate) {
        AlitransferDate = alitransferDate;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getAliEmail() {
        return AliEmail;
    }

    public void setAliEmail(String aliEmail) {
        AliEmail = aliEmail;
    }

    public String toAlipayTransFormat(){
        DecimalFormat df   =   new   DecimalFormat("#####0.00");
        return Integer.toString( Id ) + "^" + AliEmail + "^" + RealName + "^"
                + df.format(TransferLines) + "^" + "微距竞投中奖打款";
    }
}
