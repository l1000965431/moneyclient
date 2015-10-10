package com.money.controller;

import com.money.config.Config;
import com.money.config.ServerReturnValue;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.WxBinding;
import until.WxOauth2Token;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fisher on 2015/7/13.
 */

@Controller
@RequestMapping("/User")
public class UserController extends ControllerBase implements IController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Qualifier("wxBinding")
    @Autowired
    WxBinding wxBinding;

    @RequestMapping("/passWordLogin")
    @ResponseBody
    public String Login(HttpServletRequest request,
                        HttpServletResponse response) {

        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return null;
        }


        String UserName = mapData.get("userId");
        String PassWord = mapData.get("password");

        String LoginResult = userService.userLand(UserName, PassWord);

        Map<String, Object> map = new HashMap();
        map.put("token", LoginResult);

        if (LoginResult.length() >= 8) {
            map.put("LoginResult", ServerReturnValue.LANDSUCCESS);
            map.put("UserResponse", userService.getUserInfo(UserName));
        } else {
            map.put("LoginResult", LoginResult);
            map.put("UserResponse", "");
        }

        return GsonUntil.JavaClassToJson(map);
    }

    @RequestMapping("/tokenLogin")
    @ResponseBody
    public int tokenLogin(HttpServletRequest request,
                          HttpServletResponse response) {
        String token = request.getParameter("token");
        String userId = request.getParameter("userId");
        return userService.tokenLand(userId, token);
    }

    @RequestMapping("/perfectInfo")
    @ResponseBody
    public int perfectInfo(HttpServletRequest request,
                           HttpServletResponse response) {
        String userID = request.getParameter("userID");
        String token = request.getParameter("token");
        String info = request.getParameter("info");
        return userService.perfectInfo(userID, token, info);
    }

    @RequestMapping("/changeInfo")
    @ResponseBody
    public int changeInfo(HttpServletRequest request,
                          HttpServletResponse response) {
        String token = request.getParameter("token");
        String info = request.getParameter("info");
        String userType = request.getParameter("userType");
        return userService.changeInfo(token, info, userType);
    }

    @RequestMapping("/quitLogin")
    @ResponseBody
    public boolean quitLogin(HttpServletRequest request,
                             HttpServletResponse response) {
        String userID = request.getParameter("userId");
        return userService.quitLand(userID);
    }

    @RequestMapping("/register")
    @ResponseBody
    public int register(HttpServletRequest request,
                        HttpServletResponse response) {

        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return ServerReturnValue.REQISTEREDUSERNAMEERROR;
        }


        String userName = mapData.get("userId");
        //String code = request.getParameter( "code" );
        String password = mapData.get("password");
        int userType = Integer.valueOf(mapData.get("userType"));
        String inviteCode = mapData.get("inviteCode");

        return userService.userRegister(userName, "", password, userType,inviteCode);
    }

    @RequestMapping("/submitTeleNum")
    @ResponseBody
    public int submitTeleNum(HttpServletRequest request,
                             HttpServletResponse response) {
        String userName = request.getParameter("userId");
        return userService.submitTeleNum(userName, "");
    }

    @RequestMapping("/sendPasswordCode")
    @ResponseBody
    public int sendPasswordCode(HttpServletRequest request,
                                HttpServletResponse response) {
        String userName = request.getParameter("userId");
        String password = request.getParameter("password");
        return userService.sendPasswordCode(userName, password, "");
    }

    @RequestMapping("/changPassword")
    @ResponseBody
    public int sendPasswochangPasswordrdCode(HttpServletRequest request,
                                             HttpServletResponse response) {

        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return 0;
        }

        String userName = mapData.get("userId");
        //String code = request.getParameter( "code" );
        String newPassword = mapData.get("newPassword");
        String oldPassword = mapData.get("oldPassword");
        return userService.changPassword(userName, "", newPassword, oldPassword);
    }

    @RequestMapping("/RetrievePassword")
    @ResponseBody
    public int RetrievePassword(HttpServletRequest request,
                                HttpServletResponse response) {

        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return 0;
        }

        String userName = mapData.get("userId");
        //String code = request.getParameter( "code" );
        String newPassword = mapData.get("newPassword");
        return userService.RetrievePassword(userName, newPassword);
    }

    @RequestMapping("/SendUserCode")
    @ResponseBody
    public int SendUserCode(HttpServletRequest request,
                            HttpServletResponse response) {
        String userID = request.getParameter("userId");
        return userService.SendCode(userID);
    }

    @RequestMapping("/ChangeUserHeadPortrait")
    @ResponseBody
    public int ChangeUserHeadPortrait(HttpServletRequest request,
                                      HttpServletResponse response) {
        String userID = request.getParameter("userId");
        String Url = request.getParameter("Url");

        if (userID == null || userID.length() == 0) {
            return ServerReturnValue.SERVERRETURNERROR;
        }

        if (Url == null || Url.length() == 0) {
            return ServerReturnValue.SERVERRETURNERROR;
        }

        return userService.ChangeUserHeadPortrait(userID, Url);
    }

    @RequestMapping("/ClearOpenId")
    @ResponseBody
    public int ClearOpenId(HttpServletRequest request,
                                      HttpServletResponse response) {
        String userID = request.getParameter("userId");
        if (userID == null || userID.length() == 0) {
            return ServerReturnValue.SERVERRETURNERROR;
        }

        userService.ClearBinding(userID);
        return ServerReturnValue.SERVERRETURNCOMPELETE;
    }


    /**
     * 获得微信openId
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getWxOpenId")
    @ResponseBody
    public void getWxOpenId(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code == null) {
            response.sendRedirect("../project/BindingResult.jsp?result=2");
            return;
        }

        WxOauth2Token wxOauth2Token;
        try {
            wxOauth2Token = wxBinding.getOauth2AccessToken(Config.WXAPPID, Config.WXAPPSECRET, code);
        } catch (IOException e) {
            response.sendRedirect("../project/BindingResult.jsp?result=2");
            return;
        } catch (HttpException e) {
            response.sendRedirect("../project/BindingResult.jsp?result=2");
            return;
        }

        if (wxOauth2Token == null) {
            response.sendRedirect("../project/BindingResult.jsp?result=2");
            return;
        }

        //request.setAttribute("openId", wxOauth2Token.getOpenId());
        response.sendRedirect("../project/WxBinding.jsp?openId=" + wxOauth2Token.getOpenId());
        //response.sendRedirect("../project/WxBinding.jsp?openId=" + "asdjkaznxcjkakls9840234");
        return;
    }

    @RequestMapping("/BindingWxOpenId")
    @ResponseBody
    public int BindingWxOpenId(HttpServletRequest request,
                               HttpServletResponse response) throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String openId = request.getParameter("openId");

        if (userId == null || password == null || openId == null) {
            return 0;
        }

        int result = userService.BindingUserId(openId, userId, password);
        if (result == 1) {
            response.sendRedirect("../project/BindingResult.jsp?result=1");
            return 1;
        } else if( result == 3 ) {
            response.sendRedirect("../project/BindingResult.jsp?result=3");
            return 3;
        }else{
            response.sendRedirect("../project/BindingResult.jsp?result=2");
            return 2;
        }
    }

}
