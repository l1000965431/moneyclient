package until;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Component;

/**
 * Created by liumin on 15/7/31.
 */

@Component
public class PRestSmsSDKUntil {

    static CCPRestSmsSDK restAPI;

    public static CCPRestSmsSDK getRestAPI(){
        return restAPI;
    }

    PRestSmsSDKUntil(){
        //初始化SDK
        restAPI = new CCPRestSmsSDK();
        //******************************注释*********************************************
        //*初始化服务器地址和端口                                                       *
        //*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        //*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
        //*******************************************************************************
        restAPI.init("sandboxapp.cloopen.com", "8883");
        restAPI.setAccount("aaf98f894e7826aa014e852a878f08a6", "86d1a1277811460bb4c4e7ec7520e490");
        //******************************注释*********************************************
        //*初始化应用ID                                                                 *
        //*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
        //*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        //*******************************************************************************
        restAPI.setAppId("8a48b5514e7c2193014e852b72d405c8");
    }

}
