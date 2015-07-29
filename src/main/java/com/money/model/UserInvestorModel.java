package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by liumin on 15/7/23.
 */
@Entity
@Table(name = "UserInvestor")
public class UserInvestorModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //主键，手机号
    private String userId;//service里的username
    //用户名
    private String userName;
    //邮箱
    private String mail;
    //性别
    private int sex;
    //所在地
    private String location;
    //真实姓名
    private String realName;
    //是否完善过信息
    private boolean IsPerfect;
    //用户类型
    private int userType;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        this.location = location;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPerfect() {
        return IsPerfect;
    }

    public void setIsPerfect(boolean isPerfect) {
        IsPerfect = isPerfect;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
