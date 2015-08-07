package com.money.dao.PrizeListDAO;

import com.money.dao.BaseDao;
import com.money.model.PrizeListModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Created by liumin on 15/7/31.
 */

@Repository
public class PrizeListDAO extends BaseDao {

    /**
     * 根据分期ID 查询中奖列表
     * @param InstallmentActivityID
     * @return
     */
    public PrizeListModel getListPrizeListModel( String InstallmentActivityID ){

        PrizeListModel prizeListModel = null;
        Session session = getNewSession();
        prizeListModel = (PrizeListModel) session.createCriteria( PrizeListModel.class )
                .add( Restrictions.eq( "ActivityIID",InstallmentActivityID ) )
                .uniqueResult();
        return prizeListModel;

    }

}
