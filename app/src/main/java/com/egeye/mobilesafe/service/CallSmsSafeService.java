package com.egeye.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.egeye.mobilesafe.db.BlackNumberDao;

/**
 * Created by Octavio on 2016/2/8.
 */
public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private InnerSmsRecevier recevier;
    private BlackNumberDao dao;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dao = new BlackNumberDao(this);
        recevier = new InnerSmsRecevier();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(recevier, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(recevier);
        recevier = null;
        super.onDestroy();
    }

    /**
     * 利用内容提供者删除呼叫记录
     * to be continue
     *
     * @param number
     */
    private void deleteCallLog(String number) {
        ContentResolver resolver = getContentResolver();
        //呼叫记录的Uri路径
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{number});
    }

    private class InnerSmsRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "内部广播接收器，短信到来了");

            //检测发件人是否为黑名单号码，并且有设置短信拦截和全部拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);
                if ("2".equals(result) || "3".equals(result)) {
                    Log.i(TAG, "拦截短信");
                    abortBroadcast();
                }
            }
        }
    }
}
