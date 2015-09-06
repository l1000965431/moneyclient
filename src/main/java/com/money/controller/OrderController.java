package com.money.controller;

import com.google.gson.Gson;
import com.money.Service.order.OrderService;
import com.money.Service.user.UserService;
import com.money.config.ServerReturnValue;
import com.money.model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.Adapter.OrderGsonAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by liumin on 15/7/29.
 */

@Controller
@RequestMapping("/ActivityOrder")
public class OrderController extends ControllerBase {

    @Autowired
    OrderService orderService;

    @RequestMapping("/getOrderByUserID")
    @ResponseBody
    public String getOrderByUserID( HttpServletRequest request, HttpServletResponse response ){

        String UserID = request.getParameter( "userID" );
        String Token = request.getParameter( "token" );
        int firstPage = Integer.valueOf( request.getParameter( "firstPage" ) );

        if( UserID == null || UserID.length() == 0 ){
            response.setHeader( "response","" );
            return "";
        }

        int tokenLand = userService.tokenLand( UserID,Token );
        if( tokenLand != 1 && tokenLand != 2 ){
            response.setHeader( "response", ServerReturnValue.LANDFAILED );
            return "";
        }

        List<OrderModel> list = orderService.getOrderByUserID( UserID,firstPage,10 );
        response.setHeader( "response", ServerReturnValue.LANDSUCCESS );

        try{
            Gson gson = GsonUntil.getNewGsonByAdapter(OrderModel.class, new OrderGsonAdapter());
            String Json = gson.toJson( list );
            return Json;

        } catch ( Exception e ){
            return null;
        }

    }


}
