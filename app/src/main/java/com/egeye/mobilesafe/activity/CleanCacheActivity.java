package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egeye.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Octavio on 2016/2/15.
 */
public class CleanCacheActivity extends Activity {
    private ProgressBar pb;
    private LinearLayout llContent;
    private TextView tvCleanStat;

    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clean_cache);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        pb = (ProgressBar) findViewById(R.id.pb_cache_clean);
        llContent = (LinearLayout) findViewById(R.id.ll_cache_context);
        tvCleanStat = (TextView) findViewById(R.id.tv_cache_clean);

        scanCache();

    }

    /**
     * 扫描手机里面所有应用程序的缓存信息
     */
    private void scanCache() {
        pm = getPackageManager();
        new Thread() {
            @Override
            public void run() {

                Method getPackageSizeInfoMethod;
                Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
                    //利用反射的方法获取到PackageManager里的所有方法
                    //System.out.println(method.getName());

                    if ("getPackageSizeInfo".equals(method.getName())) {
                        getPackageSizeInfoMethod = method;
                    }

                }

                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

                for (PackageInfo packageInfo : packageInfos) {
                    //第一个参数代表接受者对象，由谁来执行这个方法
                    //第二个参数
//                    getPackageSizeInfoMethod.invoke(pm,packageInfo.packageName);

                }

            }
        }.start();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    private class MyDataObserver implements IPackageStatsObserver.Stub{
//
//    }

}
