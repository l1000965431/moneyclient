package until.Hibernate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识商业对象不能被删除,只能被设为无效的Annoation.
 * <p/>
 * 相比inferface的标示方式，annotation 方式更少侵入性,可以定义任意属性代表status,而默认为status属性.
 * <p>User: liumin
 * <p>Date: 15-7-8 下午4:19
 * <p>Version: 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IUndeletable {
    String status() default IUndeleteableEntityOperation.STATUS;
}

