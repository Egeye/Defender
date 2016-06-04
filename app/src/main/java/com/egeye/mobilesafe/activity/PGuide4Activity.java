package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/2.
 */
public class PGuide4Activity extends Activity {

    private SharedPreferences sp;
    private CheckBox cbProtectStatus;

    //退出设置对话框
    private AlertDialog dialog;
    private Button btnCancel;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_prevent_guide4);

        sp = getSharedPreferences("MDConfig", MODE_PRIVATE);
        cbProtectStatus = (CheckBox) findViewById(R.id.cb_protect_status);

        boolean protectStatus = sp.getBoolean("prevent_protect", false);
        if(protectStatus){
            //手机防盗已经开启
            cbProtectStatus.setText("已开启防盗保护");
            cbProtectStatus.setChecked(true);

        }else {
            cbProtectStatus.setText("未开启防盗保护");
            cbProtectStatus.setChecked(false);
        }

        cbProtectStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbProtectStatus.setText("已开启防盗保护");
                } else {
                    cbProtectStatus.setText("未开启防盗保护");
                }

                //保存选择的状态
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("prevent_protect", isChecked);
                editor.commit();
            }
        });
    }

    /**
     * 下一步 点击事件
     * 完成事件
     *
     * @param view
     */
    public void next(View view) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("prevent_guide", true);
        editor.commit();

        Intent i = new Intent(this, PreventActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * 上一步 点击事件
     *
     * @param view
     */
    public void previous(View view) {

        Intent i = new Intent(this, PGuide3Activity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 退出设置向导
     */
    private void guideBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(PGuide4Activity.this, R.layout.activity_prevent_dialog, null);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk = (Button) view.findViewById(R.id.btn_ok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dialog.dismiss();

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("MDConfig",MODE_PRIVATE);
                boolean config = sp.getBoolean("prevent_guide", false);
                if(config){
                    Intent i = new Intent(PGuide4Activity.this,PreventActivity.class);
                    startActivity(i);
                    finish();
                    dialog.dismiss();
                }else {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    dialog.dismiss();
                }
            }
        });


        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        //左边的间距0，顶部0，右边0，底部0

        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            guideBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void guidexit(View view) {
        guideBack();
    }
}
