package com.egeye.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.db.PhoneNumberQueryDao;

/**
 * Created by Octavio on 2016/2/6.
 */
public class PhoneAddressService extends Service {
    private static final String TAG = "PhoneAddressService";
    private SharedPreferences sharedPreferences;
    //监听来电
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;

    //监听去电
    private DialReceiver dialReceiver;

    //窗体管理者，服务
    private WindowManager windowManager;

    //自定义土司的视图
    //private TextView textView;
    private View view;
    private WindowManager.LayoutParams params;

    //双击居中
    long [] mHits = new long[2];

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MDConfig", MODE_PRIVATE);

        //注册一个广播接收器
        dialReceiver = new DialReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(dialReceiver, intentFilter);

        //监听来电，实例化自己的监听类
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //实例化窗体
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播接收器
        unregisterReceiver(dialReceiver);
        dialReceiver = null;

        //取消监听来电显示
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        myPhoneStateListener = null;
    }

    /**
     * 自定义土司
     *
     * @param address
     */
    public void myToast(String address) {
        /*
        textView = new TextView(getApplicationContext());
        textView.setText(address);
        textView.setTextSize(22);
        textView.setTextColor(Color.GREEN);
        */

        view = View.inflate(this, R.layout.toast_phonenum_belong, null);

        //土司的双击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]= SystemClock.uptimeMillis();//系统开机的时间
                if(mHits[0]>=(SystemClock.uptimeMillis()-500)){
                    //双击居中
                    params.x= windowManager.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
                    windowManager.updateViewLayout(view,params);

                    //记录控件移动的坐标位置
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("move_location_x", params.x);
                    editor.putInt("move_location_y", params.y);
                    editor.commit();
                }
            }
        });

        //给view对象设置一个触摸的监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            //定义手指的初始化位置
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //手指按下屏幕
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Log.i(TAG, "开始位置：" + startX + "," + startY);
                        break;
                    //手指在屏幕上移动
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        Log.i(TAG, "新的位置：" + newX + "," + newY);
                        int dx = newX - startX;
                        int dy = newY - startY;
                        Log.i(TAG, "手指的偏移量：" + dx + "," + dy);
                        Log.i(TAG, "更新位置，偏移量：" + dx + "," + dy);
                        params.x += dx;
                        params.y += dy;

                        //考虑到边界问题
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > windowManager.getDefaultDisplay().getWidth() - view.getWidth()) {
                            params.x = (windowManager.getDefaultDisplay().getWidth() - view.getWidth());
                        }
                        if (params.y > windowManager.getDefaultDisplay().getHeight() - view.getHeight()) {
                            params.y = (windowManager.getDefaultDisplay().getHeight() - view.getHeight());
                        }

                        windowManager.updateViewLayout(view, params);

                        //重新初始化手指的开始结束位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    //手指离开屏幕一瞬间
                    case MotionEvent.ACTION_UP:
                        //记录控件移动的坐标位置
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("move_location_x", params.x);
                        editor.putInt("move_location_y", params.y);
                        editor.commit();
                        break;
                }
                return false;
                //事件处理完毕,不要让父控件或父布局响应触摸事件了
                //点击事件：一组动作的组合——按下>停留>抬起
            }
        });

        //读取配置文件中的关于自定义土司主题背景的数据
        int[] themeId = {R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};

        view.setBackgroundResource(themeId[sharedPreferences.getInt("setting_theme", 0)]);
        TextView textView = (TextView) view.findViewById(R.id.tv_phone_num_addr);
        textView.setText(address);

        //窗体的参数就设置好了
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        /* 实现拖动自定义位置的功能：
         * 1.手指按到ImageView上会有一个初始的位置(startX，startY)
         * 2.手指在屏幕上移动，移动到了一个新的位置(newX，newY)
         * 3.计算手指在屏幕上的偏移量，dx = newX - startX……
         *       手指在屏幕上移动了(m，n)
         * 4.立刻更新ImageView在窗体上的位置，移动(m，n)
         * 5.重新初始化手指的开始位置(startX，startY)
         */

        params.gravity = Gravity.TOP + Gravity.LEFT;
        //指定窗体距离左边 100 上100个像素
        params.x = sharedPreferences.getInt("move_location_x", 0);
        params.y = sharedPreferences.getInt("move_location_y", 0);


        params.format = PixelFormat.TRANSLUCENT;

        //Android系统中具有电话优先级的一种窗体类型，记得添加权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        windowManager.addView(view, params);
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        /**
         * 第一个参数是状态，第二个参数是电话号码
         *
         * @param state
         * @param incomingNumber
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                //来电铃声状态，即来电状态
                case TelephonyManager.CALL_STATE_RINGING:

                    String phoneAddress = PhoneNumberQueryDao.queryNumber(incomingNumber);
                    myToast(phoneAddress);
                    Log.i(TAG, "监听来电服务启动了");
                    break;

                //电话空闲状态：挂断电话、来电拒接就会回调此方法
                case TelephonyManager.CALL_STATE_IDLE:
                    //把自定义土司的View移除
                    if (view != null) {
                        windowManager.removeView(view);
                    }

                default:

                    break;

            }
        }
    }

    //在服务里定义广播接收器，监听去电，内部类
    private class DialReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "内部类的广播接收器");

            //得到打出去的电话号码
            String phoneNumber = getResultData();
            String phoneAddress = PhoneNumberQueryDao.queryNumber(phoneNumber);
            //Toast.makeText(context,phoneAddress,Toast.LENGTH_LONG).show();
            myToast(phoneAddress);
        }


    }

}
