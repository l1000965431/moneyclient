package Service;

import java.util.Random;

/**
 * 服务基类
 * 服务类必须是无状态的,状态有外部参数控制  服务只负责实现过程
 * <p>User: seele
 * <p>Date: 15-7-7 下午5:43
 * <p>Version: 1.0
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

    public ServiceBase(){

    };


    public String Excute() {
        return null;
    }
}
