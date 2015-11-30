package until;

import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import until.httpClient.MoneyHttpProtocolHandler;
import until.httpClient.HttpResultType;
import until.httpClient.MoneyHttpRequest;
import until.httpClient.MoneyHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liumin on 15/8/26.
 */

@Component
public class WxBinding {

    @Qualifier("moneyHttpProtocolHandler")
    @Autowired
    private MoneyHttpProtocolHandler client;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WxBinding.class);

    public WxOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) throws IOException, HttpException {

        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";

        MoneyHttpRequest moneyHttpRequest = new MoneyHttpRequest(HttpResultType.STRING );
        moneyHttpRequest.setUrl(requestUrl);
        List<NameValuePair> parmer = new ArrayList();
        parmer.add( new BasicNameValuePair( "APPID",appId ));
        parmer.add( new BasicNameValuePair( "SECRET",appSecret ));
        parmer.add( new BasicNameValuePair( "CODE",code ));
        parmer.add( new BasicNameValuePair( "grant_type","authorization_code" ));
        moneyHttpRequest.setParameters( parmer );
        MoneyHttpResponse moneyHttpResponse = client.execute(moneyHttpRequest, "", "");
        String result = moneyHttpResponse.getStringResult();

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


