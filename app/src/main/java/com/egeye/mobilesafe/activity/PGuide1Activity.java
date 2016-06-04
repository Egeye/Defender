package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/2.
 */
public class PGuide1Activity extends Activity {

    //退出设置对话框
    private AlertDialog dialog;
    private Button btnCancel;
    private Button btnOk;
    private SharedPreferences sp;

    //gesture.1.定义一个手势识别器
//    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_prevent_guide1);

        //gesture.2.实例化这个手势识别器
//        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//
//            //当手指在上面滑动的时候回调
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//
//
//                return super.onFling(e1, e2, velocityX, velocityY);
//
//            }
//        });


    }


    /**
     * 下一步 点击事件
     *
     * @param view
     */
    public void next(View view) {
        Intent i = new Intent(this, PGuide2Activity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void guidexit(View view) {
        guideBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            guideBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出设置向导
     */
    private void guideBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(PGuide1Activity.this, R.layout.activity_prevent_dialog, null);
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
                    Intent i = new Intent(PGuide1Activity.this,PreventActivity.class);
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
}
