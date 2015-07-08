package dao;

/**
 * Created by liumin on 15/7/8.
 */
import java.io.Serializable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.springframework.orm.hibernate3.HibernateTemplate;
import until.Hibernate.HibernateEntityDao;
import until.Hibernate.HibernateGenericDao;
import until.Hibernate.Page;

/**
 * 提供hibernate dao的所有操作,<br>
 * 实现类由spring注入HibernateEntityDao和HibernateGenericDao来实现
 * <p>User: seele
 * <p>Date: 15-7-8 下午5:55
 * <p>Version: 1.0
 */
public class BaseDao<T,PK extends Serializable> implements IBaseDao<T,PK> {
    protected Class<T> entityClass;// DAO所管理的Entity类型.
    private HibernateEntityDao<T,PK> hedao;
    private HibernateGenericDao hgdao;

    public void setHedao(HibernateEntityDao<T, PK> hedao) {
        hedao.setEntityClass(entityClass);
        this.hedao = hedao;
    }
    public void setHgdao(HibernateGenericDao hgdao) {
        this.hgdao = hgdao;
    }

    /**
     *让spring提供构造函数注入
     */
    public BaseDao(Class<T> type) {
        this.entityClass = type;
    }

    public BaseDao(){}

    /**
     * 清除所有对象缓存
     */
    public void clear() {

        hgdao.clear();
    }
    /**
     * 创建Criteria对象.
     * @param criterions 可变的Restrictions条件列表
     */
    public Criteria createCriteria(Criterion... criterions) {

        return hedao.createCriteria(criterions);
    }
    /**
     * 创建Criteria对象，带排序字段与升降序字段.
     */
    public Criteria createCriteria(String orderBy, boolean isAsc,
                                   Criterion... criterions) {

        return hedao.createCriteria(orderBy, isAsc, criterions);
    }
    /**
     * 创建Query对象. 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
     * 留意可以连续设置,如下：
     * <pre>
     * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
     * </pre>
     * 调用方式如下：
     * <pre>
     *        dao.createQuery(hql)
     *        dao.createQuery(hql,arg0);
     *        dao.createQuery(hql,arg0,arg1);
     *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
     * </pre>
     *
     * @param values 可变参数.
     */
    public Query createQuery(String hql, Object... values) {

        return hgdao.createQuery(hql, values);
    }
    /**
     * @param hql 查询sql
     * @param start 分页从哪一条数据开始
     * @param pageSize 每一个页面的大小
     * @param values 查询条件
     * @return page对象
     */
    public Page dataQuery(String hql, int start, int pageSize, Object... values) {

        return hgdao.dataQuery(hql, start, pageSize, values);
    }
    /**
     * 消除与 Hibernate Session 的关联
     * @param entity
     */
    public void evit(T entity) {

        hedao.evict(entity);
    }
    /**
     * 执行本地sql语句获得标量数值列表
     */
    @SuppressWarnings("unchecked")
    public List executeNativeSql(String sql) {

        return hgdao.executeNativeSql(sql);
    }
    /**
     * 根据hql查询,直接使用HibernateTemplate的find函数.
     * @param values 可变参数
     */
    @SuppressWarnings("unchecked")
    public List find(String hql, Object... values) {

        return hgdao.find(hql, values);
    }
    /**
     * 根据属性名和属性值查询对象.
     * @return 符合条件的对象列表
     */
    public List<T> findBy(String propertyName, Object value) {

        return hedao.findBy(propertyName, value);
    }
    /**
     * 根据属性名和属性值查询对象,带排序参数.
     */
    public List<T> findBy(String propertyName, Object value, String orderBy,
                          boolean isAsc) {

        return hedao.findBy(propertyName, value, orderBy, isAsc);
    }
    /**
     * 根据属性名和属性值查询唯一对象.
     * @return 符合条件的唯一对象 or null if not found.
     */
    public T findUniqueBy(String propertyName, Object value) {

        return hedao.findUniqueBy(propertyName, value);
    }
    /**
     * 执行一些必须的sql语句把内存中的对象同步到jdbc的链接中
     */
    public void flush() {

        hgdao.flush();
    }

    /**
     * 根据Serializable类型的id获取实体对象<p/>
     * 实际调用Hibernate的session.load()方法返回实体或其proxy对象. 如果对象不存在，抛出异常.
     * @param id
     */
    public T get(PK id) {

        return hedao.get(id);
    }
    /**
     * 获取实体类型的全部对象
     */
    public List<T> getAll() {

        return hedao.getAll();
    }
    /**
     * 获取全部对象,带排序字段与升降序参数.
     */
    public List<T> getAll(String orderBy, boolean isAsc) {

        return hedao.getAll(orderBy, isAsc);
    }
    /**
     * 直接使用spring提供的HibernateTemplate
     */
    public HibernateTemplate getHibernateTemplate() {

        return hgdao.getHibernateTemplate();
    }
    /**
     * 判断对象某些属性的值在数据库中是否唯一.
     *
     * @param uniquePropertyNames 在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
     */
    public boolean isUnique(T entity, String uniquePropertyNames) {

        return hedao.isUnique(entity, uniquePropertyNames);
    }
    /**
     * 分页查询函数，使用hql.
     *
     * @param pageNo 页号,从1开始.
     */
    public Page pagedQuery(String hql, int pageNo, int pageSize,
                           Object... values) {

        return hgdao.pagedQuery(hql, pageNo, pageSize, values);
    }
    /**
     * 分页查询函数，使用已设好查询条件与排序的<code>Criteria</code>.
     *
     * @param pageNo 页号,从1开始.
     * @return 含总记录数和当前页数据的Page对象.
     */
    public Page pagedQuery(Criteria criteria, int pageNo, int pageSize) {

        return hedao.pagedQuery(criteria, pageNo, pageSize);
    }
    /**
     * 分页查询函数，根据entityClass和查询条件参数创建默认的<code>Criteria</code>.
     *
     * @param pageNo 页号,从1开始.
     * @return 含总记录数和当前页数据的Page对象.
     */
    public Page pagedQuery(int pageNo, int pageSize, Criterion... criterions) {

        return hedao.pagedQuery(pageNo, pageSize, criterions);
    }
    /**
     * 分页查询函数，根据entityClass和查询条件参数,排序参数创建默认的<code>Criteria</code>.
     *
     * @param pageNo 页号,从1开始.
     * @return 含总记录数和当前页数据的Page对象.
     */
    public Page pagedQuery(int pageNo, int pageSize, String orderBy,
                           boolean isAsc, Criterion... criterions) {

        return hedao.pagedQuery(pageNo, pageSize, orderBy, isAsc, criterions);
    }
    /**
     * 删除对象.
     */
    public void remove(T entity) {

        hedao.remove(entity);
    }
    /**
     * 根据ID删除对象.
     */
    public void removeById(PK id) {

        hedao.removeById(id);
    }
    /**
     * 保存对象.<br>
     * 如果对象已在本session中持久化了,不做任何事。<br>
     * 如果另一个seesion拥有相同的持久化标识,抛出异常。<br>
     * 如果没有持久化标识属性,调用save()。<br>
     * 如果持久化标识表明是一个新的实例化对象,调用save()。<br>
     * 如果是附带版本信息的(<version>或<timestamp>)且版本属性表明为新的实例化对象就save()。<br>
     * 否则调用update()重新关联托管对象
     */
    public void save(T entity) {
        hedao.save(entity);
    }

    /**
     * 在不同的session中关联修改过的托管对象
     */
    public void update(T entity){

        hedao.update(entity);
    }
}
