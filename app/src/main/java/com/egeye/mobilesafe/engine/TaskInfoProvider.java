package com.egeye.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavio on 2016/2/11.
 * 提供手机里面的进程信息
 */
public class TaskInfoProvider {
    /**
     * 获取所有的进程信息
     *
     * @param context 上下文
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        PackageManager pm = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();


        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            TaskInfo taskInfo = new TaskInfo();

            //应用程序的包名。
            String packname = processInfo.processName;
            taskInfo.setPackname(packname);

            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(
                    new int[]{processInfo.pid});

            long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024l;
            taskInfo.setMemsize(memsize);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);

                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //用户进程
                    taskInfo.setUserTask(true);
                } else {
                    //系统进程
                    taskInfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
                taskInfo.setName(packname);
            }
            taskInfos.add(taskInfo);
        }


        return taskInfos;
    }
}
