package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.utils.MD5Tool;


/**
 * Created by Octavio on 2016/1/31.
 */
public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"
    };
    /*
    private static int[] icons = {
            R.drawable.menu_prevent, R.drawable.menu_comunicate, R.drawable.menu_apps,
            R.drawable.menu_task, R.drawable.menu_flow, R.drawable.menu_trojan,
            R.drawable.menu_cache, R.drawable.menu_tools, R.drawable.menu_settings
    };
    */
    private static int[] icons = {
            R.drawable.green_prevent, R.drawable.green_communicate, R.drawable.green_apps,
            R.drawable.green_tasks, R.drawable.green_flow, R.drawable.green_protect,
            R.drawable.green_cache, R.drawable.green_others, R.drawable.green_settings
    };

    private SharedPreferences sp;

    private GridView gvFun;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        sp = getSharedPreferences("MDConfig",MODE_PRIVATE);

        gvFun = (GridView) findViewById(R.id.gv_fun);

        adapter = new MyAdapter();
        gvFun.setAdapter(adapter);

        gvFun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // parent就是GridView
                switch (position) {
                    case 0://手机防盗
                        showPreventionDialog();
                        break;

                    case 1://通讯卫士
                        Intent intent2 = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
                        startActivity(intent2);
                        break;

                    case 2://软件管理
                        Intent intent3 = new Intent(HomeActivity.this,AppManagerActivity.class);
                        startActivity(intent3);
                        break;

                    case 3://进程管理
                        Intent intent4 = new Intent(HomeActivity.this,TaskManagerActivity.class);
                        startActivity(intent4);
                        break;

                    case 4://流量统计
                        Intent intent5 = new Intent(HomeActivity.this,TrafficManagerActivity.class);
                        startActivity(intent5);
                        break;

                    case 5://手机杀毒
                        Intent intent6 = new Intent(HomeActivity.this,AntiVirusActivity.class);
                        startActivity(intent6);
                        break;

                    case 6://缓存清理
                        Intent intent7 = new Intent(HomeActivity.this,CleanCacheActivity.class);
                        startActivity(intent7);
                        break;

                    case 7://高级工具
                        Intent intent1 = new Intent(HomeActivity.this,ToolActivity.class);
                        startActivity(intent1);
                        break;

                    case 8://设置中心
                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }

            }
        });

    }

    /**
     * 手机防盗
     * 1
     *
     */
    private void showPreventionDialog() {
        //检查是否设置过密码
        if(isSeupPwd()){
            //有设置密码，弹出输入密码对话框
            showEnterPwdDialog();

        }else {
            // 弹出设置密码对话框
            showSetPwdDialog();
        }

    }


    private EditText etSet;
    private Button btnClean;
    private EditText etConfirm;
    private Button btnOk;
    private Button btnCancel;
    private AlertDialog dialog;
    /**
     * 手机防盗
     * 3
     * 设置密码对话框
     */
    private void showSetPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        //自定义一个布局文件，自定义控件带有输入框
        View view = View.inflate(HomeActivity.this,R.layout.activity_home_dialog_setpwd,null);

        etSet = (EditText) view.findViewById(R.id.et_set_pwd);
        etConfirm = (EditText) view.findViewById(R.id.et_confirm_pwd);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnClean = (Button) view.findViewById(R.id.btn_clean);

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
                    Toast.makeText(HomeActivity.this, "请正确输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                //判断是否一致，然后保存
                if (password.equals(confirmPwd)) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("prevent_password", MD5Tool.md5password(password));


                    //提交数据到XML文件中进行保存
                    editor.commit();

                    dialog.dismiss();
                    Log.i(TAG, "一致的话就保存密码，消掉对话框进入手机防盗页面");
                    Intent i = new Intent(HomeActivity.this,PreventActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(getBaseContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

//        builder.setView(view);
//        dialog = builder.show();

        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        //左边的间距0，顶部0，右边0，底部0

        dialog.show();

    }

    /**
     * 手机防盗
     * 4
     * 输入密码的对话框-已经设置过密码了
     * 密码正确就进入手机防盗界面
     */
    private void showEnterPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        //自定义一个布局文件，自定义控件带有输入框
        View view = View.inflate(HomeActivity.this,R.layout.activity_home_dialog_valpwd,null);

        etSet = (EditText) view.findViewById(R.id.et_set_pwd);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk = (Button) view.findViewById(R.id.btn_ok);

        btnClean = (Button) view.findViewById(R.id.btn_clean);

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSet.setText("");
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

                String passwordSP = sp.getString("prevent_password", "");

                //获取密码
                String password = etSet.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {

                    Toast.makeText(getBaseContext(), "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordSP.equals(MD5Tool.md5password(password))) {
                    //判断输入的密码是否与之前保存的密码一致

                    dialog.dismiss();
                    Log.i(TAG, "一致的话就保存密码，消掉对话框进入手机防盗页面");
                    Intent i = new Intent(HomeActivity.this,PreventActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(getBaseContext(), "密码错误!", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

//        builder.setView(view);
//        dialog = builder.show();


        dialog = builder.create();
        dialog.setView(view,0,0,0,0);
        //左边的间距0，顶部0，右边0，底部0

        dialog.show();
    }


    /**
     * 手机防盗
     * 2
     * 判断是否有无密码
     * @return
     */
    private boolean isSeupPwd(){

        String ptPassword = sp.getString("prevent_password",null);

        /*
        if(TextUtils.isEmpty(ptPassword)){
            return false;
        } else{
            return true;
        }
        */

        return !TextUtils.isEmpty(ptPassword);

    }



    /**
     * 自定义数据填充适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 需要多少个，可以直接写 9 个，不推荐
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //返回某一条对应的视图

            View view = View.inflate(HomeActivity.this, R.layout.activity_home_item, null);
            ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_item);

            tvItem.setText(names[position]);
            ivItem.setImageResource(icons[position]);

            return view;
        }
    }


    private long mExitTime;
    /**
     * by octavio 防止误触返回键，连按两次返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
