package com.dragoneye.wjjt.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table EARNING_RECORD.
 */
public class EarningRecord {

    private Long id;
    private Boolean isRead;

    public EarningRecord() {
    }

    public EarningRecord(Long id) {
        this.id = id;
    }

    public EarningRecord(Long id, Boolean isRead) {
        this.id = id;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

}
