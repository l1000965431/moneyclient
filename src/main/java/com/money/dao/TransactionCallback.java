package com.money.dao;

import org.hibernate.Session;

public interface TransactionCallback {

        void callback( Session session ) throws Exception;
}
