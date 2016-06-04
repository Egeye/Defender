package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.db.AntiVirusDao;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by Octavio on 2016/2/14.
 */
public class AntiVirusActivity extends Activity {

    private static final int SCANNING = 0;
    private static final int FINISH = 2;

    private ImageView ivScanning;
    private ProgressBar progressBar;
    private PackageManager pm;
    private TextView tvScanStat;
    private LinearLayout llScanText;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SCANNING:
                    //正在扫描
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tvScanStat.setText("正在扫描：" + scanInfo.name);

                    TextView tv = new TextView(getApplicationContext());
                    if (scanInfo.isVirus) {
                        tv.setTextColor(Color.RED);
                        tv.setText("发现病毒:" + scanInfo.name);
                    } else {
                        tv.setTextColor(0xff82B651);
                        tv.setText("扫描安全：" + scanInfo.name);
                    }
                    llScanText.addView(tv, 0);
                    break;

                case FINISH:
                    tvScanStat.setText("扫描完毕");
                    ivScanning.clearAnimation();
                    ivScanning.setVisibility(View.INVISIBLE);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_anti_virus);

        ivScanning = (ImageView) findViewById(R.id.iv_scanning);
        progressBar = (ProgressBar) findViewById(R.id.pb_scan);
        tvScanStat = (TextView) findViewById(R.id.tv_scan_status);
        llScanText = (LinearLayout) findViewById(R.id.ll_scan_context);

        //设置动画
        RotateAnimation ra = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(1000);

        ra.setRepeatCount(Animation.INFINITE);

        ivScanning.startAnimation(ra);

        /*
        //进度条测试
        progressBar.setMax(100);
        new Thread() {
            @Override
            public void run() {

                    for (int i = 0; i < 100; i++) {
                        try {
                            Thread.sleep(100);

                            progressBar.setProgress(i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }.start();
        */

        scanVirus();

    }

    /**
     * 扫描病毒
     */
    private void scanVirus() {
        pm = getPackageManager();

        tvScanStat.setText("正在初始化杀毒引擎……");

        ////////////////////////////////////////
        /*耗时，写到子线程里面
        *
        //得到手机上所有安装的应用包信息
        List<PackageInfo> infos = pm.getInstalledPackages(0);

        for (PackageInfo info : infos) {
            //String datadir = info.applicationInfo.dataDir;

            //apk完整的路径
            String sourcedir = info.applicationInfo.sourceDir;

            //System.out.println(datadir);
            //System.out.println("---------------------");
            //System.out.println(source);

            String md5 = getFileMd5(sourcedir);
            //System.out.println(info.applicationInfo.loadLabel(pm) + " " + md5);

            //查询md5信息，是否在病毒数据库里面存在
            if (AntiVirusDao.isVirus(md5)){
                //发现病毒

            }else {
                //扫描安全
            }

        }
        *//////////////////////////////

        new Thread() {
            @Override
            public void run() {

                List<PackageInfo> infos = pm.getInstalledPackages(0);

                //为了看到“正在初始化……”，延时
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progressBar.setMax(infos.size());
                int progress = 0;

                for (PackageInfo info : infos) {

                    //apk完整的路径
                    String sourcedir = info.applicationInfo.sourceDir;
                    String md5 = getFileMd5(sourcedir);

                    ScanInfo scanInfo = new ScanInfo();
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packname = info.packageName;

                    //查询md5信息，是否在病毒数据库里面存在
                    if (AntiVirusDao.isVirus(md5)) {
                        //发现病毒
                        scanInfo.isVirus = true;

                    } else {
                        //扫描安全
                        scanInfo.isVirus = false;
                    }

                    Message msg = Message.obtain();
                    msg.obj = scanInfo;
                    msg.what = SCANNING;
                    handler.sendMessage(msg);

                    //毫无意义的睡眠时间，只是为了让用户感觉杀毒软件在很认真的工作 - -
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progress++;
                    progressBar.setProgress(progress);

                }
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获取文件的md5值
     *
     * @param path 文件的全路径名
     * @return
     */
    private String getFileMd5(String path) {
        try {
            //获取一个文件的特征信息，签名信息
            File file = new File(path);

            //md5
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];

            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }

            byte[] result = digest.digest();

            StringBuffer sb = new StringBuffer();

            for (byte b : result) {
                //与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);

                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 扫描信息的内部类
     */
    class ScanInfo {
        String packname;
        String name;
        boolean isVirus;
    }

}
