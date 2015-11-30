package com.money.dao.LotteryDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.model.LotteryPeoples;
import com.money.model.SREarningModel;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 开奖DAO
 * <p>User: 刘旻
 * <p>Date: 15-7-17
 * <p>Version: 1.0
 */

@Repository
public class LotteryDAO extends BaseDao {

    /**
     * 随机获得未获奖的人
     *
     * Peoples 总共有多少人
     * @return
     */
    public List GetRandNotLottery( String activityID,Set<SREarningModel> srEarningModelSet ){

        int Peoples1 = 0,Peoples2 = 0;

        //计算总共多少人中奖
        for (SREarningModel str : srEarningModelSet) {
            if (str.getEarningType() == Config.PURCHASELOCALTYRANTS)
                Peoples1 += str.getNum();
            else if (str.getEarningType() == Config.PURCHASEPRICKSILK)
                Peoples2 += str.getNum();
        }



        String DBName = Config.ACTIVITYGROUPTICKETNAME+activityID;
        //小R中奖查询
        String sql = "SELECT * FROM "+DBName+" where PurchaseType = 2 order by rand() limit "+Integer.toString( Peoples2 )+";";
        //大R中奖查询
        String sql1 = "SELECT * FROM "+DBName+" where PurchaseType = 1 order by rand() limit "+ Integer.toString( Peoples1 )+";";
        try{
            List list = this.getListClassBySQLNoTransaction(sql, LotteryPeoples.class);
            List list1 = this.getListClassBySQLNoTransaction(sql1, LotteryPeoples.class );
            list.addAll( list1 );
            return list;
        }catch ( Exception e ){
         return null;
        }
    }

}
