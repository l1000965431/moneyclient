package until.Adapter;

import com.google.gson.*;
import com.money.model.ActivityDetailModel;
import com.money.model.SREarningModel;

import java.lang.reflect.Type;

/**
 * Created by happysky on 15-8-3.
 */
public class InvestInfoAdapter implements JsonSerializer<ActivityDetailModel> {
    public JsonElement serialize(ActivityDetailModel detailModel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = new JsonObject();
        o.addProperty("brInvestPrice", detailModel.getDynamicModel().getActivityTotalLinesPeoples());

        JsonArray jsonArray = new JsonArray();
        for(SREarningModel srEarningModel : detailModel.getSrEarningModels()){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("srEarningPrice", srEarningModel.getEarningPrice());
            jsonObject.addProperty("srEarningNum", srEarningModel.getNum());
            jsonArray.add(jsonObject);
        }
        o.add("srEarningInfo", jsonArray);
        return o;
    }
}
