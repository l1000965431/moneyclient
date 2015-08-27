package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liumin on 15/8/26.
 */

@Entity
@Table(name = "Transfer")
public class TransferModel extends BaseModel {

    @Id
    String orderId;

    Date transferDate;

    int transferLines;

    String userId;

    String openId;

    String status;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public int getTransferLines() {
        return transferLines;
    }

    public void setTransferLines(int transferLines) {
        this.transferLines = transferLines;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
