package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.db.PhoneNumberQueryDao;

/**
 * Created by Octavio on 2016/2/5.
 */
public class ToolPhoneBelongActivity extends Activity {

    private EditText editText;//输入的要查询的号码
    private TextView textView;//显示查询号码的结果
    private Vibrator vibrator;//振动器,系统提供的震动服务，需要权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_phone_belong);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        editText = (EditText) findViewById(R.id.et_enter_look_for_num);
        textView = (TextView) findViewById(R.id.tv_show_look_for_result);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() >= 3) {
                    //查询数据库并且显示出查询结果
                    String address = PhoneNumberQueryDao.queryNumber(s.toString());
                    textView.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    /**
     * 查询所输入的号码的归属地
     *
     * @param view
     */
    public void showNumFindResult(View view) {

        String phoneNumber = editText.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(ToolPhoneBelongActivity.this, "请输入要查询的号码", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            editText.startAnimation(shake);

            //震动
            vibrator.vibrate(1000);

            //停、震动，TZ,TZ
            //long[] pattern = {2000,1000,300,300,1000,2000};
            //vibrator.vibrate(pattern,-1);//-1就是不重复，0从0的位置开始循环震动，1从1

            return;
        } else {
            //去数据库查询号码归属地
            String address = PhoneNumberQueryDao.queryNumber(phoneNumber);

            textView.setText(address);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
