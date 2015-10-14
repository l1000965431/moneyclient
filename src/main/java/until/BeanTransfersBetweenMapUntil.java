package until;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * bean与Map互相转换工具
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */
public class BeanTransfersBetweenMapUntil {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BeanTransfersBetweenMapUntil.class);


    public static Map<String, Object> TransBean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            LOGGER.error("Object转换Map失败", e);
        }

        return map;

    }

    public static void TransBean2Map(Map<String, Object> map, Object obj) {
        if (map == null || obj == null) {
            return;
        }
        try {
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            LOGGER.error("Map转换Object失败", e);
        }
    }
}
