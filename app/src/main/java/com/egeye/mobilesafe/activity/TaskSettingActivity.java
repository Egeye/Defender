package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.service.AutoClrService;
import com.egeye.mobilesafe.utils.ServicesStatusTool;

/**
 * Created by Octavio on 2016/2/11.
 */
public class TaskSettingActivity extends Activity {
    private CheckBox cbShowSysTask;
    private CheckBox cbSetTimeClr;
    private CheckBox cbLockStatusClr;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        sp = getSharedPreferences("MDConfig", MODE_PRIVATE);

        cbShowSysTask = (CheckBox) findViewById(R.id.cb_show_systask);
        cbSetTimeClr = (CheckBox) findViewById(R.id.cb_settime_clean);
        cbLockStatusClr = (CheckBox) findViewById(R.id.cb_lockstatus_clean);

        cbShowSysTask.setChecked(sp.getBoolean("task_show_sys", false));

        cbShowSysTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("task_show_sys", isChecked);
                editor.commit();
            }
        });

        /*
        //间隔3秒去执行操作,1秒钟更新一下》？隔1秒钟执行TImer？
        CountDownTimer cdt = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("millisUntilFinished:" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                System.out.println("finish");
            }
        };
        cdt.start();
        /*
         *结果会输出
         * millisUntilFinished
         * millisUntilFinished
         * finish
         */

        cbLockStatusClr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //锁屏的广播事件是一个特殊的广播事件，在清单里面配置广播接收器是不会生效的
                //这种只有在代码里面注册才会生效

                Intent i = new Intent(TaskSettingActivity.this, AutoClrService.class);

                if (isChecked) {
                    startService(i);
                } else {
                    stopService(i);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        boolean running = ServicesStatusTool.isServiceRunning(
                this, "com.egeye.mobilesafe.service.AutoClrService");

        cbLockStatusClr.setChecked(running);

        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
