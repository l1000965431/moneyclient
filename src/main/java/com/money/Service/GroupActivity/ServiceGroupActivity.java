package com.money.Service.GroupActivity;

import com.money.Service.ServiceBase;
import com.money.dao.GeneraDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by happysky on 15-7-15.
 */

@Service("ServiceGroupActivity")
public class ServiceGroupActivity extends ServiceBase {
    @Autowired
    private GeneraDAO baseDao;


}
