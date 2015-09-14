package com.money.model;

import org.hibernate.annotations.DynamicUpdate;
import until.Base32;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fisher on 2015/7/18.
 */
@Entity
@Table(name = "User")
@DynamicUpdate(true)
public class UserModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //主键，手机号
    //@Index(  )
    private String userId = "";//service里的username
    //登录密码
    private String password = "";
    //用户类型
    private int userType = 0;
    //用户名
    private String userName = "";
    //邮箱
    private String mail = "";
    //性别
    private int sex = 0;
    //所在地
    private String location = "";
    //真实姓名
    private String realName = "";
    //身份证号
    private String identityId = "";
    //擅长领域
    private String expertise = "";
    //个人介绍
    private String introduction = "";
    //教育经历
    private String eduInfo = "";
    //职业生涯
    private String career = "";
    //是否完善过信息
    private boolean IsPerfect = false;
    //用户头像
    private String userHeadPortrait = "";

    //微信公众号关注Id 用于微信提现
    private String wxOpenId = "0";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String encodePassword = Base32.encode(password.getBytes());
        this.password = encodePassword;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null) {
            this.userName = "";
        } else {
            this.userName = userName;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        if (mail == null) {
            this.mail = "";
        } else {
            this.mail = mail;
        }
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null) {
            this.location = "";
        } else {
            this.location = location;
        }
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        if (realName == null) {
            this.realName = "";
        } else {
            this.realName = realName;
        }
    }

    public boolean isPerfect() {
        return IsPerfect;
    }

    public void setIsPerfect(boolean isPerfect) {
        IsPerfect = isPerfect;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        if( expertise == null ){
            this.expertise = "";
        }else{
            this.expertise = expertise;
        }
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        if( introduction == null ){
            this.introduction = "";
        }else{
            this.introduction = introduction;
        }

    }

    public String getEduInfo() {
        return eduInfo;
    }

    public void setEduInfo(String eduInfo) {
        if (eduInfo == null) {
            this.eduInfo = "";
        } else {
            this.eduInfo = eduInfo;
        }
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        if (career == null) {
            this.career = "";
        } else {
            this.career = career;
        }
    }

    public String getUserHeadPortrait() {
        return userHeadPortrait;
    }

    public void setUserHeadPortrait(String UserHeadPortrait) {
        if( UserHeadPortrait == null ){
            userHeadPortrait = "";
        }else{
            userHeadPortrait = UserHeadPortrait;
        }
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }
}
