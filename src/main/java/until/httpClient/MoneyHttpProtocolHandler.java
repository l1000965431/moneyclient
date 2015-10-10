package until.httpClient;

import org.apache.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/* *
 *类名：HttpProtocolHandler
 *功能：HttpClient方式访问
 *详细：获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
@Component
public class MoneyHttpProtocolHandler {

    private static String              DEFAULT_CHARSET                     = "UTF-8";

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int                        defaultConnectionTimeout            = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int                        defaultSoTimeout                    = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int                        defaultIdleConnTimeout              = 60000;

    private int                        defaultMaxConnPerHost               = 30;

    private int                        defaultMaxTotalConn                 = 80;

    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒*/
    private static final long          defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private PoolingHttpClientConnectionManager      connectionManager;

    private RequestConfig requestConfig;

    private static MoneyHttpProtocolHandler moneyHttpProtocolHandler = new MoneyHttpProtocolHandler();

    /**
     * 工厂方法
     * 
     * @return
     */
    public static MoneyHttpProtocolHandler getInstance() {
        return moneyHttpProtocolHandler;
    }

    /**
     * 私有的构造方法
     */
    private MoneyHttpProtocolHandler() {
        // 创建一个线程安全的HTTP连接池
/*        SSLContext sslContext = SSLContexts.custom().useTLS().build();
        sslContext.init(null,
                new TrustManager[] { new X509TrustManager() {

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }}, null);*/

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                //.register("https", new SSLConnectionSocketFactory(sslContext))
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(defaultMaxTotalConn);
        connectionManager.setDefaultMaxPerRoute(defaultMaxConnPerHost);

        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(defaultConnectionTimeout)
                .setSocketTimeout(defaultSoTimeout).setConnectTimeout(10000).build();
    }

    /**
     * 执行Http请求
     * 
     * @param request 请求数据
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath 文件路径
     * @return 
     * @throws HttpException, IOException 
     */
    public MoneyHttpResponse execute(MoneyHttpRequest request, String strParaFileName, String strFilePath) throws HttpException, IOException {
        HttpClient httpclient = HttpClients.custom().setConnectionManager( connectionManager ).setDefaultRequestConfig(requestConfig).build();

        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;
        HttpRequestBase httpRequestBase = null;

        //get模式且不带上传文件
        if (request.getMethod().equals(MoneyHttpRequest.METHOD_GET)) {
            httpRequestBase = new HttpGet(request.getUrl());
            // parseNotifyConfig会保证使用GET方法时，request一定使用QueryString
            //httpRequestBase.setQueryString(request.getQueryString());
        } else if(strParaFileName.equals("") && strFilePath.equals("")) {
        	//post模式且不带上传文件
            httpRequestBase = new HttpPost(request.getUrl());
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(request.getParameters(), Consts.UTF_8);
            ((HttpPost)httpRequestBase).setEntity( entity );
        }
        else {
        	//post模式且带上传文件
/*            method = new PostMethod(request.getUrl());
            List<Part> parts = new ArrayList<Part>();
            for (int i = 0; i < request.getParameters().length; i++) {
            	parts.add(new StringPart(request.getParameters()[i].getName(), request.getParameters()[i].getValue(), charset));
            }
            //增加文件参数，strParaFileName是参数名，使用本地文件
            parts.add(new FilePart(strParaFileName, new FilePartSource(new File(strFilePath))));

            // 设置请求体
            ((PostMethod) method).setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[0]), new HttpMethodParams()));*/
        }

        if( httpRequestBase == null ){
            return null;
        }
        httpRequestBase.setHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);
        // 设置Http Header中的User-Agent属性
        httpRequestBase.setHeader("User-Agent", "Mozilla/4.0");
        MoneyHttpResponse moneyHttpResponse = new MoneyHttpResponse();
        HttpEntity entity = null;
        try {
            HttpResponse response = httpclient.execute(httpRequestBase);
            entity = response.getEntity();

            if (request.getResultType().equals(HttpResultType.STRING)) {
                moneyHttpResponse.setStringResult( EntityUtils.toString(entity, Consts.UTF_8));
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                moneyHttpResponse.setByteResult(EntityUtils.toString(entity, Consts.UTF_8).getBytes());
            }
            moneyHttpResponse.setResponseHeaders(httpRequestBase.getAllHeaders());
        } catch (UnknownHostException ex) {

            return null;
        } catch (IOException ex) {

            return null;
        } catch (Exception ex) {

            return null;
        } finally {
            if( entity != null ){
                InputStream instream = entity.getContent();
                instream.close();
            }

            httpRequestBase.releaseConnection();
        }
        return moneyHttpResponse;
    }


    public MoneyHttpResponse execute(HttpPost request) throws IOException {
        HttpClient httpclient = HttpClients.custom().setConnectionManager( connectionManager ).setDefaultRequestConfig(requestConfig).build();
        request.setHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=UTF-8");
        // 设置Http Header中的User-Agent属性
        request.setHeader("User-Agent", "Mozilla/4.0");
        MoneyHttpResponse moneyHttpResponse = new MoneyHttpResponse();
        HttpEntity entity = null;
        try {
            HttpResponse response = httpclient.execute(request);
            entity = response.getEntity();
            moneyHttpResponse.setStringResult( EntityUtils.toString(entity, Consts.UTF_8));
        } catch (UnknownHostException ex) {

            return null;
        } catch (IOException ex) {

            return null;
        } catch (Exception ex) {

            return null;
        } finally {
            if( entity != null ){
                InputStream instream = entity.getContent();
                instream.close();
            }

            request.releaseConnection();
        }
        return moneyHttpResponse;

    }

    /**
     * 将NameValuePairs数组转变为字符串
     * 
     * @param nameValues
     * @return
     */
    protected String toString(NameValuePair[] nameValues) {
        if (nameValues == null || nameValues.length == 0) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nameValues.length; i++) {
            NameValuePair nameValue = nameValues[i];

            if (i == 0) {
                buffer.append(nameValue.getName() + "=" + nameValue.getValue());
            } else {
                buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
            }
        }

        return buffer.toString();
    }
}
