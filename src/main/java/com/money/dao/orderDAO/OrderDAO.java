package com.money.dao.orderDAO;

import com.money.dao.BaseDao;
import com.money.dao.TransactionSessionCallback;
import com.money.model.OrderModel;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Created by liumin on 15/7/7.
 */

@Repository
public class OrderDAO extends BaseDao {

    public List<OrderModel> getOrderByUserID(final String UserID, final int firstPage, final int Num) {

        final List[] list = {null};

        this.excuteTransactionByCallback(new TransactionSessionCallback() {

            public boolean callback(Session session) throws Exception {

                list[0] = session.createCriteria(OrderModel.class)
                        .setMaxResults(Num)
                        .setFirstResult(firstPage * Num)
                        .add(Restrictions.eq("userId", UserID))
                        .addOrder(Order.desc("orderDate"))
                        .list();

                for (Object o : list[0]) {
                    OrderModel orderModel = (OrderModel) o;
                    orderModel.getActivityDetailModel().getActivityVerifyCompleteModel().getStatus();
                    orderModel.getActivityDetailModel().getDynamicModel().getActivityState();
                    orderModel.getActivityDetailModel().getActivityVerifyCompleteModel().getSrEarningModels().size();
                    orderModel.getActivityDetailModel().getSrEarningModels().size();
                }

                return true;
            }
        });
        return list[0];
    }


    public int TestGenTou(){
        String sql = "SELECT sum(orderLines) FROM moneyserver.activityorder where PurchaseType = 2;";
        Session session = getNewSession();

        return Integer.valueOf( session.createSQLQuery(sql).uniqueResult().toString());
    }

    public int TestLingTou(){
        String sql = "SELECT sum(orderLines) FROM moneyserver.activityorder where PurchaseType = 1;";
        Session session = getNewSession();

        return Integer.valueOf( session.createSQLQuery(sql).uniqueResult().toString());
    }

}
