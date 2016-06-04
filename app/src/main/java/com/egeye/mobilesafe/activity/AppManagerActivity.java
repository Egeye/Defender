package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.db.ApplockDao;
import com.egeye.mobilesafe.domain.AppInfo;
import com.egeye.mobilesafe.engine.AppInfoProvider;
import com.egeye.mobilesafe.utils.DensityTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavio on 2016/2/9.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AppManagerActivity";
    private TextView tvAvaliRom;
    private TextView tvAvaliSD;
    private ListView lvApps;
    private LinearLayout llLoding;
    private AppManagerAdapter adapter;

    //所有应用程序包信息
    private List<AppInfo> appInfos;

    // 用户应用程序的信息
    private List<AppInfo> userAppInfos;

    // 系统应用程序的信息
    private List<AppInfo> sysAppInfos;

    //当前程序统计信息
    private TextView tvAppCount;

    private PopupWindow popupWindow;

    //popwindow 上的控件 卸载启动分享
    private LinearLayout llUninstall;
    private LinearLayout llLaunch;
    private LinearLayout llShare;

    //被点击的条目
    private AppInfo appInfo;

    private ApplockDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        dao = new ApplockDao(this);

        tvAvaliRom = (TextView) findViewById(R.id.tv_avali_rom);
        tvAvaliSD = (TextView) findViewById(R.id.tv_avali_sd);
        llLoding = (LinearLayout) findViewById(R.id.ll_app_loding);
        lvApps = (ListView) findViewById(R.id.lv_apps);
        tvAppCount = (TextView) findViewById(R.id.tv_app_count);

        long sdSize = getAvaliSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romSize = getAvaliSpace(Environment.getDataDirectory().getAbsolutePath());
        tvAvaliSD.setText("SD卡可用空间："
                + android.text.format.Formatter.formatFileSize(this, sdSize));

        tvAvaliRom.setText("内存可用空间：" + Formatter.formatFileSize(this, romSize));

        fillData();

        //给Listview注册一个滚动的监听器
        lvApps.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 滚动的时候调用的方法
             * @param view
             * @param firstVisibleItem 第一个可见条目在Listview集合里面的位置
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userAppInfos != null && sysAppInfos != null) {
                    dismissPopWindow();
                    if (firstVisibleItem > userAppInfos.size()) {
                        tvAppCount.setText("系统程序：" + sysAppInfos.size() + "个");
                    } else {
                        tvAppCount.setText("用户程序：" + userAppInfos.size() + "个");
                    }
                }
            }
        });

        //设置Listview的点击事件
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return;
                } else if (position == userAppInfos.size() + 1) {
                    return;
                } else if (position <= userAppInfos.size()) {
                    //用户程序
                    int newposition = position - 1;
                    appInfo = userAppInfos.get(newposition);
                } else {
                    int newposition = position - 1 - userAppInfos.size() - 1;
                    appInfo = sysAppInfos.get(newposition);
                }
                //System.out.println(appInfo.getPackageName());
                dismissPopWindow();


                /*
                TextView contentView = new TextView(getApplicationContext());
                contentView.setText(appInfo.getPackageName());
                contentView.setTextColor(Color.WHITE);
                contentView.setBackgroundColor(0xff82B651);
                */
                View contentView = View.inflate(
                        AppManagerActivity.this,
                        R.layout.activity_app_popup_item,
                        null);

                //控件初始化
                llLaunch = (LinearLayout) contentView.findViewById(R.id.ll_app_launch);
                llShare = (LinearLayout) contentView.findViewById(R.id.ll_app_share);
                //llUninstall= (LinearLayout) contentView.findViewById(R.id.iv_app_uninstall);
                llUninstall = (LinearLayout) contentView.findViewById(R.id.ll_app_uninstall);

                llLaunch.setOnClickListener(AppManagerActivity.this);
                llShare.setOnClickListener(AppManagerActivity.this);
                llUninstall.setOnClickListener(AppManagerActivity.this);

                //PopupWindow popupWindow = new PopupWindow(contentView, 200, 100);
                popupWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, -2);

                //动画效果的播放必须要求窗体有背景颜色
                //透明颜色也是颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int[] location = new int[2];
                view.getLocationInWindow(location);

                int dip = 60;
                int px = DensityTool.dip2px(getApplicationContext(), dip);
                System.out.println("px:" + px);

                popupWindow.showAtLocation(parent,
                        Gravity.LEFT | Gravity.TOP,
                        px, location[1]);
                //在代码里面设置的宽高值都是像素,60像素

                //创建动画资源
                //0.3f水平方向缩放比例
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(300);

                //0.5f,半透明，1.0f完全不透明
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(300);

                //false 用各自独立的插入器进行播放
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);

                contentView.startAnimation(set);
            }
        });

        //长点击事件监听器，程序锁
        //return false 代表事件到我这里还没处理完，别的控件可以继续处理这个事件
        lvApps.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return true;
                } else if (position == userAppInfos.size() + 1) {
                    return true;
                } else if (position <= userAppInfos.size()) {
                    //用户程序
                    int newposition = position - 1;
                    appInfo = userAppInfos.get(newposition);
                } else {
                    int newposition = position - 1 - userAppInfos.size() - 1;
                    appInfo = sysAppInfos.get(newposition);
                }

                ViewHolder holder = (ViewHolder) view.getTag();

                //判断条目是否存在程序锁数据库里面
                if (dao.find(appInfo.getPackageName())) {
                    //被锁定的程序,要解除锁定，更新界面为打开锁的图片
                    dao.delete(appInfo.getPackageName());
                    holder.ivAppStatus.setImageResource(R.drawable.status_unlock);

                } else {
                    //锁定程序，更新界面为关闭的锁
                    dao.add(appInfo.getPackageName());
                    holder.ivAppStatus.setImageResource(R.drawable.status_lock);
                }

                return true;
            }
        });
    }

    private void fillData() {
        llLoding.setVisibility(View.VISIBLE);
        //为了防止加载的时候造成主线程阻塞，应该写到子线程里面
        //appInfos = AppInfoProvider.getAppInfos(this);
        new Thread() {
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);

                userAppInfos = new ArrayList<AppInfo>();
                sysAppInfos = new ArrayList<AppInfo>();
                for (AppInfo info : appInfos) {
                    if (info.isUserApp()) {
                        userAppInfos.add(info);
                    } else {
                        sysAppInfos.add(info);
                    }
                }

                //加载ListView的数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new AppManagerAdapter();
                            lvApps.setAdapter(new AppManagerAdapter());
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        llLoding.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    private void dismissPopWindow() {
        //把旧的弹出窗体关闭掉
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissPopWindow();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 获取某个目录的可用空间
     *
     * @param path
     * @return
     */
    private long getAvaliSpace(String path) {
        StatFs statFs = new StatFs(path);

        //获取分区的个数
        statFs.getBlockCount();

        //获取分区的大小
        long size = statFs.getBlockSize();

        //获取可用的区块的个数
        long count = statFs.getAvailableBlocks();

        return size * count;
    }

    /**
     * popwindow条目对应的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_app_launch:
                Log.i(TAG, "启动：" + appInfo.getName());
                startApplication();
                dismissPopWindow();
                break;

            case R.id.ll_app_share:
                Log.i(TAG, "分享：" + appInfo.getName());
                shareApplication();
                dismissPopWindow();
                break;

            case R.id.ll_app_uninstall:
                if (appInfo.isUserApp()) {
                    Log.i(TAG, "卸载：" + appInfo.getName());
                    uninstallApplication();
                } else {
                    Toast.makeText(this, "系统应用，执行卸载需要获取root权限", Toast.LENGTH_SHORT).show();
                }
                dismissPopWindow();

                break;
        }
    }

    private void shareApplication() {
        Intent i = new Intent();
        i.setAction("android.intent.action.SEND");

        //最好加一下这条
        i.addCategory(Intent.CATEGORY_DEFAULT);

        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "推荐使用一款软件,名称叫：" + appInfo.getName());
        startActivity(i);
    }

    /**
     * 卸载应用程序
     */
    private void uninstallApplication() {
        // <action android:name="android.intent.action.VIEW" />
        // <action android:name="android.intent.action.DELETE" />
        // <category android:name="android.intent.category.DEFAULT" />
        // <data android:scheme="package" />
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
        startActivityForResult(intent, 0);
    }

    /**
     * 刷新卸载后的界面。
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 启动应用程序
     */
    private void startApplication() {
        //查询这个应用程序的入口Activity，把他开启起来
        PackageManager pm = getPackageManager();

        /*
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        //将手机上所有的具有启动能力的Activity都查询出来
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        */

        Intent i = pm.getLaunchIntentForPackage(appInfo.getPackageName());
        if (i != null) {
            startActivity(i);
        } else {
            Toast.makeText(this, "无法启动当前应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 减少布局文件的id查找次数
     */
    static class ViewHolder {
        TextView tvAppName;
        TextView tvAppLocat;
        ImageView ivAppIcon;
        ImageView ivAppStatus;
    }

    /**
     * 数据适配器
     */
    private class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //控制ListView的条目个数
            //return appInfos.size();
            return userAppInfos.size() + 1 + sysAppInfos.size() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo appinfo;

            //TextView textView = new TextView(AppManagerActivity.this);
            //textView.setText(appInfos.get(position).toString());

            //考虑特殊的Item
            if (position == 0) {
                //显示的是用户程序的统计
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xff82B651);
                tv.setText("用户程序：" + userAppInfos.size() + "个");
                return tv;
            } else if (position == userAppInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xff82B651);
                tv.setText("系统程序：" + sysAppInfos.size() + "个");
                return tv;
            } else if (position <= userAppInfos.size()) {
                //多了一个textview占用
                int newposition = position - 1;
                appinfo = userAppInfos.get(newposition);
            } else {
                //系统程序
                int newposition = position - userAppInfos.size() - 1 - 1;
                appinfo = sysAppInfos.get(newposition);
            }

            View view;
            ViewHolder holder;


            /*
            if (position < userAppInfos.size()) {
                //这些位置是留给用户程序显示的
                appinfo = userAppInfos.get(position);
            } else {
                //这些位置是留给系统程序
                int newposition = position - userAppInfos.size();
                appinfo = sysAppInfos.get(newposition);
            }
            */

            //不仅需要检查是否为空，还需要判断是否为合适的类型去复用
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.activity_app_item, null);
                holder = new ViewHolder();
                holder.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tvAppLocat = (TextView) view.findViewById(R.id.tv_app_locate);
                holder.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
                holder.ivAppStatus = (ImageView) view.findViewById(R.id.iv_app_status);

                view.setTag(holder);
            }

            holder.ivAppIcon.setImageDrawable(appinfo.getIcon());
            holder.tvAppName.setText(appinfo.getName());

            if (appinfo.isInRom()) {
                holder.tvAppLocat.setText("手机内存" + " uid:" + appinfo.getUid());
            } else {
                holder.tvAppLocat.setText("外部存储" + " uid:" + appinfo.getUid());
            }


            if (dao.find(appinfo.getPackageName())) {
                holder.ivAppStatus.setImageResource(R.drawable.status_lock);
            } else {
                holder.ivAppStatus.setImageResource(R.drawable.status_unlock);
            }

            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

}
