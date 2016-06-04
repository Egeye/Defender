package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.utils.StreamTool;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Octavio on 2016/1/30.
 */
public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private static final int ENTER_HOME = 1;
    private static final int SHOW_UPDATE_DIALOG = 0;
    private static final int URL_ERROR = 2;
    private static final int JSON_ERROR = 4;
    private static final int NETWORK_ERROR = 3;

    private TextView tvVersion;
    private TextView tvUpdateInfo;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG://显示升级更新的对话框

                    Log.i(TAG, "显示升级的对话框。");

                    showUpdateDialog();

                    break;

                case ENTER_HOME://进入主界面

                    enterHome();
                    break;

                case URL_ERROR:
                    enterHome();
                    Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();
                    break;

                case JSON_ERROR:
                    enterHome();
                    Toast.makeText(getApplicationContext(), "JSON数据解析错误", Toast.LENGTH_SHORT).show();
                    break;

                case NETWORK_ERROR:
                    enterHome();
                    Toast.makeText(SplashActivity.this, "网络异常，无法自动检查更新", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

    };

    //服务器端新版本的相关信息
    private String
            version,
            description,
            apkurl; //新版本的下载地址

    private SharedPreferences sp;


    /**
     * 弹出升级对话框
     */
    private void showUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("提示更新");
        //builder.setCancelable(false);//设置无法取消,用在强制升级，因为有bug
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //点返回，或者点其他地方，等于不想更新
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setMessage(description);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载更新内容，并且安装   // 有SD卡 afinal
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {


                    FinalHttp fh = new FinalHttp();
                    fh.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/mobilesafe2.0apk", new AjaxCallBack<File>() {

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);

                            installAPK(file);

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {

                            t.printStackTrace();//打印错误日志
                            Toast.makeText(getApplicationContext(), "下载失败！", Toast.LENGTH_LONG).show();

                            super.onFailure(t, errorNo, strMsg);
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);

                            tvUpdateInfo.setVisibility(View.VISIBLE);
                            //当前下载百分比
                            int progress = (int) (current * 100 / count);

                            tvUpdateInfo.setText("更新进度：" + progress + "%");
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "没有SD卡，请安装上再试", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });

        builder.show();

    }

    /**
     * 下载好更新后自动安装APK
     *
     * @param file
     */
    private void installAPK(File file) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);

    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //关闭启动界面
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("MDConfig",MODE_PRIVATE);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("Version:" + getVersionName());

        tvUpdateInfo = (TextView) findViewById(R.id.tv_update_info);

        boolean updateStatus = sp.getBoolean("update_status",false);

        //拷贝数据库出来
        copyDB("address.db");
        copyDB("antivirus.db");

        if(updateStatus){
            // 检查更新升级
            checkUpdate();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            },3000);

        }



        //界面切换过渡动画效果
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(1500);
        findViewById(R.id.rl_splash).startAnimation(aa);

        //创建桌面快捷方式图标
        installShortCut();
    }

    /**
     * 创建快捷图标
     */
    private void installShortCut() {
        boolean shortcut = sp.getBoolean("create_shortcut", false);
        if(shortcut)
            return;
        SharedPreferences.Editor editor = sp.edit();

        //发送广播的意图， 大吼一声告诉桌面，要创建快捷图标了
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        //快捷方式  要包含3个重要的信息 1，名称 2.图标 3.干什么事情
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //桌面点击图标对应的意图。
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("android.intent.action.MAIN");
        shortcutIntent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.setClassName(getPackageName(), "com.egeye.mobilesafe.activity.SplashActivity");
		//shortcutIntent.setAction("com.itheima.xxxx");
		//shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        sendBroadcast(intent);
        editor.putBoolean("create_shortcut", true);
        editor.commit();
    }

    /**
     * String path = "data/data/com.egeye.mobilesafe/files/address.db"
     */
    private void copyDB(String fileName) {

        //优化让他只要拷贝一次，避免每次都拷贝
        try {

            File file = new File(getFilesDir(),fileName);

            if(file.exists()&&file.length()>0){
                //已经正常拷贝
                Log.i(TAG,"已经拷贝过数据库了");

            }else {
                InputStream inputStream = getAssets().open(fileName);

                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buffer))!=-1){

                    fileOutputStream.write(buffer,0,length);
                }

                inputStream.close();
                fileOutputStream.close();

            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测是否有新版本，如果有就升级
     */
    private void checkUpdate() {
        new Thread() {
            public void run() {
                Message mes = Message.obtain();

                long startTime = System.currentTimeMillis();

                try {

                    URL url = new URL(getString(R.string.serverurl));

                    // 联网
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // 设置请求方法名称
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        //联网成功
                        InputStream is = conn.getInputStream();

                        // 把流转成String
                        String result = StreamTool.readFromStream(is);
                        Log.i(TAG, "联网成功!" + result);

                        // json解析
                        JSONObject obj = new JSONObject(result);

                        // 得到服务器的版本信息
                        version = (String) obj.get("version");
                        description = (String) obj.get("description");
                        apkurl = (String) obj.get("apkurl");

                        //校验是否有新版本
                        if (getVersionName().equals(version)) {
                            //版本一致,进入程序主界面
                            mes.what = ENTER_HOME;
                        } else {
                            //弹出升级对话框
                            mes.what = SHOW_UPDATE_DIALOG;
                        }

                    }

                } catch (MalformedURLException e) {
                    mes.what = URL_ERROR;
                    e.printStackTrace();

                } catch (IOException e) {
                    mes.what = NETWORK_ERROR;
                    e.printStackTrace();

                } catch (JSONException e) {
                    mes.what = JSON_ERROR;
                    e.printStackTrace();

                } finally {

                    long endTime = System.currentTimeMillis();
                    long dTime = endTime - startTime;//费时

                    if (dTime < 3000) {
                        try {
                            Thread.sleep(3000 - dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    handler.sendMessage(mes);

                }

            }
        }.start();
    }


    /**
     * 动态得到应用程序的版本号
     *
     * @return info.versionName
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        // 用来管理手机上的APK，安装和未安装的都可以

        // 得到指定APK的功能清单文件
        try {
            //pm.getPackageInfo("com.egeye.mobilesafe",0);
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
