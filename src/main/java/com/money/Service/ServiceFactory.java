package com.money.Service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * 服务工厂
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

public class ServiceFactory extends ApplicationObjectSupport {

    static  ServiceFactory servicefactory = null;
    ServiceFactory(){
        servicefactory = this;
        //context = new ClassPathXmlApplicationContext("xml/Spring-Service-Config.xml");
    }

    static public <T> T getService( String name ){
        try {
            return (T) servicefactory.getApplicationContext().getBean(name);
        }catch ( Exception e ){
            return null;
        }
    }

}
