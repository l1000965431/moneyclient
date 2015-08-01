package com.money.model;

import javax.persistence.*;

/**
 * Created by liumin on 15/7/25.
 */

@Entity
@javax.persistence.Table(name = "PrizeList")
public class PrizeListModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;

    /**
     * 分期项目ID
     */
    String ActivityIID;

    /**
     * 发奖的情况
     */
    @Column(columnDefinition="TEXT")
    String PrizeSituation;

    /**
     * 是否已经发奖
     */
    boolean IsPrize;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPrizeSituation() {
        return PrizeSituation;
    }

    public void setPrizeSituation(String prizeSituation) {
        PrizeSituation = prizeSituation;
    }

    public boolean isPrize() {
        return IsPrize;
    }

    public void setIsPrize(boolean isPrize) {
        IsPrize = isPrize;
    }

    public String getActivityIID() {
        return ActivityIID;
    }

    public void setActivityIID(String activityIID) {
        ActivityIID = activityIID;
    }
}
