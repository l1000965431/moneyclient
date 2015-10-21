package until.UmengPush;

import until.GsonUntil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumin on 15/10/17.
 */

public class UMengMessage {

    public UMengMessage( String UserId,String MessageType,String MessageBody,String description ){
        this.UserId = UserId;
        mapmessagebody.put("MessageType", MessageType);
        mapmessagebody.put("MessageBody", MessageBody);
        this.description = description;
    }

    String UserId;

    String description;

    Map<String,String> mapmessagebody = new HashMap<>();

    @Override
    public String toString(){
        return GsonUntil.JavaClassToJson( this );
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String toMessageBodyString(){
        return GsonUntil.JavaClassToJson( mapmessagebody );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getMapmessagebody() {
        return mapmessagebody;
    }

    public void setMapmessagebody(Map<String, String> mapmessagebody) {
        this.mapmessagebody = mapmessagebody;
    }
}
