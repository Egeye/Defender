package com.egeye.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by Octavio on 2016/2/12.
 */
public class KillAllReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("自定义的广播消息接收到了，killalll");
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : infos) {
            am.killBackgroundProcesses(info.processName);
        }

    }
}
