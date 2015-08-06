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
public class SREarningModel extends BaseModel {
    /**
     *  ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     *  项目组ID
     */
    @ManyToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER)
    @JoinColumn( name = "activityStageId",referencedColumnName = "activityStageId")
    ActivityDetailModel activityDetailModel;

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
    int earningType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ActivityDetailModel getActivityDetailModel() {
        return activityDetailModel;
    }

    public void setActivityDetailModel(ActivityDetailModel activityDetailModel) {
        this.activityDetailModel = activityDetailModel;
    }

    public int getEarningType() {
        return earningType;
    }

    public void setEarningType(int earningType) {
        this.earningType = earningType;
    }
}
