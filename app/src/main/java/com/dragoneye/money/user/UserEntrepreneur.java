package com.dragoneye.money.user;

import com.dragoneye.money.protocol.UserProtocol;

/**
 * Created by happysky on 15-7-22.
 */
public class UserEntrepreneur extends UserBase {
    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String identityId;

    /**
     * 个人简介
     */
    private String introduce;

    /**
     * 教育经历
     */
    private String eduInfo;

    /**
     * 事业经历
     */
    private String career;

    @Override
    protected void initUserType(){
        setUserType(UserProtocol.PROTOCOL_USER_TYPE_ENTREPRENEUR);
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getEduInfo() {
        return eduInfo;
    }

    public void setEduInfo(String eduInfo) {
        this.eduInfo = eduInfo;
    }
}
