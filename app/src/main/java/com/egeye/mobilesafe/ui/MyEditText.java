package com.egeye.mobilesafe.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/5.
 * 自带删除按钮的输入框，无内容时可以自动隐藏按钮
 */
public class MyEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {

    private Drawable mClearDrawable;
    private boolean hasFocus;

    public MyEditText(Context context) {
        this(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /**获取EditText的DrawableRight,假如没有设置我们就使用默认的图片**/
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            /** throw new NullPointerException("You can add drawableRight attribute in XML");**/
            mClearDrawable = getResources().getDrawable(R.drawable.button_clear);
        }
        mClearDrawable.setBounds(
                0, 0,
                mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());
        /**默认设置隐藏图标**/
        setClearIconVisible(false);
        /**设置焦点改变的监听**/
        setOnFocusChangeListener(this);
        /**设置输入框里面内容发生改变的监听**/
        addTextChangedListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    /**
     * @重写方法名:onTextChanged
     * @父类:@see android.widget.TextView#onTextChanged(java.lang.CharSequence, int, int, int)
     * @方法说明:主要监控输入框，当存在内容时候，显示删除的按钮
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFocus) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }


    /**
     * @param visible
     * @方法说明:设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @方法名称:setClearIconVisible
     * @返回值:void
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(
                getCompoundDrawables()[0],
                getCompoundDrawables()[1],
                right,
                getCompoundDrawables()[3]);
    }
}
