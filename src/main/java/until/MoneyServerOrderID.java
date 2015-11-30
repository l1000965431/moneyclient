package until;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liumin on 15/9/29.
 */
public class MoneyServerOrderID {

    public static String GetOrderID( String userId ){
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + userId;
        return orderNo;
    }


}
