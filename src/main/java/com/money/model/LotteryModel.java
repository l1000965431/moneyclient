package com.money.model;

import java.io.Serializable;

/**
 * Created by liumin on 15/7/17.
 */
public class LotteryModel implements Serializable {

    int id;

    int userid;

    int lottery;

    int lines1;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getLines() {
        return lines1;
    }

    public void setLines(int lines) {
        this.lines1 = lines;
    }

    public int getLottery() {
        return lottery;
    }

    public void setLottery(int lottery) {
        this.lottery = lottery;
    }
}
