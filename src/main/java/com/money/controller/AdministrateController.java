package com.money.controller;

import com.money.Service.AdminService.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liumin on 15/7/25.
 */

@Controller
@RequestMapping("/Administrate")
public class AdministrateController extends ControllerBase implements IController {

    @Autowired
    AdminService adminService;

    @RequestMapping("Login")
    @ResponseBody
    public int Test(){
        return 1;
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request){
        String userId = request.getParameter("userId");
        String userPassword = request.getParameter("userPassword");

        return "";
    }


}
