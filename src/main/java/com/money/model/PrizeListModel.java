package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liumin on 15/7/25.
 */

@Entity
@javax.persistence.Table(name = "PrizeList")
public class PrizeListModel extends BaseModel {

    /**
     * 分期项目ID
     */
    @Id
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

    /**
     * 发奖日期
     */
    Date PrizeDate;


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

    public Date getPrizeDate() {
        return PrizeDate;
    }

    public void setPrizeDate(Date prizeDate) {
        PrizeDate = prizeDate;
    }
}
