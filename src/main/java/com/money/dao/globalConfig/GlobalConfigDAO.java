package com.money.dao.globalConfig;

import com.money.dao.BaseDao;
import com.money.model.GlobalConfigModel;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by happysky on 15-7-22.
 * 全局参数设置
 */

@Repository
public class GlobalConfigDAO extends BaseDao {

    void SetConfigVaule(String Key, String Vaule) {

        GlobalConfigModel globalConfigModel = (GlobalConfigModel) this.loadNoTransaction(GlobalConfigModel.class, Key);

        if (globalConfigModel == null) {
            return;
        }

        globalConfigModel.setValue(Vaule);
    }

    public void SetConfigVaule(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            SetConfigVaule( entry.getKey(),entry.getValue() );
        }
    }
}
