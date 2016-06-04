package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.utils.MD5Tool;

/**
 * Created by Octavio on 2016/2/2.
 */
public class PreventActivity extends Activity {

    private SharedPreferences sp;

    //安全号码
    private TextView tvPhoneNumber;
    private ImageView ivStatus;

    //重新设置密码
    private EditText etSet;
    private EditText etConfirm;
    private Button btnOk;
    private Button btnCancel;
    private AlertDialog dialog;
    private Button btnClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //判断一下是否进行过设置向导，没有就跳到设置向导界面
        sp = getSharedPreferences("MDConfig", MODE_PRIVATE);

        boolean config = sp.getBoolean("prevent_guide", false);
        if (config) {

            //手机防盗界面
            setContentView(R.layout.activity_prevent);

            tvPhoneNumber = (TextView) findViewById(R.id.tv_security_num);
            ivStatus = (ImageView) findViewById(R.id.iv_prevent_status);

            //得到安全号码
            String phoneNumber = sp.getString("prevent_phone","");
            tvPhoneNumber.setText(phoneNumber);

            //设置状态图片
            boolean status = sp.getBoolean("prevent_protect",false);
            if(status){
                ivStatus.setImageResource(R.drawable.status_lock);
            }else {
                ivStatus.setImageResource(R.drawable.status_unlock);
            }

        } else {
            //手机防盗设置向导
            Intent i = new Intent(PreventActivity.this, PGuide1Activity.class);
            startActivity(i);
            finish();

        }


    }

    /**
     * 重新进行设置向导
     *
     * @param view
     */
    public void reGuide(View view) {
        Intent i = new Intent(PreventActivity.this, PGuide1Activity.class);
        startActivity(i);
        finish();
    }

    /**
     * 重新设置密码
     *
     * @param view
     */
    public void reSet(View view) {
        Toast.makeText(this,"重设密码之后将会需要重新登录",Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(PreventActivity.this);

        //自定义一个布局文件，自定义控件带有输入框
        View dialogView = View.inflate(PreventActivity.this, R.layout.activity_home_dialog_setpwd, null);

        etSet = (EditText) dialogView.findViewById(R.id.et_set_pwd);
        etConfirm = (EditText) dialogView.findViewById(R.id.et_confirm_pwd);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);

        btnClean = (Button) dialogView.findViewById(R.id.btn_clean);

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSet.setText("");
                etConfirm.setText("");
            }
        });

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
                //获取密码
                String password = etSet.getText().toString().trim();
                String confirmPwd = etConfirm.getText().toString().trim();

                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPwd)) {
                    Toast.makeText(PreventActivity.this, "请正确输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                //判断是否一致，然后保存
                if (password.equals(confirmPwd)) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("prevent_password", MD5Tool.md5password(password));


                    //提交数据到XML文件中进行保存
                    editor.commit();

                    dialog.dismiss();
                    Intent i = new Intent(PreventActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Toast.makeText(getBaseContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        dialog = builder.create();
        dialog.setView(dialogView, 0, 0, 0, 0);
        //左边的间距0，顶部0，右边0，底部0

        dialog.show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
