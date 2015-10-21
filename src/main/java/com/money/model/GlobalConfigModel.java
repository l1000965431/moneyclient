package com.money.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 全局键值对表，存储喊卡系数，分成比例等
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity(name = "globalconfig")
@Table
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class  GlobalConfigModel extends BaseModel{

    /**
     * 值
     */

    @Column(columnDefinition = "TEXT")
    String value;

    /**
     * 键
     */
    @Id
    String ConfigKey;

    public String getConfigkey() {
        return ConfigKey;
    }

    public void setConfigkey(String configkey) {
        ConfigKey = configkey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
