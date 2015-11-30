package until.UmengPush;

import until.MoneyServerDate;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 友盟推送的参数
 * <p>User: liumin
 * <p>Date: 15-7-7 下午2:24
 * <p>Version: 1.0
 */


public class UmengSendParameter {

    String appkey;

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    String timestamp;

    String type;

    String alias_type;

    String alias;

    Map<String,Object> payload;

    boolean production_mode;

    String description;

    public UmengSendParameter( String Alias,String ticker,String title,String text,String description  ){
        this.appkey = "55c5affbe0f55a4474006226";
        this.timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));

        if( Alias == null || Alias.length() == 0 ){
            this.type = "broadcast";
        }else{
            this.type = "customizedcast";
            this.alias_type = "SINA_WEIBO";
            this.alias = Alias;
        }

        this.payload = new HashMap<String, Object>();
        this.payload.put( "display_type","notification" );

        Map<String,Object> body = new HashMap<String, Object>();
        body.put( "ticker",ticker );
        body.put( "title",title );
        body.put( "text",text );
        body.put( "after_open","go_app" );
        this.payload.put( "body",body );
        this.description = description;
        this.production_mode = true;
    }

    public UmengSendParameter( UMengMessage uMengMessage  ){

        String UserId = uMengMessage.getUserId();

        this.appkey = "55c5affbe0f55a4474006226";
        this.timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
        this.payload = new HashMap();
        this.payload.put( "display_type","message" );
        Map<String,Object> body = new HashMap();
        body.put( "custom",uMengMessage.toMessageBodyString() );
        this.payload.put( "body",body );
        this.description = uMengMessage.getDescription();
        this.production_mode = true;
        if( UserId == null || UserId.length() == 0 ){
            this.type = "broadcast";
        }else{
            this.type = "customizedcast";
            this.alias_type = "SINA_WEIBO";
            this.alias = uMengMessage.getUserId();
        }
    }


    public String getAppkey() {
        return appkey;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getAlias_type() {
        return alias_type;
    }

    public String getAlias() {
        return alias;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public boolean isProduction_mode() {
        return production_mode;
    }

    public String getDescription() {
        return description;
    }


}
