package until.Adapter;

import com.google.gson.*;
import com.money.model.OrderModel;

import java.lang.reflect.Type;

/**
 * Created by liumin on 15/7/29.
 */
public class OrderGsonAdapter implements JsonSerializer<OrderModel> {

    public JsonElement serialize(OrderModel orderModel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o=new JsonObject();

        o.addProperty("PurchaseNum", orderModel.getPurchaseNum());
        o.addProperty("AdvanceNum", orderModel.getAdvanceNum());
        o.addProperty("orderLines", orderModel.getOrderLines());
        o.addProperty("ActivityName", orderModel.getActivityDetailModel().getActivityVerifyCompleteModel().getName());
        o.addProperty("ActivityStatus", orderModel.getActivityDetailModel().getStatus());

        o.addProperty("TotalLines", orderModel.getActivityDetailModel().getDynamicModel().getActivityTotalLines());
        o.addProperty("CurLines", orderModel.getActivityDetailModel().getDynamicModel().getActivityCurLines());
        o.addProperty("TotalLinesPeoples", orderModel.getActivityDetailModel().getDynamicModel().getActivityTotalLinesPeoples());
        o.addProperty("CurLinesPeoples", orderModel.getActivityDetailModel().getDynamicModel().getActivityCurLinesPeoples());
        o.addProperty("CurStageIndex", orderModel.getActivityDetailModel().getStageIndex());
        o.addProperty("TotalStageIndex", orderModel.getActivityDetailModel().getActivityVerifyCompleteModel().getTotalInstallmentNum());
        return o;
    }
}
