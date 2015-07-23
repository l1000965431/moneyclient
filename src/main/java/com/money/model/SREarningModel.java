package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 小R收益层次分布
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "srearning")
public class SREarningModel implements Serializable {
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
    @JoinColumn( name = "groupId",referencedColumnName = "id")
    ActivityGroupModel activityGroupModel;

    /**
     *  收益金额
     */
    int earningPrice;

    /**
     *  当前收益金额个数
     */
    int num;

    /**
     *  当前收益金额基础中奖几率
     */
    float baseProbability;

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

    public int getEarningPrice() {
        return earningPrice;
    }

    public void setEarningPrice(int earningPrice) {
        this.earningPrice = earningPrice;
    }

    public float getBaseProbability() {
        return baseProbability;
    }

    public void setBaseProbability(float baseProbability) {
        this.baseProbability = baseProbability;
    }
}
