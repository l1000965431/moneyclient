package until.Adapter;

import com.google.gson.*;
import com.money.model.ActivityDetailModel;
import com.money.model.SREarningModel;

import java.lang.reflect.Type;

/**
 * Created by happysky on 15-8-3.
 */
public class InvestInfoAdapter implements JsonSerializer<SREarningModel> {
    public JsonElement serialize(SREarningModel srEarningModel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = new JsonObject();
        o.addProperty("srEarningPrice", srEarningModel.getEarningPrice());
        o.addProperty("srEarningNum", srEarningModel.getNum());
        o.addProperty("srEarningType", srEarningModel.getEarningType());
        return o;
    }
}
