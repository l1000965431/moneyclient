package com.money.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 项目组
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "activitygroup")
public class ActivityGroupModel implements Serializable{
    /**
     *  项目组ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     *  项目组名称
     */
    String name;

    /**
     *  项目组中的所有项目
     */
    @OneToMany(mappedBy = "activityGroupModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ActivityDetailModel> activityDetailModels = new HashSet<ActivityDetailModel>();

    /**
     *  项目组中小R投资层次
     */
    @OneToMany(mappedBy = "activityGroupModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SRInvestTicketModel> srInvestTicketModels = new HashSet<SRInvestTicketModel>();

    /**
     * 项目组中小R收益层次
     */
    @OneToMany(mappedBy = "activityGroupModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SREarningModel> srEarningModels = new HashSet<SREarningModel>();

    /**
     *  小R需投资金额
     */
    float srInvestAmount;

    /**
     *  大R需投资金额
     */
    float brInvestAmount;

    /**
     *  小R收益金额
     */
    float srEarningAmount;

    /**
     *  大R收益金额
     */
    float brEarningAmount;

    /**
     *  小R各个金额的票和票数
     *  存储为json数组
     *  每个元素的key为票价，value为票数
     */
    @Column(columnDefinition = "TEXT")
    String srTickets;

    /**
     *  小R收益层次金额
     *  存储为json数组
     *  每个元素的key为奖励金额，value为数量
     */
    @Column(columnDefinition = "TEXT")
    String srBonus;

    /**
     *  基础中奖几率
     */
    float baseProbability;

    public Set<SRInvestTicketModel> getSrInvestTicketModels() {
        return srInvestTicketModels;
    }

    public void setSrInvestTicketModels(Set<SRInvestTicketModel> srInvestTicketModels) {
        this.srInvestTicketModels = srInvestTicketModels;
    }


    public Set<ActivityDetailModel> getActivityDetailModels() {
        return activityDetailModels;
    }

    public void setActivityDetailModels(Set<ActivityDetailModel> activityDetailModels) {
        this.activityDetailModels = activityDetailModels;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSrInvestAmount() {
        return srInvestAmount;
    }

    public void setSrInvestAmount(float srInvestAmount) {
        this.srInvestAmount = srInvestAmount;
    }

    public float getBrInvestAmount() {
        return brInvestAmount;
    }

    public void setBrInvestAmount(float brInvestAmount) {
        this.brInvestAmount = brInvestAmount;
    }

    public float getSrEarningAmount() {
        return srEarningAmount;
    }

    public void setSrEarningAmount(float srEarningAmount) {
        this.srEarningAmount = srEarningAmount;
    }

    public float getBrEarningAmount() {
        return brEarningAmount;
    }

    public void setBrEarningAmount(float brEarningAmount) {
        this.brEarningAmount = brEarningAmount;
    }

    public String getSrTickets() {
        return srTickets;
    }

    public void setSrTickets(String srTickets) {
        this.srTickets = srTickets;
    }

    public String getSrBonus() {
        return srBonus;
    }

    public void setSrBonus(String srBonus) {
        this.srBonus = srBonus;
    }

    public float getBaseProbability() {
        return baseProbability;
    }

    public void setBaseProbability(float baseProbability) {
        this.baseProbability = baseProbability;
    }

    public Set<SREarningModel> getSrEarningModels() {
        return srEarningModels;
    }

    public void setSrEarningModels(Set<SREarningModel> srEarningModels) {
        this.srEarningModels = srEarningModels;
    }
}
