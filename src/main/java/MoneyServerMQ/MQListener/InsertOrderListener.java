package MoneyServerMQ.MQListener;

import MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import dao.BaseDao;
import model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.io.UnsupportedEncodingException;

/**
 * Created by liumin on 15/7/10.
 */
public class InsertOrderListener extends MoneyServerListener {
    @Autowired
    private BaseDao baseDao;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String bodystring = BodyToString( message.getBody() );

            OrderModel ordermoel = GsonUntil.jsonToJavaClass(bodystring, OrderModel.class);

            baseDao.save( ordermoel );

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }
}
