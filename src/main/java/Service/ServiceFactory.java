package Service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by liumin on 15/7/3.
 */
public class ServiceFactory extends ApplicationObjectSupport {

    static  ServiceFactory servicefactory = null;

    ServiceFactory(){
        servicefactory = this;
        //context = new ClassPathXmlApplicationContext("xml/Spring-Service-Config.xml");
    }

    static public ServiceBase getService( String name ){
        try {
            return (ServiceBase) servicefactory.getApplicationContext().getBean(name);
        }catch ( Exception e ){
            return null;
        }
    }
}
