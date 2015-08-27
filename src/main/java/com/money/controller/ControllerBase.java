package com.money.controller;

import com.aliyun.openservices.ons.api.Action;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by liumin on 15/7/6.
 */

public class ControllerBase implements IController {

    public ControllerBase(){
    }

    public String getClientType(HttpServletRequest request) {
        return request.getHeader( "ClientType" );
    }

    public String getClientOS(HttpServletRequest request) {
        return request.getHeader( "ClientOS" );
    }

    public int getClientVersion(HttpServletRequest request) {
        String ClientVersion = request.getHeader( "ClientVersion" );

        if( ClientVersion != null ){
            return Integer.parseInt( ClientVersion );
        }else{
            return -1;
        }
    }

    public String getClientToken(HttpServletRequest request) {
        return   request.getHeader( "ClientToken" );
    }

    public Long getClientTimestamp(HttpServletRequest request) {


        try {
            String ClientTimestamp = request.getHeader( "ClientTimestamp" );
            if( ClientTimestamp != null ){
                return Long.parseLong(ClientTimestamp);
            }else{
                return Long.valueOf(-1);
            }
        }catch (Exception e){
            return Long.valueOf(-1);
        }
    }

    public String getrequestReader( HttpServletRequest request ){
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

}
