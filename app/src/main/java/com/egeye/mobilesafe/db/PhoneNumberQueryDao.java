package com.egeye.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Octavio on 2016/2/5.
 */
public class PhoneNumberQueryDao {

    private static String path = "data/data/com.egeye.mobilesafe/files/address.db";

    /**
     * 传一个号码，返回一个归属地回去
     *
     * @param number
     * @return addressResult
     */
    public static String queryNumber(String number) {

        SQLiteDatabase database;
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        String addressResult = number;

        //对输入的手机号码进行正则表达式约束是否标准
        if (number.matches("^1[34568]\\d{9}$")) {
            //手机号码

            Cursor cursor = database.rawQuery(
                    "select location from data2 where id = (select outkey from data1 where id = ?)",
                    new String[]{number.substring(0, 7)});

            while (cursor.moveToNext()) {
                String addressQuery = cursor.getString(0);
                addressResult = addressQuery;

            }

            cursor.close();

        } else {
            //其他号码
            switch (number.length()) {
                case 3:
                    //110类似的号码
                    addressResult = "匪警号码";
                    break;

                case 4:

                    //5554
                    addressResult = "模拟器";
                    break;

                case 5:

                    //10086
                    addressResult = "客服号码";
                    break;

                case 8:
                    addressResult = "本地号码";
                    break;

                case 7:
                    addressResult = "本地号码";
                    break;

                default:

                    //处理长途电话
                    if (number.length() >= 10 && number.startsWith("0")) {

                        //010-59790376
                        Cursor cursor = database.rawQuery(
                                "select location from data2 where area =?",
                                new String[]{number.substring(1, 3)});


                        while (cursor.moveToNext()) {

                            String addressQuery = cursor.getString(0);
                            addressResult = addressQuery.substring(0, addressQuery.length() - 2);

                        }

                        cursor.close();


                        //0855-59790376
                        cursor = database.rawQuery(
                                "select location from data2 where area =?",
                                new String[]{number.substring(1, 4)});

                        while (cursor.moveToNext()) {

                            String addressQuery = cursor.getString(0);
                            addressResult = addressQuery.substring(0, addressQuery.length() - 2);

                        }

                        cursor.close();

                    }
                    break;


            }

        }


        return addressResult;
    }


}
