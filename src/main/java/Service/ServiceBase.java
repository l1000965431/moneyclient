package Service;

import java.util.Random;

/**
 * 服务基类
 * 服务类必须是无状态的,状态有外部参数控制  服务只负责实现过程
 * <p>User: seele
 * <p>Date: 15-7-7 下午5:43
 * <p>Version: 1.0
 */
public class ServiceBase {
    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {

        ServiceName = serviceName;
    }

    private String ServiceName;

    public ServiceBase(){

    }


    public String Excute() {
        return null;
    }
}
