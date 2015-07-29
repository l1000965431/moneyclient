package com.money.dao.LotteryDAO;

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

        String sql = "SELECT * FROM sqlrandtset order by rand() limit "+Integer.toString( Peoples );

        try{
            List list = this.getListClassBySQL(sql, LotteryPeoples.class );
            return list;
        }catch ( Exception e ){
         return null;
        }
    }

}
