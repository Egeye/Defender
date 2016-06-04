package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.ui.SettingsItemView;

/**
 * Created by Octavio on 2016/2/2.
 */
public class PGuide2Activity extends Activity {

    private SettingsItemView sivBindsim;
    private TelephonyManager tm;//服务,读取手机sim卡的信息
    private SharedPreferences sp;
    //退出设置对话框
    private AlertDialog dialog;
    private Button btnCancel;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_prevent_guide2);
        sivBindsim = (SettingsItemView) findViewById(R.id.siv_bindsim);
        sp = getSharedPreferences("MDConfig", MODE_PRIVATE);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        String sim = sp.getString("prevent_sim", null);
        if (TextUtils.isEmpty(sim)) {
            //未绑定sim卡
            sivBindsim.setChecked(false);
        } else {
            sivBindsim.setChecked(true);
        }

        sivBindsim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sp.edit();

                if (sivBindsim.isCheck()) {

                    //保存sim卡的序列号为空,即未绑定sim卡
                    sivBindsim.setChecked(false);
                    String sim = tm.getSimSerialNumber();
                    editor.putString("prevent_sim", null);

                } else {

                    //保存sim卡的序列号
                    sivBindsim.setChecked(true);
                    String sim = tm.getSimSerialNumber();
                    editor.putString("prevent_sim", sim);

                }
                editor.commit();
            }
        });


    }

    /**
     * 下一步 点击事件
     *
     * @param view
     */
    public void next(View view) {

        //判断是否绑定了SIM卡
        String sim = sp.getString("prevent_sim",null);
        if(TextUtils.isEmpty(sim)){
            //未绑定
            Toast.makeText(this,"请绑定SIM卡",Toast.LENGTH_LONG).show();
            return;
        }

        Intent i = new Intent(this, PGuide3Activity.class);
        startActivity(i);
        finish();
    }

    /**
     * 上一步 点击事件
     *
     * @param view
     */
    public void previous(View view) {

        Intent i = new Intent(this, PGuide1Activity.class);
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
        View view = View.inflate(PGuide2Activity.this, R.layout.activity_prevent_dialog, null);
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
                    Intent i = new Intent(PGuide2Activity.this,PreventActivity.class);
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
