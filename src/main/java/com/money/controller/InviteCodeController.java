package com.money.controller;

import com.money.Service.InviteCodeService.InviteCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

/**
 * Created by liumin on 15/10/4.
 */

@Controller
@RequestMapping("/InviteCodeController")
public class InviteCodeController {

    @Autowired
    InviteCodeService inviteCodeService;

    @RequestMapping("/useInviteCode")
    @ResponseBody
    public int useInviteCode(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String userId = request.getParameter( "userId" );
        String inviteCode = request.getParameter( "inviteCode" );

        if( userId == null || inviteCode == null ){
            return 0;
        }

        return inviteCodeService.useInviteCode( userId,inviteCode );
    }

    @RequestMapping("/InsertInviteCode")
    @ResponseBody
    public int InsertInviteCode(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        int inviteCode = Integer.valueOf(request.getParameter("num"));
        return inviteCodeService.AddInviteCode(inviteCode);
    }

    @RequestMapping("/TestInviteCode")
    @ResponseBody
    public int TestInviteCode(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        return inviteCodeService.CountInviteCode();
    }

}
