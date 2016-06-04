package com.egeye.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Octavio on 2016/2/6.
 */
public class ServicesStatusTool {

    /**
     * 校验某个服务是否还活着
     * serviceName :传进来的服务的名称
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        //校验服务是否还活着
        //ActivityManager可以管理Activity和Service
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos =  am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info : infos){
            //得到正在运行的服务的名字
            String name = info.service.getClassName();
            if(serviceName.equals(name)){
                return true;
            }
        }
        return false;
    }

}
