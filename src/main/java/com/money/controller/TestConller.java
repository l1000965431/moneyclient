package com.money.controller;

import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.memcach.MemCachService;
import com.money.model.SREarningModel;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.PingPlus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liumin on 15/7/6.
 */

@Controller
@RequestMapping("/Test")
public class TestConller extends ControllerBase implements IController {

/*    @Autowired
    private HttpClient client;*/

    @RequestMapping("/TestHead")
    @ResponseBody
    String TestHead(HttpServletRequest request,
                    HttpServletResponse response) throws Exception {

        PurchaseInAdvance purchaseInAdvance = ServiceFactory.getService("PurchaseInAdvance");
        //activityService.ActivityCompleteStart("4");
        //activityService.InstallmentActivityIDStart( "4",1 );
        purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance("4", "4_1");
        return "1";
    }

    @RequestMapping("/TestActivityStart")
    @ResponseBody
    String TestActivityStart(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        activityService.ActivityCompleteStart("4");
        return "1";
    }

    @RequestMapping("/TestDB")
    @ResponseBody
    String TestDB(HttpServletRequest request,
                  HttpServletResponse response) throws Exception {
        UserService userService = ServiceFactory.getService("UserService");
        return userService.getUserInfo("18511583205").toString();
    }

    @RequestMapping("/TestRedis")
    @ResponseBody
    String TestRedis(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {

        //MemCachService.MemCachgGet("foo");
        return "1";
    }

    @RequestMapping("/TestPing")
    @ResponseBody
    String TestPing(HttpServletRequest request,
                    HttpServletResponse response) throws Exception {

        PingPlus.Test();
        //MemCachService.MemCachgGet("foo");
        return "1";
    }

    @RequestMapping("/Testwebhooks")
    @ResponseBody
    String Testwebhooks(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        //PingPlus.Test();
        //MemCachService.MemCachgGet("foo");
        return MemCachService.MemCachgGet("foo");
    }

    @RequestMapping("/TestcallCharg")
    @ResponseBody
    String TestcallCharg(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        String a = request.getParameter("a");
        Pattern p = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(a);
        return GsonUntil.JavaClassToJson(m.find());
    }

    @RequestMapping("/TestcallDBCach")
    @ResponseBody
    String TestcallDBCach(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        List<SREarningModel> LinePeoplesSREarningList = new ArrayList<SREarningModel>();
        SREarningModel srEarningModel1 = new SREarningModel();
        srEarningModel1.setEarningType(1);
        srEarningModel1.setEarningPrice(0);
        srEarningModel1.setNum(1);
        LinePeoplesSREarningList.add(srEarningModel1);

        srEarningModel1 = new SREarningModel();
        srEarningModel1.setEarningType(1);
        srEarningModel1.setEarningPrice(54);
        srEarningModel1.setNum(1);
        LinePeoplesSREarningList.add(srEarningModel1);


        String json = GsonUntil.JavaClassToJson(LinePeoplesSREarningList);

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        activityService.Test("18", json);

        return json;
    }

    @RequestMapping("/TestWX")
    @ResponseBody
    String TestWX(HttpServletRequest request,
                  HttpServletResponse response) throws IOException {

/*        String result = null;
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

        url = url.replace("APPID", Config.WXAPPID).replace("APPSECRET", Config.WXAPPSECRET);

        HttpGet post = new HttpGet(url);
        CloseableHttpResponse response2 = null;
        try {
            response2 = (CloseableHttpResponse) client.execute(post);
            HttpEntity entity = response2.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }finally {
            response2.close();
        }*/

        return null;
    }


    @RequestMapping("/Testalipay")
    @ResponseBody
    String Testalipay(HttpServletRequest request,
                  HttpServletResponse response) throws IOException {
        String a = "0315006^xinjie_xj@163.com^星辰公司^20.00^F^TXN _RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^20081024" +
                "8427065^20081024143651|" +
                "0315006^xinjie_xj@163.com^星辰公司^20.00^F^TXN _RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^200810248427065^20081024143651";

        List<List<String>> c = new ArrayList<List<String>>();
        String[] b = a.split( "\\|" );
        for( int i = 0; i < b.length; i++ ){
            List<String> d = Arrays.asList(b[i].split( "\\^" ));
            c.add( d );
        }

        String json = GsonUntil.JavaClassToJson( c );

        return json;
    }

}

