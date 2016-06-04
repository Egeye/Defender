package com.egeye.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.egeye.mobilesafe.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavio on 2016/2/9.
 * 业务方法，提供手机里面安装的所有应用程序信息
 */
public class AppInfoProvider {

    /**
     * 获取所有安装的应用程序信息
     *
     * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {
        PackageManager pm = context.getPackageManager();

        //所有的安装在系统上的应用程序包的信息
        List<PackageInfo> packinfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        for (PackageInfo packageInfo : packinfos) {
            AppInfo appInfo = new AppInfo();

            //相当于一个应用程序apk包的清单文件
            String packname = packageInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();

            //应用程序信息的标记.相当于用户提交的答卷
            int flags = packageInfo.applicationInfo.flags;

            //操作系统分配给应用系统的一个固定的编号，一旦应用程序被安装到手机，id就固定下来
            int uid = packageInfo.applicationInfo.uid;
            appInfo.setUid(uid);

//            File rcvFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
//            File sndFile = new File("/proc/uid_stat/" + uid + "/tcp_snd");

            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //代表这个flag没有被选中，即为用户的应用
                appInfo.setUserApp(true);
            } else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //装在手机的内存中
                appInfo.setInRom(true);
            } else {
                //装在外存储设备
                appInfo.setInRom(false);
            }

            appInfo.setPackageName(packname);
            appInfo.setIcon(icon);
            appInfo.setName(name);

            appInfos.add(appInfo);
        }

        return appInfos;
    }
}
