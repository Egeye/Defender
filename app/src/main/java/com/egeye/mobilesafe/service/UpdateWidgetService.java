package com.egeye.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.receiver.MyWidgetReceiver;
import com.egeye.mobilesafe.utils.SystemInfoTool;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Octavio on 2016/2/12.
 */
public class UpdateWidgetService extends Service {
    private static final String TAG = "UpdateWidgetService";

    private ScreenOffReceiver offreceiver;
    private ScreenOnReceiver onreceiver;

    private Timer timer;
    private TimerTask task;
    private AppWidgetManager awm;

    @Override
    public void onCreate() {
        awm = AppWidgetManager.getInstance(this);

        onreceiver = new ScreenOnReceiver();
        offreceiver = new ScreenOffReceiver();

        registerReceiver(onreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        startTimer();
        super.onCreate();
    }

    private void startTimer() {
        if (timer == null && task == null) {
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "更新widget");

                    //设置更新的组件
                    ComponentName provider = new ComponentName(
                            UpdateWidgetService.this, MyWidgetReceiver.class);

                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_task_cleaner);

                    views.setTextViewText(R.id.process_count, "正在运行的进程："
                            + SystemInfoTool.getRunningProcessCount(getApplicationContext()) + "个");

                    long size = SystemInfoTool.getAvailMem(getApplicationContext());

                    views.setTextViewText(R.id.process_memory,
                            "可用内存：" + Formatter.formatFileSize(getApplicationContext(), size));

                    //自定义一个广播事件，杀死后台进程的事件
                    Intent i = new Intent();
                    i.setAction("com.egeye.mobilesafe.killall");

                    //描述一个动作，这个动作是由另外一个应用程序执行
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(),
                            0, i,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    views.setOnClickPendingIntent(R.id.ll_clear, pendingIntent);


                    awm.updateAppWidget(provider, views);
                }
            };
            //0,立即执行（第一次执行延迟多久）,每个3秒钟更新一次
            timer.schedule(task, 0, 3000);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer();

        unregisterReceiver(onreceiver);
        unregisterReceiver(offreceiver);
        onreceiver = null;
        offreceiver = null;


    }

    private void stopTimer() {
        if (timer != null && task != null) {
            timer.cancel();
            task.cancel();
            task = null;
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "屏幕锁屏了");

            stopTimer();
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "屏幕锁屏了");

            startTimer();
        }
    }


}
