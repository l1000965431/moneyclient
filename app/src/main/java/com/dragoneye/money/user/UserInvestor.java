package com.dragoneye.money.user;

import com.dragoneye.money.protocol.UserProtocol;

/**
 * Created by happysky on 15-7-22.
 */
public class UserInvestor extends UserBase {
    @Override
    protected void initUserType(){
        setUserType(UserProtocol.PROTOCOL_USER_TYPE_INVESTOR);
    }
}
