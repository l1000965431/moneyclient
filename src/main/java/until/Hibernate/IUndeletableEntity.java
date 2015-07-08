package until.Hibernate;
/**
 * 标识商业对象不能被删除,只能被设为无效的接口.
 * <p>User: liumin
 * <p>Date: 15-7-8 下午4:19
 * <p>Version: 1.0
 */
public interface IUndeletableEntity {
    void setStatus(String status);
}
