package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fisher on 2015/7/18.
 */
@Entity
@Table(name = "User")
public class UserModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //主键，手机号
    private String userId;//service里的username
    //登录密码
    private String password;
    //用户类型
    private String userType;
    //用户名
    private String userName;
    //邮箱
    private String mail;
    //性别
    private String sex;
    //所在地
    private String location;
    //真实姓名
    private String realName;
    //是否已经完善过信息
    private boolean isperfectInfo;


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
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isperfectInfo() {
        return isperfectInfo;
    }

    public void setIsperfectInfo(boolean isperfectInfo) {
        this.isperfectInfo = isperfectInfo;
    }
}
