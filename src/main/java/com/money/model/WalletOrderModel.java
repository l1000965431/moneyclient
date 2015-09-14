package com.money.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by liumin on 15/8/9.
 */

@Entity
@Table(name = "walletorder")
@DynamicUpdate(true)
public class WalletOrderModel {

    /**
     * 订单ID
     */
    @Id
    String OrderID;

    /**
     * 订单日期
     */
    Date OrderDate;

    /**
     * 充值金额
     */
    int WalletLines;

    /**
     * 充值渠道
     */
    String WalletChannel;

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public Date getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(Date orderDate) {
        OrderDate = orderDate;
    }

    public int getWalletLines() {
        return WalletLines;
    }

    public void setWalletLines(int walletLines) {
        WalletLines = walletLines;
    }

    public String getWalletChannel() {
        return WalletChannel;
    }

    public void setWalletChannel(String walletChannel) {
        WalletChannel = walletChannel;
    }
}
