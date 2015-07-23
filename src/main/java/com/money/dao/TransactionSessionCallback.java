package com.money.dao;

import org.hibernate.Session;

public interface TransactionSessionCallback {

        void callback(Session session) throws Exception;
}
