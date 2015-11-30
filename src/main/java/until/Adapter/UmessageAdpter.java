package until.Adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.money.model.LotteryPeoples;
import until.GsonUntil;
import until.UmengPush.UMengMessage;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by liumin on 15/10/17.
 */
public class UmessageAdpter implements JsonSerializer<UMengMessage>,JsonDeserializer<UMengMessage> {
    @Override
    public JsonElement serialize(UMengMessage uMengMessage, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o=new JsonObject();
        o.addProperty("Description", uMengMessage.getDescription());
        o.addProperty("UserId", uMengMessage.getUserId());
        o.addProperty("MessageBody", uMengMessage.toMessageBodyString());
        return o;
    }

    public UMengMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if( jsonElement != null ){
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement jsonElement1 = jsonObject.get( "MessageBody" );
            Map<String,String> map =
                    GsonUntil.jsonToJavaClass(jsonElement1.getAsString(),
                            new TypeToken<Map<String, String>>() {}.getType());

            return new UMengMessage( jsonObject.get("UserId").getAsString(),map.get( "MessageType" ),
                    map.get( "MessageBody" ),jsonObject.get("Description").getAsString() );
        }else{
            return null;
        }
    }
}
