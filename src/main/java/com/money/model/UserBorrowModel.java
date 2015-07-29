package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fisher on 2015/7/18.
 */
@Entity
@Table(name = "UserBorrow")
public class UserBorrowModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //主键，手机号
    private String userId;//service里的username
    //身份证
    private String identity;
    //一句话介绍自己
    private String selfIntroduce;
    //擅长领域
    private String goodAtField;
    //教育经历
    private String education;
    //个人介绍
    private String personalProfile;

    private boolean isperfectInfo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSelfIntroduce() {
        return selfIntroduce;
    }

    public void setSelfIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public String getGoodAtField() {
        return goodAtField;
    }

    public void setGoodAtField(String goodAtField) {
        this.goodAtField = goodAtField;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(String personalProfile) {
        this.personalProfile = personalProfile;
    }

    public boolean isperfectInfo() {
        return isperfectInfo;
    }

    public void setIsperfectInfo(boolean isperfectInfo) {
        this.isperfectInfo = isperfectInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
