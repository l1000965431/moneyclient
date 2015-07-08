package until.Hibernate;

/**
 * Created by liumin on 15/7/8.
 */
import java.util.List;
import org.hibernate.criterion.Criterion;
/**
 * 定义如果支持Entity不被直接删除必须支持的Operation.
 * <p>User: liumin
 * <p>Date: 15-7-8 下午4:19
 * <p>Version: 1.0
 */
public interface IUndeleteableEntityOperation<T> {
    /*
     * Undelete Entity用到的几个常量,因为要同时兼顾Interface与Annotation,所以集中放此.
     */
    String UNVALID_VALUE = "-1";
    String NORMAL_VALUE = "0";
    String STATUS = "status";
    /**
     * 取得所有状态为有效的对象.
     */
    List<T> getAllValid();
    /**
     * 删除对象，但如果是Undeleteable的entity,设置对象的状态而不是直接删除.
     */
    void remove(Object entity);
    /**
     * 获取过滤已删除对象的hql条件语句.
     */
    String getUnDeletableHQL();
    /**
     * 获取过滤已删除对象的Criterion条件语句.
     */
    Criterion getUnDeletableCriterion();
}
