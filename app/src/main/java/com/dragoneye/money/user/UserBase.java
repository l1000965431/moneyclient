package com.dragoneye.money.user;

/**
 * Created by happysky on 15-7-22.
 */
public abstract class UserBase {
    /**
     * 用户名
     */
    private String userId;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 用户类型
     */
    private int userType;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 性别
     */
    public static final int SEXUALITY_MALE = 0;
    public static final int SEXUALITY_FEMALE = 1;
    public static final int SEXUALITY_PRIVACY = 2;
    private int sexuality;

    /**
     * 地址
     */
    private String address;

    /**
     * 是否完善过信息
     */
    private boolean isPerfectInfo;

    UserBase(){
        initUserType();
    }

    protected abstract void initUserType();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSexuality() {
        return sexuality;
    }

    public String getSexualityString(){
        switch (sexuality){
            case SEXUALITY_FEMALE:
                return "女";
            case SEXUALITY_MALE:
                return "男";
            case SEXUALITY_PRIVACY:
                return "保密";
        }
        return "未知";
    }

    public void setSexuality(int sexuality) {
        this.sexuality = sexuality;
    }

    public boolean isPerfectInfo() {
        return isPerfectInfo;
    }

    public void setIsPerfectInfo(boolean isPerfectInfo) {
        this.isPerfectInfo = isPerfectInfo;
    }
}
