package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

import com.egeye.mobilesafe.R;

import java.util.List;

/**
 * Created by Octavio on 2016/2/14.
 */
public class TrafficManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_traffic_manager);

        //1. 获取一个包管理器
        PackageManager pm = getPackageManager();

        //2.遍历手机操作系统，获取所有的应用程序的uid
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);

        for (ApplicationInfo applicationInfo : applicationInfos) {
            int uid = applicationInfo.uid;

            //上传的流量byte 发送的
            long tx = TrafficStats.getUidTxBytes(uid);

            //下载的流量byte
            long rx = TrafficStats.getUidRxBytes(uid);
            //方法的返回值 -1 代表的是应用程序没有产生流量或者操作系统不支持流量统计
        }

        //获取手机3G/2G网络的上传总流量
        TrafficStats.getMobileTxBytes();

        //下载总流量
        TrafficStats.getMobileRxBytes();

        //手机全部网络接口 包括WiFi，3g，2G上传的总流量
        TrafficStats.getTotalTxBytes();

        //全部下载的总量流量
        TrafficStats.getTotalRxBytes();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
