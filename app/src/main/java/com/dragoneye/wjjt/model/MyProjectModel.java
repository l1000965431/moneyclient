package com.dragoneye.wjjt.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by happysky on 15-8-12.
 */
public class MyProjectModel implements Serializable{
    /**
     * 初次审核
     */
    public static final int STATUS_FIRST_AUDITING = 0;

    /**
     * 需修改
     */
    public static final int STATUS_NEED_REVAMP = 1;

    /**
     * 修改完成需再次审核
     */
    public static final int STATUS_REVAMPED = 2;

    /**
     * 通过审核(进入项目库保留)
     */
    public static final int STATUS_AUDITOR_PASS_AND_KEEP = 3;

    /**
     * 等待客服联系
     */
    public static final int STATUS_WILL_BE_USE = 4;

    /**
     * 已开始上线筹款
     */
    public static final int STATUS_START_RAISE = 5;

    /**
     * 筹款结束
     */
    public static final int STATUS_RAISE_FINISH = 6;

    /**
     * 未通过审核
     */
    public static final int STATUS_AUDITOR_NOT_PASS = 7;

    /**
     * 项目ID
     */
    Long id;

    /**
     *  审核状态
     */
    int auditorStatus;

    /**
     *  项目提交者ID
     */
    String creatorId;

    /**
     *  项目审核者ID
     */
    String auditorId;

    /**
     *  项目修改次数
     */
    int revampCount;

    /**
     *  项目提交时间
     */
    String createDate;

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
     * 盈利模式
     */
    String profitMode;

    /**
     * 团队介绍
     */
    String teamIntroduce;

    /**
     * 标签
     */
    String tags;

    /**
     * 筹资金额
     */
    int targetFund;

    /**
     * 筹资天数
     */
    int raiseDay;

    /**
     * 团队人数
     */
    int teamSize;

    /**
     * 审核不同过原因
     */
    String noaudireason;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAuditorStatus() {
        return auditorStatus;
    }

    public void setAuditorStatus(int auditorStatus) {
        this.auditorStatus = auditorStatus;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public Date getFormatCreateDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(format);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getNoaudireason() {
        return noaudireason;
    }

    public void setNoaudireason(String noaudireason) {
        this.noaudireason = noaudireason;
    }

    public int getRevampCount() {
        return revampCount;
    }

    public void setRevampCount(int revampCount) {
        this.revampCount = revampCount;
    }

    public String getProfitMode() {
        return profitMode;
    }

    public void setProfitMode(String profitMode) {
        this.profitMode = profitMode;
    }
}
