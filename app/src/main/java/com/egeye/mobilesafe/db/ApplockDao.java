package com.egeye.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavio on 2016/2/13.
 * 程序锁的dao
 */
public class ApplockDao {

    private AppLockDBHelper helper;
    private Context context;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public ApplockDao(Context context) {
        helper = new AppLockDBHelper(context);
        this.context = context;
    }

    /**
     * 添加一个锁定应用程序的报名
     *
     * @param packname
     */
    public void add(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("packname", packname);
        db.insert("applock", null, values);
        db.close();

        Intent i = new Intent();
        i.setAction("com.egeye.mobilesafe.applockchange");
        context.sendBroadcast(i);
    }

    /**
     * 解除被锁定的应用的包名
     *
     * @param packname
     */
    public void delete(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();

        Intent i = new Intent();
        i.setAction("com.egeye.mobilesafe.applockchange");
        context.sendBroadcast(i);
    }


    /**
     * 查询一条程序锁包名记录
     *
     * @param packname
     * @return
     */
    public boolean find(String packname) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }

        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部的包名
     *
     * @return
     */
    public List<String> finALL() {

        List<String> protectPacknames = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            protectPacknames.add(cursor.getString(0));
        }

        cursor.close();
        db.close();
        return protectPacknames;
    }

}
