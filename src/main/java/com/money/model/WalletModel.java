package com.money.model;

import org.hibernate.annotations.DynamicUpdate;

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

@Entity(name = "wallet")
@Table
@DynamicUpdate(true)
public class WalletModel extends BaseModel{

    @Id
    String UserID;

    //钱包金额
    @Column( nullable=false,columnDefinition="INT default 0" )
    int WalletLines;

    /**
     * 微劵
     */
    @Column( nullable=false,columnDefinition="INT default 0" )
    int virtualSecurities;

    /**
     * 领投劵
     */
    @Column( nullable=false,columnDefinition="INT default 0" )
    int ledSecurities;

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

    public boolean IsvirtualSecuritiesEnough( int CostvirtualSecurities ){
        return virtualSecurities>=CostvirtualSecurities;
    }

    public boolean IsledSecuritiesEnough( int CostledSecurities ){
        return ledSecurities >= CostledSecurities;
    }

    public int getVirtualSecurities() {
        return virtualSecurities;
    }

    public void setVirtualSecurities(int virtualSecurities) {
        this.virtualSecurities = virtualSecurities;
    }

    public int getLedSecurities() {
        return ledSecurities;
    }

    public void setLedSecurities(int ledSecurities) {
        this.ledSecurities = ledSecurities;
    }
}
