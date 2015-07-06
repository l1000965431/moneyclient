package Service;

import java.util.Random;

/**
 * Created by liumin on 15/7/3.
 */
public class ServiceBase implements ServiceInterface{
    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {

        int max=20;
        int min=10;
        Random random = new Random();
        ServiceName = serviceName+Integer.toString(random.nextInt(max)%(max-min+1) + min);
    }

    private String ServiceName;

    ServiceBase(){

    };


    public String Excute() {
        return null;
    }
}
