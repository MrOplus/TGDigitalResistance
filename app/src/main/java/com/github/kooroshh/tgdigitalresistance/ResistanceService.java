package com.github.kooroshh.tgdigitalresistance;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.util.List;

/**
 * Created by Oplus on 2018/05/03.
 */

public class ResistanceService extends Service {
    private static final int NOTIFICATION_ID = 0x100000;
    public static List<ResistanceServer> servers;
    public static boolean isConnected = false;
    private String ChannelID = "" ;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChannelID = createNotificationChannel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ResistanceLowAPI.Kill(getApplicationContext());
            String server = intent.getStringExtra("SERVER");
            String encryption = intent.getStringExtra("ENCRYPTION");
            String password = intent.getStringExtra("PASSWORD");
            String port = intent.getStringExtra("PORT");
            String localPort = "5080";
            ResistanceLowAPI.Run(getApplicationContext(),server,password,encryption,port,localPort);

            isConnected = true;
            showNotification();
        }catch (Exception E){
            E.printStackTrace();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void showNotification(){
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this,ChannelID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is Running")
                .setSmallIcon(R.drawable.ic_notif)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "com.github.kooroshh.digitalresistance.ResistanceService" ;
        String channelName = "Default Channel";
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager service =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert service != null;
        service.createNotificationChannel(chan);
        return channelId;
    }
    @Override
    public void onDestroy() {
        try {
            ResistanceLowAPI.Kill(getApplicationContext());
            stopForeground(true);
            stopSelf();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            isConnected = false;
            super.onDestroy();
        }
    }
}
