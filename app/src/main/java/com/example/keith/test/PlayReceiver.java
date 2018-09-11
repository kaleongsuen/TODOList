package com.example.keith.test;

import android.app.Notification;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.NotificationManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.graphics.BitmapFactory;
import android.app.NotificationChannel;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Keith on 6/11/2017.
 */

public class PlayReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bData = intent.getExtras();
        //if(bData.get("msg").equals("play_hskay")){
        String taskName = bData.get("task_name").toString();
        //String taskName = "testing Task";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int notification_id = (int)calendar.getTimeInMillis();

        String id ="finna_channel";//channel id

        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Intent intent1 = new Intent();
        intent1.setClass(context,MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,notification_id,intent1,0);
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_round);

        NotificationManager notificationManager2 =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,id)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Keith is reminding you :p")
                    .setContentText("Task <"+taskName+"> is deadline today!")
                    .setLargeIcon(bitmap)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE);
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createWaveform(new long[]{10,250,250,250,250},-1));

        notificationManager2.notify(notification_id, notification.build());

        //}
        //Toast.makeText(context,"times up!!!!!",Toast.LENGTH_LONG).show();
    }
}
