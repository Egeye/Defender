package com.egeye.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/1.
 * 自定义的组合控件，包含两个TextView和一个ImageView、一个View
 */
public class SettingsClickView extends RelativeLayout {
    private TextView tvUpdate;
    private TextView tvUpdateStatus;
    private String status_on;
    private String status_off;


    public SettingsClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 带有两个参数的构造方法，布局文件使用的时候调用
     *
     * @param context
     * @param attrs
     */
    public SettingsClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String status_title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "status_title");
        status_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "status_off");
        status_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "status_on");
        tvUpdate.setText(status_title);
        setStatus(status_off);
    }

    public SettingsClickView(Context context) {
        super(context);
        initView(context);
    }


    /**
     * 初始化布局文件
     *
     * @param context
     */
    private void initView(Context context) {
        // 把一个布局文件——>View，并且加载到SettingsItem里面
        View.inflate(context, R.layout.activity_settings_item2, this);
        tvUpdate = (TextView) this.findViewById(R.id.tv_setting_update);
        tvUpdateStatus = (TextView) this.findViewById(R.id.tv_setting_update_status);
    }

    /**
     * 设置组合控件的选中状态
     */
    public void setChecked(boolean checked) {
        if (checked) {
            setStatus(status_on);
        } else {
            setStatus(status_off);
        }
    }

    /**
     * 设置组合控件的状态描述信息
     */
    public void setStatus(String text) {
        tvUpdateStatus.setText(text);
    }

}
