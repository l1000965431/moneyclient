package until;

import com.google.gson.*;
import com.money.model.OrderModel;

import java.lang.reflect.Type;

/**
 * Created by liumin on 15/7/29.
 */
public class OrderGsonAdapter implements JsonSerializer<OrderModel> {

    public JsonElement serialize(OrderModel orderModel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o=new JsonObject();

        //o.addProperty("orderId", orderModel.getOrderId());
        o.addProperty("orderState",orderModel.getOrderState() );
        o.addProperty("PurchaseNum", orderModel.getPurchaseNum());
        o.addProperty("AdvanceNum", orderModel.getAdvanceNum());
        o.addProperty("orderLines", orderModel.getOrderLines());
        o.addProperty("ActivityName", orderModel.getActivityVerifyCompleteModel().getName());
        o.addProperty("ActivityStatus", orderModel.getActivityVerifyCompleteModel().getStatus());

        return o;
    }
}
