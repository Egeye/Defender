package com.egeye.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.egeye.mobilesafe.service.UpdateWidgetService;

/**
 * Created by Octavio on 2016/2/12.
 */
public class MyWidgetReceiver extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Intent i = new Intent(context, UpdateWidgetService.class);
        context.startService(i);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);


        Intent i = new Intent(context, UpdateWidgetService.class);
        context.startService(i);
    }


    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        System.out.println("onDisabled");

        Intent i = new Intent(context, UpdateWidgetService.class);
        context.stopService(i);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Intent i = new Intent(context, UpdateWidgetService.class);
        context.startService(i);
    }
}
