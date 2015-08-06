package com.money.dao.LotteryDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.model.LotteryPeoples;
import org.springframework.stereotype.Repository;
import java.util.List;

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
    public List GetRandNotLottery( String activityID,int Peoples ){
        String DBName = Config.ACTIVITYGROUPTICKETNAME+activityID;
        //小R中奖查询
        String sql = "SELECT * FROM "+DBName+" where PurchaseType = 2 order by rand() limit "+Integer.toString( Peoples )+";";
        //大R中奖查询
        String sql1 = "SELECT * FROM "+DBName+" where PurchaseType = 1;";
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
