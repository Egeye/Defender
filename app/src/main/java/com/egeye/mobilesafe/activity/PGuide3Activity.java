package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/2.
 */
public class PGuide3Activity extends Activity {

    private EditText etPhone;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_prevent_guide3);
        sp = getSharedPreferences("MDConfig",MODE_PRIVATE);

        etPhone = (EditText) findViewById(R.id.et_phone);

        etPhone.setText(sp.getString("prevent_phone", ""));


    }

    /**
     * 下一步 点击事件
     *
     * @param view
     */
    public void next(View view) {

        //保存安全号码才能下一步

        String phone = etPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"请设置安全号码",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("prevent_phone",phone);
        editor.commit();

        Intent i = new Intent(this, PGuide4Activity.class);
        startActivity(i);
        finish();
    }

    /**
     * 上一步 点击事件
     *
     * @param view
     */
    public void previous(View view) {

        Intent i = new Intent(this, PGuide2Activity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //退出设置对话框
    private AlertDialog dialog;
    private Button btnCancel;
    private Button btnOk;

    /**
     * 退出设置向导
     */
    private void guideBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(PGuide3Activity.this,R.layout.activity_prevent_dialog,null);
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
                    Intent i = new Intent(PGuide3Activity.this,PreventActivity.class);
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
        if(keyCode == KeyEvent.KEYCODE_BACK){
            guideBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void guidexit(View view){
        guideBack();
    }

    /**
     * 选择联系人的按钮功能
     * @param view
     */
    public void selectContact(View view){
        Intent i = new Intent(PGuide3Activity.this,SelectContactActivity.class);

        startActivityForResult(i,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data==null){
            return;
        }

        String phone = data.getStringExtra("phone").replace("-","").replace(" ","");
        etPhone.setText(phone);

    }
}
