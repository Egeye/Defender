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
 * 自定义的组合控件，包含两个TextView和一个CheckBox、一个View
 */
public class SettingsItemView extends RelativeLayout {

    private CheckBox cbUpdateStatus;
    private TextView tvUpdate;
    private TextView tvUpdateStatus;
    private String status_on;
    private String status_off;


    public SettingsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 带有两个参数的构造方法，布局文件使用的时候调用
     *
     * @param context
     * @param attrs
     */
    public SettingsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        //attrs.getAttributeValue(0);
        //通过此方法可以得到属性-（数组的兴衰）
        // android:id="@+id/si_update"
        // android:layout_width="wrap_content"
        //  android:layout_height="wrap_content"
        //  egeye:status_off="自动更新已经开启"
        //  egeye:status_on="自动更新已经关闭"
        //   egeye:update="自动检测更新">

        String status_title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","status_title");
        status_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","status_off");
        status_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","status_on");

        tvUpdate.setText(status_title);

        setStatus(status_off);

    }

    public SettingsItemView(Context context) {
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
        View.inflate(context, R.layout.activity_settings_item, this);
        // SettingsItem.this

        cbUpdateStatus = (CheckBox) this.findViewById(R.id.cb_setting_update);
        tvUpdate = (TextView) this.findViewById(R.id.tv_setting_update);
        tvUpdateStatus = (TextView) this.findViewById(R.id.tv_setting_update_status);

    }

    /**
     * 检验组合控件是否选中
     */
    public boolean isCheck() {

        return cbUpdateStatus.isChecked();
    }

    /**
     * 设置组合控件的选中状态
     */
    public void setChecked(boolean checked) {

        if(checked){
            setStatus(status_on);
        } else{
            setStatus(status_off);
        }

        cbUpdateStatus.setChecked(checked);

    }

    /**
     * 设置组合控件的状态描述信息
     */
    public void setStatus(String text) {

        tvUpdateStatus.setText(text);
    }

}
