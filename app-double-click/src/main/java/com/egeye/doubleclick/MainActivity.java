package com.egeye.doubleclick;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    long firstClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void newA(View view){
        Intent i = new Intent(this,FifthClick.class);
        startActivity(i);
    }

    public void doubleClick(View view){
        if(firstClickTime>0){
            long secondClickTime = SystemClock.uptimeMillis();
            long dTime = secondClickTime - firstClickTime;
            if(dTime<500){
                Toast.makeText(this, "双击了", Toast.LENGTH_SHORT).show();

            }else {
                firstClickTime = 0;
            }
            return;
        }
        firstClickTime = SystemClock.uptimeMillis();
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                firstClickTime = 0;
            }
        }.start();
    }


}
