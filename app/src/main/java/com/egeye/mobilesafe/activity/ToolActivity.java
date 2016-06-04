package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.utils.SmsTool;

/**
 * Created by Octavio on 2016/2/5.
 */
public class ToolActivity extends Activity {
    private ProgressBar progressBar;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        progressBar = (ProgressBar) findViewById(R.id.pb_backup_sms);

    }

    /**
     * 短信备份
     *
     * @param view
     */
    public void smsBackup(View view) {
        progressBar.setVisibility(View.VISIBLE);
        /*
         * 玩具程序的写法
        try {
            SmsTool.backupSms(this);
            Toast.makeText(this, "短信备份成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "短信备份失败", Toast.LENGTH_SHORT).show();
        }
        */

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份短信……");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    SmsTool.backupSms(ToolActivity.this, new SmsTool.IBackupCallback() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                            progressBar.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            pd.setProgress(progress);
                            progressBar.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    ToolActivity.this,
                                    "短信备份成功",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    ToolActivity.this,
                                    "短信备份失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    pd.dismiss();
                }
            }
        }.start();

    }

    /**
     * 短信恢复
     *
     * @param view
     */
    public void smsRestore(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("提示");
        builder.setMessage("短信恢复将会删除手机上的现有短信，确定继续吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmsTool.restoreSms(ToolActivity.this, true);
                Toast.makeText(getApplicationContext(), "短信还原成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 点击进入查询号码归属地页面
     *
     * @param view
     */
    public void lookForPhoneNumber(View view) {
        Intent i = new Intent(this, ToolPhoneBelongActivity.class);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
