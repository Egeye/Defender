package com.egeye.doubleclick;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Octavio on 2016/2/7.
 */
public class FifthClick extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fifth_click);

    }

    //src 拷贝的源数组
    //srcPos 从源数组的那个位置开始拷贝.
    //dst 目标数组
    //dstPos 从目标数组的那个位子开始写数据
    //length 拷贝的元素的个数
    long [] mHits = new long[3];
    public void fifthClick(View view) {
        System.arraycopy(mHits,1,mHits,0,mHits.length-1);
        mHits[mHits.length-1]= SystemClock.uptimeMillis();//系统开机的时间
        if(mHits[0]>=(SystemClock.uptimeMillis()-500)){
            Toast.makeText(this,"3次点击事件受理",Toast.LENGTH_SHORT).show();
        }
    }
}
