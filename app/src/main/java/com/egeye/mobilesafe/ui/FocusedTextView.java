package com.egeye.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Octavio on 2016/2/1.
 * 自定义一个TextView ，像按钮一样带有焦点属性
 */
public class FocusedTextView extends TextView {

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FocusedTextView(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public FocusedTextView(Context context){
        super(context);
    }

    /**
     * 当前并没有焦点，只是欺骗Android系统，以获得焦点方式去处理
     * @return true
     */
    @Override
    public boolean isFocused() {
        //return super.isFocused();
        return true;
    }
}
