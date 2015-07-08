package until;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liumin on 15/7/7.
 */
public class MoneyServerDate {

     public static String getCurData(){
        SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
        sdf.applyPattern("yyyy年MM月dd日_HH时mm分ss秒");
        String timeStr = sdf.format(new Date());

        return timeStr;
    }



}
