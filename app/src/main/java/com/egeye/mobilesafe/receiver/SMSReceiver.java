package com.egeye.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.service.GPSService;


/**
 * Created by Octavio on 2016/2/3.
 */
public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "EGEYE";
    private SharedPreferences sp;
    private DevicePolicyManager devicePolicyManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        sp = context.getSharedPreferences("MDConfig", Context.MODE_PRIVATE);
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        //接收短信广播
        Object[] myobject = (Object[]) intent.getExtras().get("pdus");


        for (Object b : myobject) {

            //具体的某一条短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);

            //短信发送者
            String sender = sms.getOriginatingAddress();
            //短信内容
            String body = sms.getMessageBody();

            //提取安全号码
            String phoneNum = sp.getString("prevent_phone", "");

            //获取当前位置信息
            String gpsLocation = sp.getString("prevent_location", null);

//            //测试代码!!
//            String testNum = "1555521" + phoneNum;
            //if (sender.equals(testNum)) {

            if (sender.equals(phoneNum)) {

                if ("#*location*#".equals(body)) {
                    //得到手机的GPS
                    Log.i(TAG, "得到手机的GPS");

                    Intent i = new Intent(context, GPSService.class);
                    context.startService(i);


                    if (TextUtils.isEmpty(gpsLocation)) {
                        //位置未得到
                        SmsManager.getDefault().sendTextMessage(sender, null, "GPSing", null, null);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sender, null, gpsLocation, null, null);
                    }

                    //终止掉这个广播，即不让手机接收到短信
                    abortBroadcast();

                } else if ("#*alarm*#".equals(body)) {

                    Log.i(TAG, "播放报警音乐");

                    MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                    player.start();
                    player.setLooping(true);
                    player.setVolume(1.0f, 1.0f);

                    abortBroadcast();

                } else if ("#*wipedata*#".equals(body)) {

                    Log.i(TAG, "远程清除数据");
                    //清除SD卡上的数据
                    //devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);

                    //0恢复出厂设置
                    //devicePolicyManager.wipeData(0);
                    abortBroadcast();

                } else if ("#*lockscreen*#".equals(body)) {

                    Log.i(TAG, "远程屏幕锁屏");

                    ComponentName mDeviceAdminSample = new ComponentName(context, MyAdmin.class);
                    if(devicePolicyManager.isAdminActive(mDeviceAdminSample)){
                        devicePolicyManager.lockNow();
                        devicePolicyManager.resetPassword("", 0);
                    }else {
                        Toast.makeText(context, "还没有获得管理员权限", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    abortBroadcast();

                }

            }


        }

    }
}
