package com.money.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户充值钱包表
 * <p>User: liumin
 * <p>Date: 15-7-24
 * <p>Version: 1.0
 */

@Entity
@Table(name = "wallet")
public class WalletModel {

    @Id
    String UserID;

    //钱包金额
    @Column( nullable=false,columnDefinition="INT default 0" )
    int WalletLines;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getWalletLines() {
        return WalletLines;
    }

    public void setWalletLines(int walletLines) {
        WalletLines = walletLines;
    }

    public boolean IsLinesEnough( int CostLines ){
        return WalletLines>=CostLines;
    }


}
