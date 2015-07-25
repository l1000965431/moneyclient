package com.money.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liumin on 15/7/25.
 */

@Controller
@RequestMapping("/User")
public class AdministrateController extends ControllerBase implements IController {

    @RequestMapping("Test")
    @ResponseBody
    public int Test(){
        return 1;
    }
    

}
