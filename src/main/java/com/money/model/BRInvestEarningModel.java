package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 大R的投资与收益分布
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "brinvestearning")
public class BRInvestEarningModel extends BaseModel {
    /**
     *  ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     *  项目组ID
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn( name = "activityId", referencedColumnName = "activityStageId")
    ActivityDynamicModel activityDynamicModel;

    /**
     *  投资金额
     */
    int investPrice;

    /**
     *  当前投资金额数量
     */
    int num;

    /**
     *  投资金额对应的收益
     */
    int earningPrice;

    /**
     *  已经卖出去了多少张
     */
    int soldNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityDynamicModel getActivityDynamicModel() {
        return activityDynamicModel;
    }

    public void setActivityDynamicModel(ActivityDynamicModel activityDetailModel) {
        this.activityDynamicModel = activityDetailModel;
    }

    public int getInvestPrice() {
        return investPrice;
    }

    public void setInvestPrice(int investPrice) {
        this.investPrice = investPrice;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getEarningPrice() {
        return earningPrice;
    }

    public void setEarningPrice(int earningPrice) {
        this.earningPrice = earningPrice;
    }

    public int getSoldNum() {
        return soldNum;
    }

    public void setSoldNum(int soldNum) {
        this.soldNum = soldNum;
    }
}
