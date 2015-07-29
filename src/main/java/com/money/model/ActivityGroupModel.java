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
/*@Entity
@Table(name = "activitygroup")*/
public class ActivityGroupModel extends BaseModel{
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
    Set<ActivityDynamicModel> activityDynamicModes = new HashSet<ActivityDynamicModel>();

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
     * 投资总金额
     */
    int investAmount;

    /**
     * 收益总金额
     */
    int earningAmount;


    /**
     *  小R需投资金额
     */
    int srInvestAmount;

    /**
     *  小R投资占比
     */
    float srInvestProportion;

    /**
     *  大R需投资金额
     */
    int brInvestAmount;

    /**
     *  大R需投资占比
     */
    float brInvestProportion;

    /**
     *  小R收益金额
     */
    int srEarningAmount;

    /**
     *  小R收益占比
     */
    float srEarningProportion;

    /**
     *  大R收益金额
     */
    int brEarningAmount;

    /**
     *  大R收益占比
     */
    float brEarningProportion;

    /**
     * rc开头函数会重新计算大R与小R占比与金额
     * @param srInvestProportion
     */
    public void rcSetSrInvestProportion(float srInvestProportion) {
        int investAmount = getInvestAmount();
        int srInvestAmount = (int)(investAmount * srInvestProportion);
        srInvestAmount = srInvestAmount - srInvestAmount % 2;
        setSrInvestAmount(srInvestAmount);
        setBrInvestAmount(investAmount - srInvestAmount);
        setSrInvestProportion( (float)srInvestAmount / investAmount );
        setBrInvestProportion( (float)getBrInvestAmount() / investAmount );
    }

    public void rcSetBrInvestProportion(float brInvestProportion) {
        rcSetSrInvestProportion(1.0f - brInvestProportion);
    }

    public void rcSetSrInvestAmount(int srInvestAmount) {
        int investAmount = getInvestAmount();
        srInvestAmount = srInvestAmount - srInvestAmount % 2;
        float srInvestProportion = srInvestAmount / (float)investAmount;
        rcSetSrInvestProportion(srInvestProportion);
    }

    public void rcSetBrInvestAmount(int brInvestAmount) {
        int investAmount = getInvestAmount();
        brInvestAmount = brInvestAmount - brInvestAmount % 2;
        float brInvestProportion = brInvestAmount / (float)investAmount;
        rcSetBrInvestProportion(brInvestProportion);
    }

    public void rcSetSrEarningProportion(float srEarningProportion) {
        int earningAmount = getEarningAmount();
        int srEarningAmount = (int)(earningAmount * srEarningProportion);
        setSrEarningAmount(srEarningAmount);
        setBrEarningAmount(earningAmount - srEarningAmount);
        setSrEarningProportion(srEarningProportion);
        setBrEarningProportion(1.0f - srEarningProportion);
    }

    public void rcSetBrEarningProportion(float brEarningProportion) {
        rcSetSrEarningProportion(1.0f - brEarningProportion);
    }

    public void rcSetSrEarningAmount(int srEarningAmount) {
        int earningAmount = getEarningAmount();
        setSrEarningAmount( srEarningAmount );
        setSrEarningProportion( srEarningAmount / (float)earningAmount );
        setBrEarningAmount( earningAmount - srEarningAmount );
        setBrEarningProportion( 1.0f - getSrEarningProportion() );
    }

    public void rcSetBrEarningAmount(int brEarningAmount) {
        int earningAmount = getEarningAmount();
        rcSetSrEarningAmount( earningAmount - brEarningAmount );
    }

    public Set<SRInvestTicketModel> getSrInvestTicketModels() {
        return srInvestTicketModels;
    }

    public void setSrInvestTicketModels(Set<SRInvestTicketModel> srInvestTicketModels) {
        this.srInvestTicketModels = srInvestTicketModels;
    }

    public Set<ActivityDynamicModel> getActivityDynamicModes() {
        return activityDynamicModes;
    }

    public void setActivityDynamicModes(Set<ActivityDynamicModel> activityDetailModels) {
        this.activityDynamicModes = activityDetailModels;
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

    public int getSrInvestAmount() {
        return srInvestAmount;
    }

    public void setSrInvestAmount(int srInvestAmount) {
        this.srInvestAmount = srInvestAmount;
    }

    public int getBrInvestAmount() {
        return brInvestAmount;
    }

    public void setBrInvestAmount(int brInvestAmount) {
        this.brInvestAmount = brInvestAmount;
    }

    public int getSrEarningAmount() {
        return srEarningAmount;
    }

    public void setSrEarningAmount(int srEarningAmount) {
        this.srEarningAmount = srEarningAmount;
    }

    public int getBrEarningAmount() {
        return brEarningAmount;
    }

    public void setBrEarningAmount(int brEarningAmount) {
        this.brEarningAmount = brEarningAmount;
    }

    public Set<SREarningModel> getSrEarningModels() {
        return srEarningModels;
    }

    public void setSrEarningModels(Set<SREarningModel> srEarningModels) {
        this.srEarningModels = srEarningModels;
    }

    public float getSrInvestProportion() {
        return srInvestProportion;
    }

    public void setSrInvestProportion(float srInvestProportion) {
        this.srInvestProportion = srInvestProportion;
    }

    public float getBrInvestProportion() {
        return brInvestProportion;
    }

    public void setBrInvestProportion(float brInvestProportion) {
        this.brInvestProportion = brInvestProportion;
    }

    public float getSrEarningProportion() {
        return srEarningProportion;
    }

    public void setSrEarningProportion(float srEarningProportion) {
        this.srEarningProportion = srEarningProportion;
    }

    public float getBrEarningProportion() {
        return brEarningProportion;
    }

    public void setBrEarningProportion(float brEarningProportion) {
        this.brEarningProportion = brEarningProportion;
    }


    public void setInvestAmount(int investAmount) {
        this.investAmount = investAmount;
    }

    public void setEarningAmount(int earningAmount) {
        this.earningAmount = earningAmount;
    }

    public int getInvestAmount() {
        return investAmount;
    }

    public int getEarningAmount() {
        return earningAmount;
    }
}
