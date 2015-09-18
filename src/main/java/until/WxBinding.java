package until;

import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by liumin on 15/8/26.
 */

@Component
public class WxBinding {

    @Autowired
    private HttpClient client;

    public WxOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) throws IOException {

        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID", appId);
        requestUrl = requestUrl.replace("SECRET", appSecret);
        requestUrl = requestUrl.replace("CODE", code);

        HttpPost post = new HttpPost(requestUrl);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        InputStream in = null;
        String result = null;
        try {
            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
                in = entity.getContent();
                byte[] read = new byte[1024];
                byte[] all = new byte[0];
                int num;
                while ((num = in.read(read)) > 0) {
                    byte[] temp = new byte[all.length + num];
                    System.arraycopy(all, 0, temp, 0, all.length);
                    System.arraycopy(read, 0, temp, all.length, num);
                    all = temp;
                }
                result = new String(all, "UTF-8");
            }
        } finally {
            if (in != null) in.close();
            post.releaseConnection();
        }

        Map<String,Object> map = GsonUntil.jsonToJavaClass( result,new TypeToken<Map<String,Object>>(){}.getType());
        if( map == null ){
            return null;
        }

        WxOauth2Token wxOauth2Token = new WxOauth2Token();
        wxOauth2Token.setOpenId( map.get( "openid" ).toString() );
        wxOauth2Token.setToken( map.get( "access_token" ).toString() );
        return wxOauth2Token;
    }
}


