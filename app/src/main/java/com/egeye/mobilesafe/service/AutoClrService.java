package com.egeye.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by Octavio on 2016/2/11.
 */
public class AutoClrService extends Service {
    private ScreenOffReceiver receiver;
    private ActivityManager am;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        receiver = new ScreenOffReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        super.onCreate();


    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();

    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        private static final String TAG = "ScreenOffReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "屏幕锁屏了");

            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo info : infos) {
                am.killBackgroundProcesses(info.processName);
            }
        }
    }


}
