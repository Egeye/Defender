package com.egeye.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.egeye.mobilesafe.activity.EnterPWDActivity;
import com.egeye.mobilesafe.db.ApplockDao;

import java.util.List;

/**
 * Created by Octavio on 2016/2/13.
 * 看门狗代码，监视系统程序的运行状态
 */
public class WatchDogService extends Service {
    private ScreenOffReceiver offreceiver;
    private InnerWatcheReceiver receiver;
    private ApplockDao dao;
    private ActivityManager am;
    private boolean flag;

    private DataChangeReceiver dataChangeReceiver;

    private String tempStopProtectPackname;

    //代码省电优化 ee
    private List<String> protectPacknames;
    private Intent i;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //数据库要锁定的应用信息即时生效广播
        dataChangeReceiver = new DataChangeReceiver();
        registerReceiver(dataChangeReceiver, new IntentFilter("com.egeye.mobilesafe.applockchange"));

        offreceiver = new ScreenOffReceiver();
        registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        receiver = new InnerWatcheReceiver();
        registerReceiver(receiver, new IntentFilter("com.egeye.mobilesafe.tempstop"));

        dao = new ApplockDao(this);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);


        //优化代码 ee
        protectPacknames = dao.finALL();

        //——代码优化 ee
        //当前应用需要保护
        i = new Intent(getApplicationContext(), EnterPWDActivity.class);
        //服务是没有任务栈信息的，在服务开启Activity
        //要指定这个Activity运行的任务栈，指定这个任务栈状态
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //////////////

        flag = true;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);

                    String packname = infos.get(0).topActivity.getPackageName();
                    //System.out.println("当前用户操作的程序：packagename" + packname);


                    //代码优化 ee
                    //if (dao.find(packname)) {
                    if (protectPacknames.contains(packname)) {
                        //查询内存，比查数据库快很多——代码优化 ee


                        //判断这个应用程序是否需要临时的停止保护
                        if (packname.equals(tempStopProtectPackname)) {

                        } else {

                            /*代码优化 ee
                            //当前应用需要保护
                            Intent i = new Intent(getApplicationContext(), EnterPWDActivity.class);

                            //服务是没有任务栈信息的，在服务开启Activity
                            //要指定这个Activity运行的任务栈，指定这个任务栈状态
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            */

                            //设置要保护程序的包名
                            i.putExtra("packname", packname);

                            startActivity(i);
                        }


                    } else {

                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        flag = false;
        unregisterReceiver(receiver);
        receiver = null;

        unregisterReceiver(offreceiver);
        offreceiver = null;

        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver=null;
    }

    /**
     * 锁屏广播
     */
    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPackname = null;
        }
    }

    private class InnerWatcheReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收到了临时停止保护的广播事件");

            tempStopProtectPackname = intent.getStringExtra("packname");
        }
    }

    //内部的广播接收器，接收数据库里添加要锁定的应用程序信息，实现即时锁定
    private class DataChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            protectPacknames = dao.finALL();
            System.out.println("数据内容发生变化了");
        }
    }
}
