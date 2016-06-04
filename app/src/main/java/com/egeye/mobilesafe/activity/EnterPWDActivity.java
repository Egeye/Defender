package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/13.
 */
public class EnterPWDActivity extends Activity {

    private EditText metPwd;
    private String packname;
    private ImageView ivAppIcon;
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        metPwd = (EditText) findViewById(R.id.met_app_watch);

        //当前要保护的程序包名和图标信息
        ivAppIcon = (ImageView) findViewById(R.id.iv_watch_app_icon);
        tvAppName = (TextView) findViewById(R.id.tv_watch_app_name);

        //当前要保护的应用程序的包名
        Intent i = getIntent();
        packname = i.getStringExtra("packname");

        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);

            tvAppName.setText(info.loadLabel(pm));
            ivAppIcon.setImageDrawable(info.loadIcon(pm));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //回到桌面
        //        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        //所有的activity最小化 不会执行ondestory 只执行 onstop方法。
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void confirmPwd(View view) {
        String pwd = metPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "请输入密码解锁", Toast.LENGTH_SHORT).show();
            return;
        }

        //假设正确的密码是123
        if ("123".equals(pwd)) {
            //告诉看门狗这个应用已输入正确访问密码，可以暂时停止保护了
            //自定义广播
            Intent i = new Intent();
            i.setAction("com.egeye.mobilesafe.tempstop");
            i.putExtra("packname", packname);

            sendBroadcast(i);

            finish();
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}
