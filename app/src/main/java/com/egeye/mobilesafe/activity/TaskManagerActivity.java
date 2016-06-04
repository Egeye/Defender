package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.domain.TaskInfo;
import com.egeye.mobilesafe.engine.TaskInfoProvider;
import com.egeye.mobilesafe.utils.SystemInfoTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavio on 2016/2/11.
 */
public class TaskManagerActivity extends Activity {
    private SharedPreferences sp;

    //进程总计
    private TextView tvProcessCount;

    //内存信息
    private TextView tvMemInfo;

    //进程列表
    private ListView lvTask;

    //加载提示
    private LinearLayout llLoding;

    private TextView tvTaskFlag;

    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private TaskManagerAdapter adapter;

    //杀死进程
    private int processCount;
    private long availMem;
    private long totalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        llLoding = (LinearLayout) findViewById(R.id.ll_task_loding);
        tvProcessCount = (TextView) findViewById(R.id.tv_process_count);
        tvMemInfo = (TextView) findViewById(R.id.tv_mem_info);
        lvTask = (ListView) findViewById(R.id.lv_tasks);
        tvTaskFlag = (TextView) findViewById(R.id.tv_task_flag);

        fillData();
        lvTask.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && systemTaskInfos != null) {
                    if (firstVisibleItem > userTaskInfos.size()) {
                        tvTaskFlag.setText("系统进程：" + systemTaskInfos.size() + "个");
                    } else {
                        tvTaskFlag.setText("用户进程：" + userTaskInfos.size() + "个");
                    }
                }
            }
        });


        lvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;

                if (position == 0) {
                    //用户进程标签
                    return;
                } else if (position == userTaskInfos.size() + 1) {
                    //系统进程标签
                    return;
                } else if (position <= userTaskInfos.size()) {
                    taskInfo = userTaskInfos.get(position - 1);
                } else {
                    taskInfo = systemTaskInfos.get(position - 1 - 1 - userTaskInfos.size());
                }

                //判断是否为自己的进程
                if (getPackageName().equals(taskInfo.getPackname())) {
                    return;
                }

                System.out.println("------------------" + taskInfo.toString());
                ViewHolder holder = (ViewHolder) view.getTag();
                if (taskInfo.isChecked()) {
                    taskInfo.setChecked(false);
                    holder.cbChoose.setChecked(false);
                } else {
                    taskInfo.setChecked(true);
                    holder.cbChoose.setChecked(true);
                }
            }
        });
    }

    private void setTaskTitle() {
        //进程的数量
        processCount = SystemInfoTool.getRunningProcessCount(this);
        tvProcessCount.setText("运行中的进程:" + processCount + "个");

        availMem = SystemInfoTool.getAvailMem(this);
        totalMem = SystemInfoTool.getTotalMem(this);
        tvMemInfo.setText("剩余/总内存：" +
                Formatter.formatFileSize(this, availMem) + "/" +
                Formatter.formatFileSize(this, totalMem));
    }

    /**
     * 填充Listview的数据
     */
    private void fillData() {

        llLoding.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                allTaskInfos = TaskInfoProvider
                        .getTaskInfos(getApplicationContext());

                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();

                for (TaskInfo info : allTaskInfos) {
                    if (info.isUserTask()) {
                        userTaskInfos.add(info);
                    } else {
                        systemTaskInfos.add(info);
                    }
                }

                // 更新设置界面。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llLoding.setVisibility(View.INVISIBLE);
                        if (adapter == null) {
                            adapter = new TaskManagerAdapter();
                            lvTask.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setTaskTitle();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 全选
     *
     * @param view
     */
    public void TaskSelectAll(View view) {
        for (TaskInfo info : allTaskInfos) {
            if (getPackageName().equals(info.getPackname())) {
                continue;
            }
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void TaskSelectRever(View view) {
        for (TaskInfo info : allTaskInfos) {
            if (getPackageName().equals(info.getPackname())) {
                continue;
            }
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理
     *
     * @param view
     */
    public void TaskClean(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        int count = 0;
        long savedMem = 0;

        // 记录那些被杀死的条目
        List<TaskInfo> killedTaskinfos = new ArrayList<TaskInfo>();

        for (TaskInfo info : allTaskInfos) {
            if (info.isChecked()) {// 被勾选的，杀死这个进程。
                am.killBackgroundProcesses(info.getPackname());
                if (info.isUserTask()) {
                    userTaskInfos.remove(info);
                } else {
                    systemTaskInfos.remove(info);
                }
                killedTaskinfos.add(info);
                count++;
                savedMem += info.getMemsize();
            }
        }

        allTaskInfos.removeAll(killedTaskinfos);

        adapter.notifyDataSetChanged();
        Toast.makeText(
                this,
                "杀死了" + count + "个进程，释放了"
                        + Formatter.formatFileSize(this, savedMem)
                        + "的内存",
                Toast.LENGTH_SHORT).show();
        processCount -= count;
        availMem += savedMem;
        tvProcessCount.setText("运行中的进程：" + processCount + "个");
        tvMemInfo.setText("剩余/总内存："
                + Formatter.formatFileSize(this, availMem) + "/"
                + Formatter.formatFileSize(this, totalMem));
    }

    /**
     * 设置
     *
     * @param view
     */
    public void TaskSet(View view) {
        Intent i = new Intent(this, TaskSettingActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvSize;
        CheckBox cbChoose;
    }

    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            sp = getSharedPreferences("MDConfig", MODE_PRIVATE);

            if (sp.getBoolean("task_show_sys", false)) {
                return allTaskInfos.size() + 2;
            } else {
                return userTaskInfos.size() + 1;
            }
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
            TaskInfo taskInfo;

            if (position == 0) {
                //用户进程标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xff82B651);
                tv.setText("用户进程：" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == userTaskInfos.size() + 1) {
                //系统进程标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xff82B651);
                tv.setText("系统进程：" + systemTaskInfos.size() + "个");
                return tv;
            } else if (position <= userTaskInfos.size()) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - 1 - 1 - userTaskInfos.size());
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(TaskManagerActivity.this, R.layout.activity_task_item, null);
                holder = new ViewHolder();

                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tvSize = (TextView) view.findViewById(R.id.tv_task_size);
                holder.cbChoose = (CheckBox) view.findViewById(R.id.cb_task_chose);

                view.setTag(holder);
            }

            holder.ivIcon.setImageDrawable(taskInfo.getIcon());
            holder.tvName.setText(taskInfo.getName());
            holder.tvSize.setText("内存占用：" +
                    Formatter.formatFileSize(
                            TaskManagerActivity.this,
                            taskInfo.getMemsize()));
            holder.cbChoose.setChecked(taskInfo.isChecked());

            if (getPackageName().equals(taskInfo.getPackname())) {
                holder.cbChoose.setVisibility(View.INVISIBLE);
            } else {
                holder.cbChoose.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }
}
