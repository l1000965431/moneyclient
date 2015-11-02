package com.money.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.activityPreferential.ActivityPreferentialService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.job.TestJob;
import com.money.memcach.MemCachService;
import com.money.model.PreferentiaLotteryModel;
import com.money.model.SREarningModel;
import com.money.model.UserModel;
import org.quartz.DateBuilder;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
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
    String TestHead(HttpServletRequest request) throws Exception {

        PurchaseInAdvance purchaseInAdvance = ServiceFactory.getService("PurchaseInAdvance");
        //activityService.ActivityCompleteStart("4");
        //activityService.InstallmentActivityIDStart( "4",1 );
        purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance("4", "4_1");
        return "1";
    }

    @RequestMapping("/TestActivityStart")
    @ResponseBody
    String TestActivityStart(HttpServletRequest request) throws Exception {

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        activityService.ActivityCompleteStart("4");
        return "1";
    }

    @RequestMapping("/TestDB")
    @ResponseBody
    String TestDB(HttpServletRequest request) throws Exception {
        UserService userService = ServiceFactory.getService("UserService");
        return userService.getUserInfo("18511583205").toString();
    }

    @RequestMapping("/TestRedis")
    @ResponseBody
    String TestRedis(HttpServletRequest request) throws Exception {

        String userId = request.getParameter("userId");

        return MemCachService.MemCachgGet(userId);
    }

    @RequestMapping("/TestPing")
    @ResponseBody
    String TestPing(HttpServletRequest request) throws Exception {

        PingPlus.Test();
        //MemCachService.MemCachgGet("foo");
        return "1";
    }

    @RequestMapping("/Testwebhooks")
    @ResponseBody
    String Testwebhooks(HttpServletRequest request) throws Exception {

        //PingPlus.Test();
        //MemCachService.MemCachgGet("foo");
        return MemCachService.MemCachgGet("foo");
    }

    @RequestMapping("/TestcallCharg")
    @ResponseBody
    String TestcallCharg(HttpServletRequest request) throws Exception {
        String a = request.getParameter("a");
        Pattern p = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(a);
        return GsonUntil.JavaClassToJson(m.find());
    }

    @RequestMapping("/TestcallDBCach")
    @ResponseBody
    String TestcallDBCach(HttpServletRequest request) throws Exception {
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
    String TestWX(HttpServletRequest request) throws IOException {

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
    String Testalipay(HttpServletRequest request) throws IOException {
        String a = "0315006^xinjie_xj@163.com^星辰公司^20.00^F^TXN _RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^20081024" +
                "8427065^20081024143651|" +
                "0315006^xinjie_xj@163.com^星辰公司^20.00^F^TXN _RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^200810248427065^20081024143651";

        List<List<String>> c = new ArrayList<List<String>>();
        String[] b = a.split("\\|");
        for (int i = 0; i < b.length; i++) {
            List<String> d = Arrays.asList(b[i].split("\\^"));
            c.add(d);
        }

        String json = GsonUntil.JavaClassToJson(c);

        return json;
    }

    @RequestMapping("/TestDownLoad")
    @ResponseBody
    void TestDownLoad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String a = request.getSession().getId();

        response.sendRedirect("http://p.gdown.baidu.com/58ebaf2831bb441e9ee3605aa1a809f7b36dee8a7618473419a33a0354cafd0a2a10c55cbf934846e0ec064e283e33be852c01ee7d062f92d6ff5aee1e8081b1514725476657f847544a3dcd927f5535141cf0cf4368df612cd2816ac355708eb471ee58b44e3273333aa69a8d7ab90e05df928e9af10fea108c3adda329dc23315a713107726a14561e14ea0fee26c9d5e17eb1cc279a8051e2142f2121b6689303a8383beba488");
    }

    @RequestMapping("/TestBeanToMap")
    @ResponseBody
    String TestBean22Map() {
        UserService userService = ServiceFactory.getService("UserService");
        UserModel userModel = userService.getUserInfo("18511583205");
        Map map = BeanTransfersUntil.TransBean2Map(userModel);
        return GsonUntil.JavaClassToJson(map);
    }

    @RequestMapping("/TestQrtzJob")
    @ResponseBody
    void TestQrtzJob() {
        ScheduleJob job = new ScheduleJob();
        job.setJobGroup("TestQrtzJob");
        job.setCronExpression("2 * * * * ?");
        job.setJobId("1");
        job.setJobName("TestQrtzJob1");
        job.setJobStatus("1");

        try {
            QuartzUntil.getQuartzUntil().AddTick(job, TestJob.class, DateBuilder.nextGivenSecondDate(null, 0));
        } catch (SchedulerException e) {
            return;
        }
    }

    @RequestMapping("/TestActivityPreferential")
    @ResponseBody
    void TestActivityPreferential(HttpServletRequest request) throws ParseException {

        String ActivityID = request.getParameter("activityId");
        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");

        if (activityPreferentialService == null) {
            return;
        }

        String a = "[\n" +
                "    {\n" +
                "        \"earningPrice\": 6,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 4,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 3,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 2,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 1,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    }\n" +
                "]\n";

        activityPreferentialService.InsertActivityPreferential(100, MoneyServerDate.getDateCurDate(), ActivityID, a, 50, 5);
    }

    @RequestMapping("/TestActivityPreferentialStart")
    @ResponseBody
    String TestActivityPreferentialStart(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + ActivityID;
        MemCachService.RemoveValue(ActivityInfoKey);


        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");

        activityPreferentialService.StartActivityPreferential(Integer.valueOf(ActivityID));


        List<byte[]> list = MemCachService.getRedisList(ActivityInfoKey.getBytes());

        List<PreferentiaLotteryModel> re = new ArrayList<>();

        for (byte[] a : list) {
            re.add((PreferentiaLotteryModel) GsonUntil.jsonToJavaClass(new String(a), new TypeToken<PreferentiaLotteryModel>() {
            }.getType()));
        }

        return GsonUntil.JavaClassToJson(re);
    }

    @RequestMapping("/TestActivityPreferentialInfo")
    @ResponseBody
    String TestActivityPreferentialInfo(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIINFO + ActivityID;

        return new String(MemCachService.MemCachgGet(ActivityInfoKey.getBytes()));
    }

    @RequestMapping("/TestActivityPreferentialBilled")
    @ResponseBody
    String TestActivityPreferentialBilled(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIBLLLED + ActivityID;

        List<byte[]> list = MemCachService.getRedisList(ActivityInfoKey.getBytes());

        List<PreferentiaLotteryModel> re = new ArrayList<>();

        for (byte[] a : list) {
            re.add((PreferentiaLotteryModel) BeanTransfersUntil.bytesToObject(a));
        }

        return GsonUntil.JavaClassToJson(re);
    }

    @RequestMapping("/TestActivityPreferentialUnBilled")
    @ResponseBody
    String TestActivityPreferentialUnBilled(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + ActivityID;

        List<byte[]> list = MemCachService.getRedisList(ActivityInfoKey.getBytes());

        List<PreferentiaLotteryModel> re = new ArrayList<>();

        for (byte[] a : list) {
            re.add((PreferentiaLotteryModel) BeanTransfersUntil.bytesToObject(a));
        }

        return GsonUntil.JavaClassToJson(re);
    }

    @RequestMapping("/TestActivityPreferentialJoin")
    @ResponseBody
    int TestActivityPreferentialJoin(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String UserId = request.getParameter("userId");
        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");

        return activityPreferentialService.JoinActivityPreferential(Integer.valueOf(ActivityID), UserId, 0);
    }

    @RequestMapping("/TestGlobalConfig")
    @ResponseBody
    String TestGlobalConfig() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put( "AddExpInvite",Config.AddExpInvite );
        map.put( "AddVirtualSecuritiesInvite",Config.AddVirtualSecuritiesInvite );
        map.put( "AddExpPurchase",Config.AddExpPurchase );
        map.put( "AddVirtualSecuritiesSelf",Config.AddVirtualSecuritiesSelf );
        map.put( "MaxVirtualSecurities",Config.MaxVirtualSecurities );
        map.put( "MaxVirtualSecuritiesBuy",Config.MaxVirtualSecuritiesBuy );

        return GsonUntil.JavaClassToJson( map );
    }

}

