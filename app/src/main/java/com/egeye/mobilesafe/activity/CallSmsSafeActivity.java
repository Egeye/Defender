package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.egeye.mobilesafe.R;
import com.egeye.mobilesafe.db.BlackNumberDao;
import com.egeye.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

/**
 * Created by Octavio on 2016/2/7.
 */
public class CallSmsSafeActivity extends Activity {

    private ListView lvDenyNum;
    private List<BlackNumberInfo> infos;
    private BlackNumberDao dao;
    private CallSmsAdapter adapter;

    private LinearLayout llLoding;
    private int offset = 0;
    private int maxnum = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsms_safe);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //初始化操作
        lvDenyNum = (ListView) findViewById(R.id.lv_deny_number);
        llLoding = (LinearLayout) findViewById(R.id.ll_loding);
        dao = new BlackNumberDao(this);

        fillData();

        //给ListView注册一个滚动事件的监听器
        lvDenyNum.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //空闲状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //判断ListView当前滚动的位置
                        int lastPosition = lvDenyNum.getLastVisiblePosition();
                        //System.out.println("最后一个条目的位置"+lastPosition);

                        //集合里面有20个ITem，位置从0开始，最后一个条目的位置是19
                        if (lastPosition == (infos.size() - 1)) {
                            offset += maxnum;
                            fillData();
                        }
                        break;

                    //手指触摸滚动
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;

                    //惯性滑行状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void fillData() {
        llLoding.setVisibility(View.VISIBLE);
        //查询数据耗时，当数据条目多的时候
        //infos = dao.findAll();
        new Thread() {
            @Override
            public void run() {
                //问题：每次加载新的数据，旧的数据就不见了
                //为了让新增加的数据放到末尾，进行判断
                if (infos == null) {
                    infos = dao.findPart(offset, maxnum);
                } else {
                    //原来已经加载过数据了
                    infos.addAll(dao.findPart(offset, maxnum));
                }

                //子线程无法更新主线程UI
                //adapter = new CallSmsAdapter();
                //lvDenyNum.setAdapter(adapter);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llLoding.setVisibility(View.INVISIBLE);

                        //问题：每次加载新的数据，ListView就会跳到最上面
                        //解决：
                        if (adapter == null) {
                            adapter = new CallSmsAdapter();
                            lvDenyNum.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
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


    public void addBlackNum(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View myView = View.inflate(CallSmsSafeActivity.this, R.layout.activity_callsms_dialog, null);
        dialog.setView(myView, 0, 0, 0, 0);
        Button btnOk = (Button) myView.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) myView.findViewById(R.id.btn_cancel);
        final EditText etBlackNum = (EditText) myView.findViewById(R.id.et_set_blacknum);
        final CheckBox cbNum = (CheckBox) myView.findViewById(R.id.cb_mode_num);
        final CheckBox cbText = (CheckBox) myView.findViewById(R.id.cb_mode_text);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNumber = etBlackNum.getText().toString().trim();
                if (TextUtils.isEmpty(blackNumber)) {
                    Toast.makeText(CallSmsSafeActivity.this, "请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode;
                if (cbNum.isChecked() && cbText.isChecked()) {
                    //全部拦截
                    mode = "3";
                } else if (cbNum.isChecked()) {
                    //电话拦截
                    mode = "1";
                } else if (cbText.isChecked()) {
                    //短信拦截
                    mode = "2";
                } else {
                    Toast.makeText(CallSmsSafeActivity.this, "请选择拦截方式", Toast.LENGTH_SHORT).show();
                    return;
                }
                dao.add(blackNumber, mode);

                //更新ListView集合里面的内容
                BlackNumberInfo newInfos = new BlackNumberInfo();
                newInfos.setNumber(blackNumber);
                newInfos.setMode(mode);

                //0添加的位置是最上面，不写则为最下面
                infos.add(0, newInfos);

                //通知 ListView 数据适配器更新数据
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class CallSmsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            //1.减少内存中View对象的创建个数
            if (convertView == null) {
                //把一个布局文件转化成view对象
                view = View.inflate(CallSmsSafeActivity.this, R.layout.activity_callsms_item, null);
                holder = new ViewHolder();
                holder.tvBlackNum = (TextView) view.findViewById(R.id.tv_black_num);
                holder.tvBlackMode = (TextView) view.findViewById(R.id.tv_black_mode);
                holder.ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
                holder.ivEdit = (ImageView) view.findViewById(R.id.iv_edit);
                //当孩子生出来的时候去找到他们的引用，存放在记事本，放到父亲的口袋里
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();//效率提高5%
            }
            //2.减少子孩子查询的次数 - 内存中对象的地址

            holder.tvBlackNum.setText(infos.get(position).getNumber());
            String mode = infos.get(position).getMode();
            if ("1".equals(mode)) {
                holder.tvBlackMode.setText("电话拦截");
            } else if ("2".equals(mode)) {
                holder.tvBlackMode.setText("短信拦截");
            } else {
                holder.tvBlackMode.setText("全部拦截");
            }
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("确定要从黑名单移除这个号码吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.delete(infos.get(position).getNumber());

                            //更新界面
                            infos.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });

            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(CallSmsSafeActivity.this,
                            R.layout.activity_callsms_dialog2, null);
                    dialog.setView(dialogView, 0, 0, 0, 0);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

                    dialog.show();
                }
            });
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

        /**
         * View对象的容器
         * 记录孩子的内存地址 - findviewbyid
         * 相当于一个记事本
         */
        class ViewHolder {
            TextView tvBlackNum;
            TextView tvBlackMode;
            ImageView ivDelete;
            ImageView ivEdit;
        }
    }

}
