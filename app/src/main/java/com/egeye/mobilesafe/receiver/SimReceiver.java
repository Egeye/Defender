package com.egeye.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Octavio on 2016/2/3.
 */
public class SimReceiver extends BroadcastReceiver {

    private SharedPreferences sp;
    private TelephonyManager tm;

    @Override
    public void onReceive(Context context, Intent intent) {
        //初始化操作
        sp = context.getSharedPreferences("MDConfig",Context.MODE_PRIVATE);
        boolean status = sp.getBoolean("prevent_protect", false);

        //开启防盗保护
        if(status){
            tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            //读取之前保存的sim卡信息，
            String oldSim = sp.getString("prevent_sim","")+"aaa";

            //读取当前SIM卡信息
            String nowSim = tm.getSimSerialNumber();

            //对比获取到的两个信息
            if(oldSim.equals(nowSim)){
                //一样，同一手机SIM卡

                Toast.makeText(context,"SIM一样",Toast.LENGTH_LONG).show();
                System.out.println("SIM卡一样");

            }else {
                //变更，发一个短信给安全号码
                Toast.makeText(context,"SIM卡已经变更",Toast.LENGTH_LONG).show();
                System.out.println("SIM卡已经变更");

                //发送短信
                //获取安全号码
                String phoneNumber = sp.getString("prevent_phone", "");
                SmsManager.getDefault().sendTextMessage(phoneNumber,null,"SIM card is change!",null,null);

            }
        }

    }
}
