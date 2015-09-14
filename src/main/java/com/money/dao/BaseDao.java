package com.money.dao;

import com.money.config.Config;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import until.CallbackFunction;

import java.io.Serializable;
import java.util.List;

/**
 * @author dapeng
 * @version V1.0
 * @Title: BaseDao.java
 * @Package tdxy.dao
 * @Description: TODO(baseDao 数据库操作实现类)
 * @date 2014年5月7日 下午5:09:22
 */

public class BaseDao {

    /**
     * Autowired 自动装配 相当于get() set()
     */
    @Autowired
    protected SessionFactory sessionFactory;

    /**
     * gerCurrentSession 会自动关闭session，使用的是当前的session事务
     *
     * @return
     */
    public Session getSession() {
        return sessionFactory.openSession();
    }

    /**
     * openSession 需要手动关闭session 意思是打开一个新的session
     *
     * @return
     */
    public Session getNewSession() {
        return sessionFactory.getCurrentSession();
    }

    public void flush() {
        getNewSession().flush();
    }

    public void clear() {
        getNewSession().clear();
    }

    /**
     * 根据 id 查询信息
     *
     * @param id
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Object load(Class c, Serializable id) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        Object o = session.get(c, id);
        t.commit();
        return o;
    }

    @SuppressWarnings("rawtypes")
    public Object loadNoTransaction(Class c, Serializable id) {
        Session session = getNewSession();
        Object o = session.get(c, id);
        return o;
    }

    @SuppressWarnings("rawtypes")
    public Object load(Class c, String id) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        Object o = session.get(c, id);
        t.commit();

        return o;
    }

    @SuppressWarnings("rawtypes")
    public Object loadNoTransaction(Class c, String id) {
        Session session = getNewSession();
        Object o = session.get(c, id);
        return o;
    }

    public <T> T load(T c, String id) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        T Value = (T) session.get(c.getClass(), id);
        t.commit();
        return Value;
    }

    /**
     * 获取所有信息
     *
     * @param c
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public List getAllList(Class c) {
        String hql = "from " + c.getName();
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        List list = session.createQuery(hql).list();
        t.commit();
        return list;
    }

    /**
     * 获取所有信息
     *
     * @param c
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public List getAllList(Class c, String orderBy, boolean isAsc) {
        if (isAsc) {
            return getNewSession().createCriteria(c).addOrder(Order.asc(orderBy)).list();
        } else {
            return getNewSession().createCriteria(c).addOrder(Order.desc(orderBy)).list();
        }
    }

    /**
     * 获取总数量
     *
     * @param c
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Long getTotalCount(Class c) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        String hql = "select count(*) from " + c.getName();
        Long count = (Long) session.createQuery(hql).uniqueResult();

        if (count == null) {
            count = (long) 0;
        }

        return count;
    }

    public Long getTotalCount(String name) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        String hql = "select count(*) from " + name;
        Long count = (Long) session.createQuery(hql).uniqueResult();
        t.commit();

        if (count == null) {
            count = (long) 0;
        }

        return count;
    }

    /**
     * 保存
     *
     * @param bean
     */
    public Serializable save(Object bean) {
        Serializable result = null;
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            result = session.save(bean);
            //session.flush();
            //session.clear();
            t.commit();
            //session.close();
        } catch (Exception e) {
            //session.clear();
            t.rollback();
        }

