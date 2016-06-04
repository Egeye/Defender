package com.egeye.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager devicePolicyManager;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        btnDelete = (Button) findViewById(R.id.delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.先清除管理员权限
                ComponentName mDeviceAdminSample = new ComponentName(MainActivity.this, MyAdmin.class);
                devicePolicyManager.removeActiveAdmin(mDeviceAdminSample);

                //2.普通应用的卸载
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });

        /*
        //模拟锁屏小按钮
        devicePolicyManager.lockNow();
        finish();
        */

    }


    public void lockScreen(View view) {
        ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);

        if(devicePolicyManager.isAdminActive(mDeviceAdminSample)){
            devicePolicyManager.lockNow();

            devicePolicyManager.resetPassword("", 0);


        }else {
            Toast.makeText(this,"还没有获得管理员权限",Toast.LENGTH_SHORT).show();
            return;
        }


        //清除SD卡上的数据
        //devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);

        //0恢复出厂设置
        //devicePolicyManager.wipeData(0);
    }

    /**
     * 用代码去开启管理员权限
     *
     * @param view
     */
    public void openAdmin(View view) {

        // Launch the activity to have the user enable our admin.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //要激活的组件
        ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);

        //解释
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "我将赐予你最高权限");
        startActivity(intent);
        // return false - don't update checkbox until we're really active


    }


    /**
     * 卸载当前软件
     *
     * @param view
     */
    public void delete(View view) {

        //1.先清除管理员权限
        ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
        devicePolicyManager.removeActiveAdmin(mDeviceAdminSample);

        //2.普通应用的卸载
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);


    }


}
