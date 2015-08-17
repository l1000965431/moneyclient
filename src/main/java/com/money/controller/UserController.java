package com.money.controller;

import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
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

    @RequestMapping("/passWordLogin")
    @ResponseBody
    public String Login( HttpServletRequest request,
                              HttpServletResponse response )
    {
        String UserName = request.getParameter( "userId" );
        String PassWord = request.getParameter( "password" );

        String LoginResult = userService.userLand(UserName,PassWord);

        if( LoginResult.length() >= 8 ){
            response.setHeader( "LoginResult", ServerReturnValue.LANDSUCCESS );

            response.setHeader( "UserResponse",userService.getUserInfo(UserName) );
        }else{
            response.setHeader( "LoginResult",LoginResult );
            response.setHeader( "UserResponse","" );
            return null;
        }

        return LoginResult;
    }

    @RequestMapping("/tokenLogin")
    @ResponseBody
    public int tokenLogin( HttpServletRequest request,
                              HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        String userId = request.getParameter( "userId" );
        return 1;
        //return userService.tokenLand(userId,token);
    }

    @RequestMapping("/perfectInfo")
    @ResponseBody
    public int perfectInfo( HttpServletRequest request,
                                HttpServletResponse response )
    {
        String userID = request.getParameter( "userID" );
        String token = request.getParameter( "token" );
        String info = request.getParameter( "info" );
        return userService.perfectInfo(userID,token, info);
    }

    @RequestMapping("/changeInfo")
    @ResponseBody
    public int changeInfo( HttpServletRequest request,
                            HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        String info = request.getParameter( "info" );
        String userType=request.getParameter("userType");
        return userService.changeInfo(token, info, userType);
    }

    @RequestMapping("/quitLogin")
    @ResponseBody
    public boolean quitLogin( HttpServletRequest request,
                                HttpServletResponse response )
    {
        String userID = request.getParameter( "userId" );
        return userService.quitLand(userID);
    }

    @RequestMapping("/register")
    @ResponseBody
    public  int register(HttpServletRequest request,
                             HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        //String code = request.getParameter( "code" );
        String password = request.getParameter( "password" );
        int userType = Integer.valueOf(request.getParameter("userType"));
        return  userService.userRegister( userName, "",password,userType );
    }

    @RequestMapping("/submitTeleNum")
    @ResponseBody
    public  int submitTeleNum(HttpServletRequest request,
                             HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        return  userService.submitTeleNum(userName,"");
    }

    @RequestMapping("/sendPasswordCode")
    @ResponseBody
    public int sendPasswordCode(HttpServletRequest request,
                                  HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        String password = request.getParameter( "password" );
        return  userService.sendPasswordCode(userName, password,"");
    }

    @RequestMapping("/changPassword")
    @ResponseBody
    public  int sendPasswochangPasswordrdCode(HttpServletRequest request,
                                     HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        //String code = request.getParameter( "code" );
        String newPassword = request.getParameter( "newPassword" );
        String oldPassword = request.getParameter( "oldPassword" );
        return  userService.changPassword(userName,"",newPassword,oldPassword);
    }

    @RequestMapping("/SendUserCode")
    @ResponseBody
    public int SendUserCode( HttpServletRequest request,
                              HttpServletResponse response ){
        String userID = request.getParameter( "userId" );
        return userService.SendCode( userID );
    }

    @RequestMapping("/ChangeUserHeadPortrait")
    @ResponseBody
    public int ChangeUserHeadPortrait( HttpServletRequest request,
                                   HttpServletResponse response ){
        String userID = request.getParameter( "userId" );
        String Url = request.getParameter( "Url" );

        if( userID == null || userID.length() == 0 ){
            return ServerReturnValue.SERVERRETURNERROR;
        }

        if( Url == null || Url.length() == 0 ){
            return ServerReturnValue.SERVERRETURNERROR;
        }

        return userService.ChangeUserHeadPortrait( userID,Url );
    }
}