        return result;
    }

    public Serializable saveNoTransaction(Object bean) {
        Serializable result;
        Session session = getNewSession();
        result = session.save(bean);
        //session.flush();
        return result;
    }

    public Serializable saveNoTransactionTest(Object bean) {
        Serializable result;
        Session session = getNewSession();
        result = session.save(bean);
        session.flush();
        return result;
    }


    /**
     * 更新
     *
     * @param bean
     */
    public void update(Object bean) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            session.update(bean);
            //session.flush();
            //session.clear();
            t.commit();
        } catch (Exception e) {
            //session.clear();
            t.rollback();
        }

        //session.close();
    }


    /**
     * 更新
     *
     * @param bean
     */
    public void updateNoTransaction(Object bean) {
        Session session = getNewSession();
        session.update(bean);
        session.flush();
    }

    /**
     * 更新
     *
     * @param bean
     */
    public void merageNoTransaction(Object bean) {
        Session session = getNewSession();
        session.merge(bean);
        session.flush();
    }


    /**
     * 更新或插入
     *
     * @param bean
     */
    public void saveOrupdate(Object bean) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            session.saveOrUpdate(bean);
            //session.flush();
            //session.clear();
            t.commit();
        } catch (Exception e) {
            //session.clear();
            t.rollback();
        }
    }

    public void saveOrupdateNoTransaction(Object bean) {
        Session session = getNewSession();
        session.saveOrUpdate(bean);
        //session.flush();
        //session.clear();
    }

    /**
     * 删除
     *
     * @param bean
     */
    public void delete(Object bean) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            session.delete(bean);
            //session.flush();
            //session.clear();
            t.commit();
        } catch (Exception e) {
            //session.clear();
            t.rollback();
        }


        //session.close();
    }

    /**
     * 根据ID删除
     *
     * @param c  类
     * @param id ID
     */
    @SuppressWarnings({"rawtypes"})
    public void delete(Class c, String id) {
        Session session = getNewSession();
        Object obj = session.get(c, id);
        session.delete(obj);
        flush();
        clear();
    }

    /**
     * 批量删除
     *
     * @param c   类
     * @param ids ID 集合
     */
    @SuppressWarnings({"rawtypes"})
    public void delete(Class c, String[] ids) {
        for (String id : ids) {
            Object obj = getNewSession().get(c, id);
            if (obj != null) {
                getNewSession().delete(obj);
            }
        }
    }


    /**
     * 原生SQL语句查询  跨表查询的时候 只能跨两张表进行查询
     *
     * @param sql
     * @return
     */

    public List<Object[]> getListBySQL(String sql) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List list = session.createSQLQuery(sql).list();
            //session.clear();
            t.commit();
            return list;
        } catch (Exception e) {
            //session.clear();
            t.rollback();
            return null;
        }
    }


    public List getListClassBySQL(String sql, Class c) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List list = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(c)).list();
            //session.clear();
            t.commit();
            //session.close();
            return list;
        } catch (Exception e) {
            //session.clear();
            t.rollback();
            //session.close();
            return null;
        }
    }

    public List getListClassBySQLNoTransaction(String sql, Class c) {
        Session session = getNewSession();
        List list = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(c)).list();
        return list;

    }

    /**
     * 原生SQL语句查询  跨表查询的时候 只能跨两张表进行查询
     *
     * @param sql
     * @return
     */

    public String excuteBySQL(String sql) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            session.createSQLQuery(sql).executeUpdate();
            //session.clear();
            //session.close();
            t.commit();
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            //session.clear();
            //session.close();
            t.rollback();
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 原生SQL语句查询  跨表查询的时候 只能跨两张表进行查询
     *
     * @param sql
     * @return 放回结果的行数
     */

    public int excuteintBySQL(String sql) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            int row = session.createSQLQuery(sql).executeUpdate();
            //session.clear();
            t.commit();
            //session.close();
            return row;
        } catch (Exception e) {
            //session.clear();
            t.rollback();
            //session.close();
            return Config.RETURNERROR;
        }
    }

    /**
     * 使用注解名进行查询  跨表查询的时候 只能跨两张表进行查询
     *
     * @param sqlname 注解名称
     * @return
     */

    public List getListByNameNative(String sqlname) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List list = session.getNamedQuery(sqlname).list();
            //session.clear();
            t.commit();
            //session.close();
            return list;
        } catch (Exception e) {
            //session.clear();
            t.rollback();
            //session.close();
            return null;
        }
    }

    /**
     * 通过回掉函数执行事务
     *
     * @param callbackFunction 回掉函数
     * @return
     */
    public String excuteTransactionByCallback(CallbackFunction callbackFunction) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            if (callbackFunction != null) {
                callbackFunction.callback();
            }
            //session.clear();
            t.commit();

            //session.close();
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            //session.clear();
            t.rollback();

            //session.close();
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 通过回掉函数执行事务
     *
     * @param callbackFunction 回掉函数
     * @return
     */
    public String excuteTransactionByCallback(CallbackFunction callbackFunction, Object object) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            if (callbackFunction != null) {
                callbackFunction.callback(object);
            }
            //session.clear();
            t.commit();
            //session.close();
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            //session.clear();
            t.rollback();
            //session.close();
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 通过回掉函数执行事务
     *
     * @param callbackFunction 回掉函数
     * @return
     */
    public String excuteTransactionByCallback(TransactionCallback callbackFunction) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            if (callbackFunction != null) {
                callbackFunction.callback(this);
            }
            //session.clear();
            t.commit();
            //
            //session.close();
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            //session.clear();
            t.rollback();
            //
            //session.close();
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 通过回掉函数执行事务
     *
     * @param transactionSessionCallback 回掉函数
     * @return
     */
    public String excuteTransactionByCallback(TransactionSessionCallback transactionSessionCallback) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            if (transactionSessionCallback == null) {
                t.rollback();
                return Config.SERVICE_FAILED;
            }

            if (transactionSessionCallback.callback(session)) {
                t.commit();
                return Config.SERVICE_SUCCESS;
            } else {
                t.rollback();
                return Config.SERVICE_FAILED;
            }
        }catch( StaleObjectStateException e ){
            t.rollback();
            return Config.SERVICE_FAILED;
        } catch (Exception e) {
            t.rollback();
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 判断一张表是否存在
     *
     * @param ModelName
     * @return
     */
    public boolean IsModelExist(String ModelName) {
        Session session = getNewSession();

        return false;
    }

    /**
     * 条件查询
     */
    public Object load(Class var1, Criterion criterion) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {
            Criteria criteria = session.createCriteria(var1);
            criteria.add(criterion);
            t.commit();
            return criteria.uniqueResult();
        } catch (Exception e) {
            t.rollback();
            return null;
        }

    }


}

