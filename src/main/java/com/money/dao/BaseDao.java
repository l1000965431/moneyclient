package com.money.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import until.CallbackFunction;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @Title: BaseDao.java
 * @Package tdxy.dao
 * @Description: TODO(baseDao 数据库操作实现类)
 * @author dapeng
 * @date 2014年5月7日 下午5:09:22
 * @version V1.0
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
        return sessionFactory.getCurrentSession();
    }

    /**
     * openSession 需要手动关闭session 意思是打开一个新的session
     *
     * @return
     */
    public Session getNewSession() {
        return sessionFactory.openSession();
    }

    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }

    /**
     * 根据 id 查询信息
     *
     * @param id
     * @return
     */
    @SuppressWarnings("rawtypes")
<<<<<<< HEAD
    public Object load(Class c, Serializable id) {
=======
    public Object load(Class c, String id) {
>>>>>>> eedba10b4e60ec96c2369787da02be17a7605fff
        Session session = getNewSession();
        return session.get(c, id);
    }

    public <T> T load( T c, String id) {
        Session session = getNewSession();
        return (T)session.get(c.getClass(), id);
    }

    /**
     * 获取所有信息
     *
     * @param c
     *
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    public List getAllList(Class c) {
        String hql = "from " + c.getName();
        Session session = getSession();
        return session.createQuery(hql).list();
    }

    /**
     * 获取所有信息
     *
     * @param c
     *
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    public List getAllList(Class c, String orderBy, boolean isAsc) {
        if( isAsc ){
            return getNewSession().createCriteria(c).addOrder(Order.asc(orderBy)).list();
        }else {
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
        String hql = "select count(*) from " + c.getName();
        Long count = (Long) session.createQuery(hql).uniqueResult();
        session.close();
        return count != null ? count.longValue() : 0;
    }

    public Long getTotalCount(String name) {
        Session session = getNewSession();
        String hql = "select count(*) from " + name;
        Long count = (Long) session.createQuery(hql).uniqueResult();
        session.close();
        return count != null ? count.longValue() : 0;
    }

    /**
     * 保存
     *
     * @param bean
     *
     */
    public void save(Object bean) {
        try {
            Session session = getNewSession();
            session.save(bean);
            session.flush();
            session.clear();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新
     *
     * @param bean
     *
     */
    public void update(Object bean) {
        Session session = getNewSession();
        session.update(bean);
        session.flush();
        session.clear();
        session.close();
    }

    /**
     * 删除
     *
     * @param bean
     *
     */
    public void delete(Object bean) {
        Session session = getNewSession();
        session.delete(bean);
        session.flush();
        session.clear();
        session.close();
    }

    /**
     * 根据ID删除
     *
     * @param c 类
     *
     * @param id ID
     *
     */
    @SuppressWarnings({ "rawtypes" })
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
     * @param c 类
     *
     * @param ids ID 集合
     *
     */
    @SuppressWarnings({ "rawtypes" })
    public void delete(Class c, String[] ids) {
        for (String id : ids) {
            Object obj = getSession().get(c, id);
            if (obj != null) {
                getSession().delete(obj);
            }
        }
    }


    /**
     * 原生SQL语句查询  跨表查询的时候 只能跨两张表进行查询
     *
     * @param sql
     *
     * @return
     *
     */

    public List getListBySQL(String sql ){
        Session session = getNewSession();
        try{
            List list = session.createSQLQuery( sql ).list();
            return list;
        }catch ( Exception e ){
            return null;
        }
    }


    /**
     * 使用注解名进行查询  跨表查询的时候 只能跨两张表进行查询
     *
     * @param sqlname  注解名称
     *
     * @return
     *
     */

    public List getListByNameNative(String sqlname ){
        Session session = getNewSession();
        try{
            List list = session.getNamedQuery(sqlname ).list();
            return list;
        }catch ( Exception e ){
            return null;
        }
    }

    /**
     * 执行事务  目前支持只原生的SQL 后端因为使用mycat 所以原生SQL效率更高 出错率更低
     *
     * @param sql
     *
     * @return
     *
     */

    public String excuteTransactionBySQL(String sql ){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        t.begin();
        try{
            session.createSQLQuery( sql ).executeUpdate();
            t.commit();
            return "SUCCESS";
        }catch ( Exception e ){
            t.rollback();
            return "FAILED";
        }
    }

    /**
     * 通过回掉函数执行事务
     * @param callbackFunction  回掉函数
     * @return
     */
    public String excuteTransactionByCallback( CallbackFunction callbackFunction ){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try{
            if( callbackFunction != null ){
                callbackFunction.callback();
            }
            t.commit();
            return "SUCCESS";
        }catch ( Exception e ){
            t.rollback();
            return "FAILED";
        }
    }

    /**
     * 通过回掉函数执行事务
     * @param callbackFunction  回掉函数
     * @return
     */
    public String excuteTransactionByCallback( CallbackFunction callbackFunction,Object object ){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try{
            if( callbackFunction != null ){
                callbackFunction.callback(object);
            }
            t.commit();
            return "SUCCESS";
        }catch ( Exception e ){
            t.rollback();
            return "FAILED";
        }
    }

    /**
     * 通过回掉函数执行事务
     * @param callbackFunction  回掉函数
     * @return
     */
    public String excuteTransactionByCallback( TransactionCallback callbackFunction ){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try{
            if( callbackFunction != null ){
                callbackFunction.callback(session);
            }
            t.commit();
            return "SUCCESS";
        }catch ( Exception e ){
            t.rollback();
            return "FAILED";
        }
    }

    /**
     * 执行事务  目前支持只原生的SQL 后端因为使用mycat 所以原生SQL效率更高 出错率更低
     *
     * @param sqlname
     *
     * @return
     *
     */

    public String excuteTransactionByNameNative(String sqlname ){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        t.begin();
        try{
            session.getNamedQuery( sqlname ).executeUpdate();
            t.commit();
            return "SUCCESS";
        }catch ( Exception e ){
            t.rollback();
            return "FAILED";
        }
    }

    /**
     * 判断一张表是否存在
     * @param ModelName
     * @return
     */
    public boolean IsModelExist( String ModelName ){
        Session session = getNewSession();

        return true;
    }


}

