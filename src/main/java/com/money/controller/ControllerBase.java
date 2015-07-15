package com.money.controller;

import javax.servlet.http.HttpServletRequest;

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
}
