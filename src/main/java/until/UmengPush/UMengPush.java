package until.UmengPush;
import com.money.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import until.GsonUntil;
import until.Md5Utils;
import ytx.org.apache.http.HttpResponse;
import ytx.org.apache.http.client.methods.HttpPost;
import ytx.org.apache.http.entity.StringEntity;
import ytx.org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumin on 15/8/10.
 */

@Component
public class UMengPush {

    @Autowired
    private DefaultHttpClient client;

    private static UMengPush uMengPush;

    UMengPush(){
        uMengPush = this;
    }

    /**
     * 友盟自定义发送消息
     * @param UserID  用户ID
     * @param ticker 通知栏提示文字
     * @param title  通知标题
     * @param text   通知文字描述
     * @param description 发送消息描述
     */
    static public void CustomizedcastSendMessage( String UserID,String ticker,String title,String text,String description ){
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,ticker,title,text,description );
        String json = GsonUntil.JavaClassToJson( umengSendParameter );
        String Sign = Md5Utils.hash("POST"+"http://msg.umeng.com/api/send"+json+Config.UMENGAPPKEY);
        if( Sign == null ){
            return;
        }
        urlVariables.put( "sign",Sign );
        HttpPost post = new HttpPost("http://msg.umeng.com/api/send?sign="+Sign);
        StringEntity se;
        try {
            se = new StringEntity(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        post.setEntity(se);
        //HttpResponse response = null;
        try {
             uMengPush.client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    static public void CustomizedcastSendMessage( UmengSendParameter umengSendParameter ){
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        String json = GsonUntil.JavaClassToJson( umengSendParameter );
        String Sign = Md5Utils.hash("POST"+"http://msg.umeng.com/api/send"+json+Config.UMENGAPPKEY);
        if( Sign == null ){
            return;
        }
        urlVariables.put( "sign",Sign );
        HttpPost post = new HttpPost("http://msg.umeng.com/api/send?sign="+Sign);
        StringEntity se;
        try {
            se = new StringEntity(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        post.setEntity(se);
        //HttpResponse response = null;
        try {
            uMengPush.client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }
}
