package until.UmengPush;

import com.money.config.Config;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import until.GsonUntil;
import until.Md5Utils;
import until.httpClient.MoneyHttpProtocolHandler;
import until.httpClient.HttpResultType;
import until.httpClient.MoneyHttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liumin on 15/8/10.
 */

@Component
public class UMengPush {

    @Autowired
    private MoneyHttpProtocolHandler client;

    private static UMengPush uMengPush;

    UMengPush() {
        uMengPush = this;
    }

    static public void CustomizedcastSendMessage(UmengSendParameter umengSendParameter) throws IOException, HttpException {
        //Map<String, Object> urlVariables = new HashMap<String, Object>();
        umengSendParameter.setTimestamp(Integer.toString((int) (System.currentTimeMillis() / 1000)));
        String json = GsonUntil.JavaClassToJson(umengSendParameter);
        String Sign = Md5Utils.hash("POST" + "http://msg.umeng.com/api/send" + json + Config.UMENGAPPKEY);
        if (Sign == null) {
            return;
        }

/*        urlVariables.put("sign", Sign);
        HttpPost post = new HttpPost("http://msg.umeng.com/api/send?sign=" + Sign);
        StringEntity se;
        se = new StringEntity(json, "UTF-8");
        post.setEntity(se);*/

        MoneyHttpRequest moneyHttpRequest = new MoneyHttpRequest(HttpResultType.STRING);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("sign", Sign));
        moneyHttpRequest.setMethod(MoneyHttpRequest.METHOD_POST);
        moneyHttpRequest.setUrl("http://msg.umeng.com/api/send");
        moneyHttpRequest.setParameters(pairs);
        uMengPush.client.execute(moneyHttpRequest, "", "");
    }
}
