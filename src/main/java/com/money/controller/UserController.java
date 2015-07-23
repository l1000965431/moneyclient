package com.money.controller;

import com.money.Service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by fisher on 2015/7/13.
 */

@Controller
@RequestMapping("/User")
public class UserController extends ControllerBase implements IController
{
    @Autowired
    UserService userService;

    @RequestMapping("passWordLogin")
    @ResponseBody
    public String Login( HttpServletRequest request,
                              HttpServletResponse response )
    {
        String UserName = request.getParameter( "username" );
        String PassWord = request.getParameter( "PassWord" );
        return userService.userLand(UserName,PassWord);
    }

    @RequestMapping("tokenLogin")
    @ResponseBody
    public int tokenLogin( HttpServletRequest request,
                              HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        return userService.tokenLand("",token);
    }

    @RequestMapping("perfectInfo")
    @ResponseBody
    public int perfectInfo( HttpServletRequest request,
                                HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        String info = request.getParameter( "info" );
        String userType=request.getParameter("userType");
        return userService.perfectInfo(token, info, userType);
    }

    @RequestMapping("changeInfo")
    @ResponseBody
    public int changeInfo( HttpServletRequest request,
                            HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        String info = request.getParameter( "info" );
        String userType=request.getParameter("userType");
        return userService.changeInfo(token, info, userType);
    }

    @RequestMapping("quitLogin")
    @ResponseBody
    public boolean quitLogin( HttpServletRequest request,
                                HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        return userService.quitLand(token);
    }

    @RequestMapping("register")
    @ResponseBody
    public  boolean register(HttpServletRequest request,
                             HttpServletResponse response )
    {
        String userName = request.getParameter( "userName" );
        String code = request.getParameter( "code" );
        String password = request.getParameter( "password" );
        int userType = Integer.valueOf(request.getParameter( "userType" ));
        return  userService.userRegister( userName, code,password,userType );
    }

    @RequestMapping("submitTeleNum")
    @ResponseBody
    public  int submitTeleNum(HttpServletRequest request,
                             HttpServletResponse response )
    {
        String userName = request.getParameter( "userName" );
        return  userService.submitTeleNum(userName,"");
    }

    @RequestMapping("sendPasswordCode")
    @ResponseBody
    public int sendPasswordCode(HttpServletRequest request,
                                  HttpServletResponse response )
    {
        String userName = request.getParameter( "userName" );
        String password = request.getParameter( "password" );
        return  userService.sendPasswordCode(userName, password,"");
    }

    @RequestMapping("changPassword")
    @ResponseBody
    public  boolean sendPasswochangPasswordrdCode(HttpServletRequest request,
                                     HttpServletResponse response )
    {
        String userName = request.getParameter( "userName" );
        String code = request.getParameter( "code" );
        String newPassword = request.getParameter( "newPassword" );
        return  userService.changPassword(userName,code,newPassword);
    }
}
