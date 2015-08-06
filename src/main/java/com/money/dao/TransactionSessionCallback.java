package com.money.dao;

import org.hibernate.Session;

public interface TransactionSessionCallback {

        boolean callback(Session session) throws Exception;
}
