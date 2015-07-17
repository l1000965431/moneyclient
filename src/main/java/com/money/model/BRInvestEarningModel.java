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
public class BRInvestEarningModel implements Serializable {
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
    @JoinColumn( name = "activityId",referencedColumnName = "id")
    ActivityDetailModel activityDetailModel;

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
}
