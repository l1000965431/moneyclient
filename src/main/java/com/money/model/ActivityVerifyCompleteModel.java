package com.money.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by liumin on 15/7/23.
 */


@Entity
@Table(name = "activityVerifyComplete")
public class ActivityVerifyCompleteModel extends BaseModel {

    /**
     * 项目ID
     */
    @Id
    String activityId;

    /**
     *  项目分期静态列表
     */
    @OneToMany(mappedBy = "activityVerifyCompleteModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ActivityDetailModel> activityDetailModels = new HashSet<ActivityDetailModel>();

    /**
     *  项目分期动态列表
     */
    @OneToMany(mappedBy = "activityVerifyCompleteModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ActivityDynamicModel> activityDynamicModels = new HashSet<ActivityDynamicModel>();

    /**
     *  项目需要筹集的原始金额
     */
    int originalFund;

    /**
     *  小R投资占比
     */
    float srInvestProportion;

    /**
     *  大R投资占比
     */
    float brInvestProportion;

    /**
     * 筹资金额
     */
    int targetFund;

    /**
     * 已筹资金
     */
    int curFund;

    /**
     * 项目状态
     */
    int status;

    /**
     * 项目名称
     */
    String name;

    /**
     * 团队地址
     */
    String address;

    /**
     * 项目视频
     */
    String videoUrl;

    /**
     * 项目图片
     */
    String imageUrl;

    /**
     * 项目简介
     */
    String summary;

    /**
     * 项目介绍
     */
    String activityIntroduce;

    /**
     * 项目类别
     */
    String category;

    /**
     * 市场分析
     */
    String marketAnalysis;

    /**
     * 团队介绍
     */
    String teamIntroduce;

    /**
     * 标签
     */
    String tags;

    /**
     * 开始筹款时间
     */
    Date activityStartTime;

    /**
     * 筹资天数
     */
    int raiseDay;

    /**
     * 团队人数
     */
    int teamSize;

    /**
     * 各金额总人数
     */
    String activityLinesPeoples;

    /**
     * 总期数
     */
    @Column( nullable=false,columnDefinition="INT default 0" )
    int TotalInstallmentNum;

    /**
     * 当前期数
     */
    @Column( nullable=false,columnDefinition="INT default 0" )
    int CurInstallmentNum;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActivityIntroduce() {
        return activityIntroduce;
    }

    public void setActivityIntroduce(String activityIntroduce) {
        this.activityIntroduce = activityIntroduce;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMarketAnalysis() {
        return marketAnalysis;
    }

    public void setMarketAnalysis(String marketAnalysis) {
        this.marketAnalysis = marketAnalysis;
    }

    public String getTeamIntroduce() {
        return teamIntroduce;
    }

    public void setTeamIntroduce(String teamIntroduce) {
        this.teamIntroduce = teamIntroduce;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getTargetFund() {
        return targetFund;
    }

    public void setTargetFund(int targetFund) {
        this.targetFund = targetFund;
    }

    public int getCurFund() {
        return curFund;
    }

    public void setCurFund(int curFund) {
        this.curFund = curFund;
    }

    public Date getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(Date activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public int getRaiseDay() {
        return raiseDay;
    }

    public void setRaiseDay(int raiseDay) {
        this.raiseDay = raiseDay;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public String getActivityLinesPeoples() {
        return activityLinesPeoples;
    }

    public void setActivityLinesPeoples(String activityLinesPeoples) {
        this.activityLinesPeoples = activityLinesPeoples;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityID) {
        this.activityId = activityID;
    }

    public Set<ActivityDetailModel> getActivityDetailModels() {
        return activityDetailModels;
    }

    public void setActivityDetailModels(Set<ActivityDetailModel> activityDetailModels) {
        this.activityDetailModels = activityDetailModels;
    }

    public Set<ActivityDynamicModel> getActivityDynamicModels() {
        return activityDynamicModels;
    }

    public void setActivityDynamicModels(Set<ActivityDynamicModel> activityDynamicModels) {
        this.activityDynamicModels = activityDynamicModels;
    }

    public int getOriginalFund() {
        return originalFund;
    }

    public void setOriginalFund(int originalFund) {
        this.originalFund = originalFund;
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

    public String getProfitMode() {
        return profitMode;
    }

    public void setProfitMode(String profitMode) {
        this.profitMode = profitMode;
    }

    public int getTotalInstallmentNum() {
        return TotalInstallmentNum;
    }

    public void setTotalInstallmentNum(int totalInstallmentNum) {
        TotalInstallmentNum = totalInstallmentNum;
    }

    public int getCurInstallmentNum() {
        return CurInstallmentNum;
    }

    public void setCurInstallmentNum(int curInstallmentNum) {
        CurInstallmentNum = curInstallmentNum;
    }

    public boolean IsEnoughAdvance( int AdvanceNum ){
        if( TotalInstallmentNum-CurInstallmentNum >= AdvanceNum ){
            return true;
        }else{
            return false;
        }
    }

    public boolean IsEnoughInstallmentNum(){
        return ( CurInstallmentNum >= TotalInstallmentNum );
    }

}
