package com.egeye.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Octavio on 2016/2/7.
 */
public class BlackNumberDBHelper extends SQLiteOpenHelper {

    /**
     * 数据库创建的构造方法
     *
     * @param context
     */
    public BlackNumberDBHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (" +
                "_id integer primary key autoincrement," +
                "number varchar(20)," +
                "mode varchar(2)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
