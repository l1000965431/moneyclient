package com.money.Service.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fisher on 2015/7/9.
 */
public class TableConstant {
    /**
     * tableBean 实体类与表名称的对应关系map
     * key：实体类全名
     * value：表名称
     */
    public static final Map<String, String> TABLE_BEAN = new HashMap<String, String>();
    static{
        TABLE_BEAN.put("com.codingyun.core.entity.bo.SysUserBo", "sys_user");
    }

    /**
     * TABLE_PRIMARY_KEY 表名称与该表主键字段的对应关系map
     * key：表名称
     * value：表主键字段名称
     */
    public static final Map<String, String> TABLE_PRIMARY_KEY = new HashMap<String, String>();
    static{
        TABLE_PRIMARY_KEY.put("sys_user", "id");
    }
}
