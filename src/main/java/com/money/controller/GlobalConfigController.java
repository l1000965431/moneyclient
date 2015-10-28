package com.money.controller;

import com.google.gson.reflect.TypeToken;
import com.money.Service.GlobalConifg.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by liumin on 15/10/20.
 */

@Controller
@RequestMapping("/GlobalConfig")
public class GlobalConfigController extends ControllerBase implements IController {

    @Autowired
    GlobalConfigService globalConfigService;

    @RequestMapping("/SetConfigVaule")
    @ResponseBody
    public void SetConfigVaule( HttpServletRequest request ){

        String josn = request.getParameter("vauleMap");

        Map<String,String> map = GsonUntil.jsonToJavaClass(josn, new TypeToken<Map<String, String>>() {
        }.getType());

        globalConfigService.SetConfigVaule( map );
    }

    @RequestMapping("/InitConfigVaule")
    public void InitConfigVaule(){
        Map<String,String> map = globalConfigService.GetConfigVaule();

        globalConfigService.SetConfigVaule( map );
    }

    @RequestMapping("/GetConfigVaule")
    @ResponseBody
    public String GetConfigVaule(){
        return GsonUntil.JavaClassToJson(globalConfigService.GetConfigVaule());
    }

}
