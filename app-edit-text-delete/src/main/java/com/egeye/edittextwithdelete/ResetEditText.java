package com.egeye.edittextwithdelete;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by Octavio on 2016/2/5.
 */
public class ResetEditText extends EditText {

    private Drawable mClearDrawable;
    private Rect mBounds;

    public ResetEditText(Context context) {
        super(context);
        initEditText();
    }

    public ResetEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText();
    }

    public ResetEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditText();
    }

    /**
     * 初始化操作
     */
    public void initEditText() {
        setEditTextDrawable();
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ResetEditText.this.setEditTextDrawable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     *
     */
    private void setEditTextDrawable() {
        if (getText().toString().length() == 0) {
            setCompoundDrawables(null, null, null, null);
        } else {
            setCompoundDrawables(null, null, mClearDrawable, null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mClearDrawable = null;
        mBounds = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((mClearDrawable != null) && (event.getAction() == MotionEvent.ACTION_UP)) {
            mBounds = mClearDrawable.getBounds();
            int i = (int) event.getRawX();
            if (i > getRight() - 3 * mBounds.width()) {
                setText("");
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null) {
            mClearDrawable = right;
            super.setCompoundDrawables(left, top, right, bottom);
        }
    }
}
