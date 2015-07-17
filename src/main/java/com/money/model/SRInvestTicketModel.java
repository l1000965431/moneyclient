package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 小R投资层次分布
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "srinvestticket")
public class SRInvestTicketModel implements Serializable {
    /**
     *  ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     *  项目组ID
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false)
    @JoinColumn( name = "groupId",referencedColumnName = "id")
    ActivityGroupModel activityGroupModel;

    /**
     *  投资金额
     */
    int price;

    /**
     *  当前金额层次数量
     */
    int num;

    /**
     *  已经卖出去了多少张
     */
    int soldNum;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityGroupModel getActivityGroupModel() {
        return activityGroupModel;
    }

    public void setActivityGroupModel(ActivityGroupModel activityGroupModel) {
        this.activityGroupModel = activityGroupModel;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSoldNum() {
        return soldNum;
    }

    public void setSoldNum(int soldNum) {
        this.soldNum = soldNum;
    }
}
