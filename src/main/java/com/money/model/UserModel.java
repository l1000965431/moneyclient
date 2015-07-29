package com.money.model;

import until.Base32;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fisher on 2015/7/18.
 */
@Entity
@Table(name = "User")
public class UserModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //主键，手机号
    private String userId;//service里的username
    //登录密码
    private String password;
    //用户类型
    private int userType;
    //用户名
    private String userName;


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
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
