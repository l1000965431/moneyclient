package com.money.Service.alipay;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.alipay.config.AlipayConfig;
import com.money.Service.alipay.util.AlipayCore;
import com.money.Service.alipay.util.AlipaySubmit;
import com.money.Service.alipay.util.UtilDate;
import com.money.Service.alipay.util.httpClient.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by happysky on 15-9-23.
 *
 */
@Service("AlipayService")
public class AlipayService extends ServiceBase implements ServiceInterface {

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do";

    @Autowired
    private HttpClient client;

    public String requestTransaction(List<TransactionData> dataList){
//        String param = buildParam(dataList);
        Map<String, String> paramsMap = buildParam(dataList);

//        HttpPost post = null;
//        try {
//            List<NameValuePair> pairs = generateNameValuePair(paramsMap, "UTF-8");
//
//            post = new HttpPost(ALIPAY_GATEWAY_NEW);
//
//            post.setEntity(new UrlEncodedFormEntity(pairs));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        org.apache.http.HttpResponse response;
//        try {
//            response = client.execute(post);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "FAILURE";
//        } finally {
//            if (post != null) {
//                post.releaseConnection();
//            }
//        }

        return buildForm(paramsMap);
    }


    public Map<String, String> buildParam(List<TransactionData> dataList){
        HashMap<String, String> map = new HashMap<String, String>();

        // j接口名称
        map.put("service", "batch_trans_notify");

        // 合作身份者
        map.put("partner", AlipayConfig.partner);

        // 参数编码字符集
        map.put("_input_charset", AlipayConfig.input_charset);

        // 服务器异步通知页面
        map.put("notify_url", "http://172.31.2.52/Wallet/TransactionResult");

        // 付款账号名
        map.put("account_name", AlipayConfig.account_name);

        // 付款详细数据
        map.put("detail_data", buildDetailData(dataList));

        // 批量付款批次号
        map.put("batch_no", UtilDate.getOrderNum());

        // 付款总笔数
        map.put("batch_num", String.valueOf(dataList.size()));

        // 付款总金额
        DecimalFormat df   =   new   DecimalFormat("#####0.00");
        String sumPriceString = df.format(calcSumPrice(dataList));
        map.put("batch_fee", sumPriceString);

        // 付款账号
        map.put("email", AlipayConfig.account_email);

        // 支付日期
        map.put("pay_date", UtilDate.getDate());

        // 付款账号别名
        map.put("buyer_account_name", AlipayConfig.account_email);

        // 业务扩展参数
        map.put("extend_ param", "");

        return map;
    }

    private String buildForm(Map<String, String> map){
        return AlipaySubmit.buildRequest(map, "post", "haha");
    }

    private String buildPostString(Map<String, String> map){
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(map);
        //生成签名结果
        String mysign = AlipaySubmit.buildRequestMysign(sPara);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", AlipayConfig.sign_type);

        return AlipayCore.createLinkString(sPara);
    }

    public void foo2(Map<String, String> map){
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
    }

    public String buildDetailData(List<TransactionData> dataList){
        String dataString = "";
        for(int i = 0; i < dataList.size() - 1; i++){
            dataString += dataList.get(i).toAlipayTransFormat() + "|";
        }
        dataString += dataList.get(dataList.size() - 1).toAlipayTransFormat();
        return dataString;
    }

    public double calcSumPrice(List<TransactionData> dataList){
        double value = 0.0f;
        for(TransactionData data : dataList){
            value += data.getPrice();
        }
        return value;
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     * @param map MAP类型数组
     * @return NameValuePair类型数组
     */
    private static List<NameValuePair> generateNameValuePair(Map<String, String> map,String charset) throws Exception {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nameValuePair.add(new BasicNameValuePair(entry.getKey(), URLEncoder.encode(entry.getValue(), charset)));
        }
        return nameValuePair;
    }
}
