package until;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * ping++ 支付服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Component
public class PingPlus {
    public static String CreateChargeParams(String UserID,int amount, String channel, String client_ip, String subject, String body,String order_no ) {
        Pingpp.apiKey = Config.PINGPLUSTESTID;
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("order_no", order_no);
        chargeParams.put("amount", amount);
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", Config.PINGPLUSLAPPID);
        chargeParams.put("app", app);
        chargeParams.put("channel", channel);
        chargeParams.put("currency", "cny");
        chargeParams.put("client_ip", "127.0.0.1");
        chargeParams.put("subject", subject);
        chargeParams.put("body", body);
        Map<String, String> initialMetadata = new HashMap<String, String>();
        initialMetadata.put("UserID", UserID);
        chargeParams.put("metadata", initialMetadata);
        Charge charge;

        try {
            charge = Charge.create(chargeParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidRequestException e) {
            e.printStackTrace();
            return null;
        } catch (APIConnectionException e) {
            e.printStackTrace();
            return null;
        } catch (APIException e) {
            e.printStackTrace();
            return null;
        }
        return charge.toString();
    }

    public static String CreateTransferMap( int amount,String opneId,String Orderno ) throws APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Pingpp.apiKey = Config.PINGPLUSTESTID;
        Map<String, Object> transferMap = new HashMap<String, Object>();
        transferMap.put("amount", amount*100);
        transferMap.put("currency", "cny");
        transferMap.put("type",  "b2c");
        transferMap.put("order_no",  Orderno);
        transferMap.put("channel",  "wx_pub");
        transferMap.put("recipient", opneId);
        transferMap.put("description", "提款");
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", Config.PINGPLUSLAPPID);
        transferMap.put("app", app);
        Transfer transfer = Transfer.create(transferMap);
        return transfer.toString();
    }



    public static void Webhooks(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        // 获得 http body 内容
        BufferedReader reader = request.getReader();
        StringBuffer buffer = new StringBuffer();
        String string;
        while ((string = reader.readLine()) != null) {
            buffer.append(string);
        }
        reader.close();
        // 解析异步通知数据
        Event event = Webhooks.eventParse(buffer.toString());
        if ("charge.succeeded".equals(event.getType())) {
            String body = event.toString();

            MoneyServerMQManager.SendMessage( new MoneyServerMessage( MoneyServerMQ_Topic.MONEYSERVERMQ_RECHARGEWALLET_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_RECHARGEWALLET_TAG,body,"1" ));

            response.setStatus(200);
        } else if ("transfer.succeeded".equals(event.getType())) {
            String body = event.toString();

            MoneyServerMQManager.SendMessage( new MoneyServerMessage( MoneyServerMQ_Topic.MONEYSERVERMQ_RECHARGEWALLET_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_RECHARGEWALLET_TAG,body,"1" ));

            response.setStatus(200);
        } else {
            response.setStatus(500);
        }

    }




    public static void Test() {
        Pingpp.apiKey = "sk_test_aD4qnHSWTCmPvX1un5rXfjPK";

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("order_no", "123456789");
        chargeParams.put("amount", 100);
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", "app_jvXfzPe9e90GeLWz");
        chargeParams.put("app", app);
        chargeParams.put("channel", "upacp");
        chargeParams.put("currency", "cny");
        chargeParams.put("client_ip", "127.0.0.1");
        chargeParams.put("subject", "LongyanTest");
        chargeParams.put("body", "MoneyServerTest");

        try {
            Charge.create(chargeParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    public static void Test1() {
        Pingpp.apiKey = "sk_test_aD4qnHSWTCmPvX1un5rXfjPK";

        Map chargeParams = new HashMap<String, Object>();
        chargeParams.put("limit", 3);
        ChargeCollection chargeCollection = null;
        try {
            chargeCollection = Charge.all(chargeParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

}
