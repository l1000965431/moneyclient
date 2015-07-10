package Service;

import org.springframework.context.support.ApplicationObjectSupport;

/**
 * Created by liumin on 15/7/3.
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
