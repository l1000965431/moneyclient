package com.money.controller;

import com.aliyun.openservices.ons.api.Action;
import com.google.gson.reflect.TypeToken;
import com.money.Service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import until.DESCoder;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by liumin on 15/7/6.
 */

public class ControllerBase implements IController {

    @Autowired
    protected UserService userService;

    public ControllerBase() {
    }

    public String getClientType(HttpServletRequest request) {
        return request.getHeader("ClientType");
    }

    public String getClientOS(HttpServletRequest request) {
        return request.getHeader("ClientOS");
    }

    public int getClientVersion(HttpServletRequest request) {
        String ClientVersion = request.getHeader("ClientVersion");

        if (ClientVersion != null) {
            return Integer.parseInt(ClientVersion);
        } else {
            return -1;
        }
    }

    public String getClientToken(HttpServletRequest request) {
        return request.getHeader("ClientToken");
    }

    public Long getClientTimestamp(HttpServletRequest request) {
        try {
            String ClientTimestamp = request.getHeader("ClientTimestamp");
            if (ClientTimestamp != null) {
                return Long.parseLong(ClientTimestamp);
            } else {
                return Long.valueOf(-1);
            }
        } catch (Exception e) {
            return Long.valueOf(-1);
        }
    }

    public String getrequestReader(HttpServletRequest request) {
        BufferedReader reader;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = request.getReader();
            String string;
            while ((string = reader.readLine()) != null) {
                buffer.append(string);
            }
            reader.close();

            return buffer.toString();

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 验证用户登录
     *
     * @param userId
     * @param token
     * @return
     */
    protected boolean UserIsLand(String userId, String token) {
        if (token == null || token.length() == 0) {
            return false;
        }

        return userService.tokenLand(userId, token) == 1;
    }

    /**
     * 验证签名的合法性
     */

    protected boolean VerifySignLegal(String Url, String sign) {


        return false;
    }


    /**
     * 对加密信息解密
     */
    protected String DecryptionDataByUserToken(String Data, String userId) {
        String token = userService.getUserToken(userId);

        return null;

    }

    protected String DecryptionDataByUserId(String data, String userId) {
        if (data == null || userId == null) {
            return null;
        }

        try {
            data = data.replaceAll("\\s*", "");
            byte[] byteData = DESCoder.decryptBASE64( data );
            String DESCoderStr = new String(DESCoder.decrypt(byteData, userId), "UTF-8");
            return DESCoderStr;
        } catch (Exception e) {
            return null;
        }
    }

    protected Map DecryptionDataToMapByUserToken(String Data, String userId) {
        Map map = GsonUntil.jsonToJavaClass(DecryptionDataByUserToken(Data, userId), new TypeToken<Map>() {
        }.getType());
        return map;
    }

    protected Map DecryptionDataToMapByUserId(String Data, String userId) {
        if( Data == null || userId == null ){
            return null;
        }

        Map map = GsonUntil.jsonToJavaClass(DecryptionDataByUserId(Data, userId), new TypeToken<Map>() {
        }.getType());
        return map;
    }

    protected String initDesKey(String key) {
        if( key == null )
            return null;

        while (key.length() < 24) {
            key = key + key;
            if (key.length() >= 24) {
                key.substring(0, 24);
            }
        }

        return key;
    }

}
