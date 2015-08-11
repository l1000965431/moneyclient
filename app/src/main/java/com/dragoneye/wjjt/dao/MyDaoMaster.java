package com.dragoneye.wjjt.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by happysky on 15-6-30.
 */
public class MyDaoMaster {
    private static final String DATABASE_NAME = "money-db";
    private static DaoMaster.DevOpenHelper helper;
    private static DaoMaster daoMaster;
    private static SQLiteDatabase database;

    public static void init(Context context){
        helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
        database = helper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
    }

    public static DaoSession getDaoSession(){
        return daoMaster.newSession();
    }

    public static SQLiteDatabase getDatabase(){
        return database;
    }
}
