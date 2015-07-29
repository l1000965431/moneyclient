package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by GuoHong on 15-7-27.
 *
 */
@Entity
@Table(name = "adminstrator")
public class AdministratorModel implements Serializable {
    /**
     *  系统管理员，账号的增加与删除
     */
    public static final int ADMIN_TYPE_ROOT = 1;
    /**
     *  审核管理员，项目的审核
     */
    public static final int ADMIN_ACTIVITY_AUDIT = 2;
    /**
     *  项目发布员，项目的发布
     */
    public static final int ADMIN_ACTIVITY_PUBLISH = 3;

    /**
     * 管理员账号
     */
    @Id
    String userId;

    /**
     * 管理员密码
     */
    String password;

    /**
     * 管理员名称
     */
    String name;

    /**
     * 管理员类型
     */
    int adminType;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdminType() {
        return adminType;
    }

    public void setAdminType(int adminType) {
        this.adminType = adminType;
    }
}
