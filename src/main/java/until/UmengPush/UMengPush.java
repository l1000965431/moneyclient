package until.UmengPush;

import com.money.config.Config;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import until.GsonUntil;
import until.Md5Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumin on 15/8/10.
 */

@Component
public class UMengPush {

    @Autowired
    private HttpClient client;

    private static UMengPush uMengPush;

    UMengPush() {
        uMengPush = this;
    }

    static public void CustomizedcastSendMessage(UmengSendParameter umengSendParameter) {
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        umengSendParameter.setTimestamp( Integer.toString((int)(System.currentTimeMillis() / 1000)) );
        String json = GsonUntil.JavaClassToJson(umengSendParameter);
        String Sign = Md5Utils.hash("POST" + "http://msg.umeng.com/api/send" + json + Config.UMENGAPPKEY);
        if (Sign == null) {
            return;
        }

        urlVariables.put("sign", Sign);
        HttpPost post = new HttpPost("http://msg.umeng.com/api/send?sign=" + Sign);
        StringEntity se;
        se = new StringEntity(json, "UTF-8");
        post.setEntity(se);

        try {
            uMengPush.client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }
}
