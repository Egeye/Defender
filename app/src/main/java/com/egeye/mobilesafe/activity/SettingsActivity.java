package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.service.CallSmsSafeService;
import com.egeye.mobilesafe.service.PhoneAddressService;
import com.egeye.mobilesafe.service.WatchDogService;
import com.egeye.mobilesafe.ui.SettingsClickView;
import com.egeye.mobilesafe.ui.SettingsItemView;
import com.egeye.mobilesafe.utils.ServicesStatusTool;

/**
 * Created by Octavio on 2016/2/1.
 */
public class SettingsActivity extends Activity {
    //设置自动更新
    private SettingsItemView siUpdate;
    private SharedPreferences sp;

    //设置来电显示
    private SettingsItemView sivIncomingAddrShow;
    private Intent incomingASIntent;

    //设置归属地显示框背景主题
    private SettingsClickView settingsClickView;

    //黑名单拦截设置
    private SettingsItemView sivSmsCallSafe;
    private Intent smsCallSafeIntent;

    //设置看门狗
    private SettingsItemView sivApplock;
    private Intent watchAppIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        sp = getSharedPreferences("MDConfig", MODE_PRIVATE);
        settingsClickView = (SettingsClickView) findViewById(R.id.scv_theme_show_addr);
        siUpdate = (SettingsItemView) findViewById(R.id.si_update);
        sivIncomingAddrShow = (SettingsItemView) findViewById(R.id.siv_incall_show_addr);
        sivApplock = (SettingsItemView) findViewById(R.id.siv_app_watchdog);

        incomingASIntent = new Intent(this, PhoneAddressService.class);

        boolean updateStatus = sp.getBoolean("update_status", false);
        if (updateStatus) {
            siUpdate.setChecked(true);
            // siUpdate.setStatus("自动更新已经开启");
        } else {
            siUpdate.setChecked(false);
            // siUpdate.setStatus("自动更新已经关闭");
        }

        int which = sp.getInt("setting_theme", 0);
        final String[] themeName = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
        settingsClickView.setStatus(themeName[which]);
        //组合控件的点击事件，归属地提示框风格
        settingsClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whichChosen = sp.getInt("setting_theme", 0);
                //对话框选择
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(themeName, whichChosen, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //保存选择的参数信息
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("setting_theme", which);
                        editor.commit();
                        settingsClickView.setStatus(themeName[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        siUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sp.edit();

                //判断是否选中
                if (siUpdate.isCheck()) {

                    siUpdate.setChecked(false);
                    // siUpdate.setStatus("自动更新已经关闭");
                    editor.putBoolean("update_status", false);

                } else {

                    siUpdate.setChecked(true);
                    // siUpdate.setStatus("自动更新已经开启");
                    editor.putBoolean("update_status", true);

                }

                editor.commit();


            }
        });

        //判断显示来电归属地的服务是否开启
        boolean isServiceRunning;
        isServiceRunning = ServicesStatusTool.isServiceRunning(this,
                "com.egeye.mobilesafe.service.PhoneAddressService");
        if (isServiceRunning) {
            //运行状态
            sivIncomingAddrShow.setChecked(true);
        } else {
            sivIncomingAddrShow.setChecked(false);
        }

        //来电归属地显示
        sivIncomingAddrShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听来电显示服务已经开启
                if (sivIncomingAddrShow.isCheck()) {
                    //关闭
                    sivIncomingAddrShow.setChecked(false);
                    stopService(incomingASIntent);
                } else {
                    //开启
                    sivIncomingAddrShow.setChecked(true);
                    startService(incomingASIntent);
                }
            }
        });


        //判断黑名单拦截的服务是否开启
        sivSmsCallSafe = (SettingsItemView) findViewById(R.id.siv_smscall_safe);
        smsCallSafeIntent = new Intent(this, CallSmsSafeService.class);
        sivSmsCallSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听来电显示服务已经开启
                if (sivSmsCallSafe.isCheck()) {
                    //关闭
                    sivSmsCallSafe.setChecked(false);
                    stopService(smsCallSafeIntent);
                } else {
                    //开启
                    sivSmsCallSafe.setChecked(true);
                    startService(smsCallSafeIntent);
                }
            }
        });

        //判断程序锁的服务是否开启
        watchAppIntent = new Intent(this, WatchDogService.class);
        sivApplock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听来电显示服务已经开启
                if (sivApplock.isCheck()) {
                    //关闭
                    sivApplock.setChecked(false);
                    stopService(watchAppIntent);
                } else {
                    //开启
                    sivApplock.setChecked(true);
                    startService(watchAppIntent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //判断显示来电归属地的服务是否开启
        boolean isServiceRunning;
        isServiceRunning = ServicesStatusTool.isServiceRunning(this,
                "com.egeye.mobilesafe.service.PhoneAddressService");
        if (isServiceRunning) {
            //运行状态
            sivIncomingAddrShow.setChecked(true);
        } else {
            sivIncomingAddrShow.setChecked(false);
        }

        //判断黑名单拦截服务是否允许
        boolean isSmscallRunning = ServicesStatusTool.isServiceRunning(this,
                "com.egeye.mobilesafe.service.CallSmsSafeService");
        sivSmsCallSafe.setChecked(isSmscallRunning);

        //判断看门狗服务是否开启
        boolean isAppWatchServiceRunning = ServicesStatusTool.isServiceRunning(this,
                "com.egeye.mobilesafe.service.WatchDogService");
        sivApplock.setChecked(isAppWatchServiceRunning);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
