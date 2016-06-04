package com.egeye.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Octavio on 2016/2/7.
 */
public class AppLockDBHelper extends SQLiteOpenHelper {

    /**
     * 数据库创建的构造方法
     * 数据库名称 applock.db
     *
     * @param context
     */
    public AppLockDBHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock (" +
                "_id integer primary key autoincrement," +
                "packname varchar(20)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
