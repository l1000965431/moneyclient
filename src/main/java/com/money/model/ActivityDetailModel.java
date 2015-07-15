package com.money.model;


import javax.persistence.*;
import java.io.Serializable;

/**
 * 项目已上线
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "activitydetails")
public class ActivityDetailModel implements Serializable {
    /**
     * 项目ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
