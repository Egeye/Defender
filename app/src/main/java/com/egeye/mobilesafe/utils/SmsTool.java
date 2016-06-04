package com.egeye.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Octavio on 2016/2/9.
 * 短信的工具类
 */
public class SmsTool {

    /**
     * 还原短信
     *
     * @param context
     * @param flag    是否保留原来的短信
     */
    public static void restoreSms(Context context, boolean flag) {
        Uri uri = Uri.parse("content://sms/");

        if (flag) {
            context.getContentResolver().delete(uri, null, null);
            //清理掉所有旧的短信
        }

        //1.读取SD卡上的XML文件

        //2.读取Max

        //3.读取每一条短信信息，address，body，date，type

        //4.把短信插入到系统短信应用中
        ContentValues values = new ContentValues();
        values.put("body", "test test neirong");
        values.put("date", "121212");
        values.put("address", "1212");
        values.put("type", "1");
        context.getContentResolver().insert(uri, values);
    }

    /**
     * 备份用户的短信
     *
     * @param context         上下文
     * @param iBackupCallback 备份短信的回调接口
     * @throws Exception
     */
    public static void backupSms(Context context, IBackupCallback iBackupCallback) throws Exception {
        ContentResolver resolver = context.getContentResolver();

        File file = new File(Environment.getExternalStorageDirectory(), "Dbackup.xml");
        FileOutputStream fos = new FileOutputStream(file);

        //把用户的短信一条条读取出来，并按照一定的格式写到文件里
        //获取XML文件的生成器（序列化器）
        XmlSerializer serializer = Xml.newSerializer();

        //初始化生成器
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);

        serializer.startTag(null, "smss");

        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(
                uri,
                new String[]{"body", "address", "type", "date"},
                null, null, null);

        //备份的最大值.开始备份的时候，设置进度条的最大值
        int max = cursor.getCount();
        //pb.setMax(max);
        iBackupCallback.beforeBackup(max);

        int process = 0;
        serializer.attribute(null, "max", max + "");
        while (cursor.moveToNext()) {
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");

            //备份过程中增加的进度
            process++;
            //pb.setProgress(process);
            iBackupCallback.onSmsBackup(process);
        }
        cursor.close();

        serializer.endTag(null, "smss");

        serializer.endDocument();
        fos.close();
    }

    /**
     * 短信备份的回调接口
     */
    public interface IBackupCallback {

        /**
         * 短信备份开始的时候，设置进度的最大值
         *
         * @param max
         */
        public void beforeBackup(int max);

        /**
         * 备份过程中，增加进度
         *
         * @param progress
         */
        public void onSmsBackup(int progress);
    }
}
