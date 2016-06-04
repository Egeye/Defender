package com.egeye.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Octavio on 2016/2/15.
 * 病毒数据库查询的业务类
 */
public class AntiVirusDao {

    /**
     * 查询一个MD5在数据库里是否存在
     *
     * @param md5
     * @return false
     */
    public static boolean isVirus(String md5) {

        String path = "/data/data/com.egeye.mobilesafe/files/antivirus.db";
        boolean result = false;

        //打开病毒库文件
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});

        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();

        return result;
    }

}
