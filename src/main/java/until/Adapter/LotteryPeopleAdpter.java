package until.Adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.money.model.LotteryPeoples;
import until.GsonUntil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by liumin on 15/8/25.
 */
public class LotteryPeopleAdpter implements JsonDeserializer<LotteryPeoples> {

    public LotteryPeoples deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if( jsonElement != null ){
            Map<String,Object> map = GsonUntil.jsonToJavaClass( jsonElement.getAsString(),new TypeToken<Map<String,Object>>(){}.getType());

            LotteryPeoples lotteryPeoples = new LotteryPeoples();
            lotteryPeoples.setUserId( map.get( "UserId" ).toString() );
            lotteryPeoples.setTickID(map.get("TickID").toString());
            lotteryPeoples.setLotteryLines( Integer.valueOf( map.get("LotteryLines").toString()) );
            lotteryPeoples.setActivityID(map.get("ActivityID").toString() );
            lotteryPeoples.setPurchaseType(Integer.valueOf(map.get("PurchaseType").toString()));

            return lotteryPeoples;
        }else{
            return null;
        }
    }
}
