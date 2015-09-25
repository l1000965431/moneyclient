package com.money.Service.alipay;

import java.text.DecimalFormat;

/**
 * Created by happysky on 15-9-24.
 */
public class TransactionData {
    /**
     *  流水号
     */
    private String serialNumber;

    /**
     *  账号
     */
    private String accountId;

    /**
     *  账户名
     */
    private String accountName;

    /**
     *  金额
     */
    private double price;

    /**
     *  备注说明
     */
    private String comment;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toAlipayTransFormat(){
        DecimalFormat df   =   new   DecimalFormat("#####0.00");
        return serialNumber + "^" + accountId + "^" + accountName + "^"
                + df.format(price) + "^" + comment;
    }
}
