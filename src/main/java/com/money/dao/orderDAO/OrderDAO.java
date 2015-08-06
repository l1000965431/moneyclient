package com.money.dao.orderDAO;

import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.model.OrderModel;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liumin on 15/7/7.
 */

@Repository
public class OrderDAO extends BaseDao {

    public List<OrderModel> getOrderByUserID( final String UserID, final int firstPage, final int Num ){

        final List[] list = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                list[0] = basedao.getNewSession().createCriteria( OrderModel.class )
                        .setMaxResults( Num )
                        .setFirstResult( firstPage*Num )
                        .add(Restrictions.eq("userId", UserID))
                        .list();
            }
        });

        return list[0];
    }


}
