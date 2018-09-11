package com.example.keith.test;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class todoWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todo_widget);
       // views.setTextViewText(R.id.appwidget_text, widgetText);

        MyDBHelper helper;
        ArrayList<Model> model;
        CustomAdapter adapter;
        helper = MyDBHelper.getInstance(context);
        model = helper.listTodoTask(helper.getReadableDatabase());
        adapter = new CustomAdapter(context, model);
        views.setRemoteAdapter(R.id.widget_list, new Intent(context, todoWidget.class));
//        listview.setAdapter(adapter);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.todo_widget);

        ComponentName componentName = new ComponentName(context,todoWidget.class);
        appWidgetManager.updateAppWidget(componentName,remoteViews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

