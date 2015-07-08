package until.Hibernate;

/**
 * Created by liumin on 15/7/8.
 */
import java.io.Serializable;
import java.util.List;

/**
 * 针对单个Entity对象的CRUD操作定义.
 * <p>User: seele
 * <p>Date: 15-7-8 下午5:55
 * <p>Version: 1.0
 */
public interface IEntityDao<T,PK extends Serializable> {
    T get(PK id);
    List<T> getAll();
    void save(T entity);
    void remove(T entity);
    void removeById(PK id);

    void update(T entity);
    /**
     * 获取Entity对象的主键名.
     */
    String getIdName(Class<T> clazz);
}
