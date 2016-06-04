package com.egeye.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.egeye.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavio on 2016/2/7.
 * 黑名单数据库的增删改查业务类
 */
public class BlackNumberDao {
    private BlackNumberDBHelper helper;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     *
     * @return
     */
    public boolean find(String number) {
        boolean reslut = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from blacknumber where number = ?",
                new String[]{number});
        if (cursor.moveToNext()) {
            reslut = true;
        }
        cursor.close();
        db.close();
        return reslut;
    }


    /**
     * 查询黑名单号码的拦截模式
     *
     * @param number
     * @return 返回号码的拦截模式，如果不是黑名单号码，返回null
     */
    public String findMode(String number) {
        String reslut = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select mode from blacknumber where number = ?",
                new String[]{number});
        if (cursor.moveToNext()) {
            reslut = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return reslut;
    }

    /**
     * 查询全部黑名单号码
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        List<BlackNumberInfo> reslut = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select number,mode from blacknumber order by _id desc",
                null);
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setMode(mode);
            info.setNumber(number);
            reslut.add(info);
        }
        cursor.close();
        db.close();
        return reslut;
    }

    /**
     * 查询全部黑名单号码2
     * 优化,查询部分的黑名单号码
     *
     * @param offset 从哪个位置获取数据
     * @param maxnum 一次最多获取多少条记录
     * @return
     */
    public List<BlackNumberInfo> findPart(int offset, int maxnum) {
        List<BlackNumberInfo> reslut = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select number,mode from blacknumber order by _id desc limit ? offset ?",
                new String[]{String.valueOf(maxnum), String.valueOf(offset)});
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setMode(mode);
            info.setNumber(number);
            reslut.add(info);
        }
        cursor.close();
        db.close();
        return reslut;
    }

    /**
     * 添加号码到黑名单
     *
     * @param number 号码
     * @param mode   拦截模式 1-电话拦截，2-短信拦截，3-全部拦截
     */
    public void add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    /**
     * 修改黑名单号码的拦截模式
     *
     * @param number  要修改的号码
     * @param newmode 新的拦截模式
     */
    public void update(String number, String newmode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", newmode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除黑名单号码
     *
     * @param number 要删除的号码
     */
    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }
}
