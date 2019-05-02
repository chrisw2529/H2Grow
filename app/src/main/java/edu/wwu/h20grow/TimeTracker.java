package edu.wwu.h20grow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;

public class TimeTracker extends Service {
    private NotificationManager mNM;
    private int NOTIFICATION = 0;
    private DatabaseManager dbManager;
    private ArrayList<Plant> plants;

    @Override
    public void onCreate(){
        mNM = (NotificationManager)getSystemService(this.NOTIFICATION_SERVICE);
        dbManager = new DatabaseManager(this);
        plants = dbManager.selectAll();
        Date currDate = Calendar.getInstance().getTime();
        long minNextWaterTime = 999999999;
        String closestPlantName = "Error";
        for(Plant p : plants){
            long pDate = p.getLastWaterTime();
            long pInterval = TimeUnit.HOURS.toMillis(p.getWaterInterval());
            long timeUntilWater = pInterval - (currDate.getTime() - pDate);
            //need to figure out how to handle negative values
            if(timeUntilWater > 0){
                if(timeUntilWater < minNextWaterTime){
                    minNextWaterTime = timeUntilWater;
                    closestPlantName = p.getPlantName();
                }
            }
        }

        if(MainActivity.user_settings != null){
            startTimer(minNextWaterTime, MainActivity.user_settings.ispNotify());
        }else{
            startTimer(minNextWaterTime, false);
        }

    }

    //can be called multiple times, will handle updating once a plant is added/watered
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;//<-TEST FUNCTIONALITY TO DETERMINE PROPER SETTING
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private void startTimer(long delay, boolean pNotify){
        if(!pNotify){
            return;
        }

        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                String notification = "You have plants that need to be watered!";
                showNotification(notification);
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, delay);
    }

    private void showNotification(String text){

        if(Build.VERSION.SDK_INT < 26){
            return;
        }

        NotificationChannel channel;
        channel = new NotificationChannel("default", "Channel", NotificationManager.IMPORTANCE_DEFAULT);

        channel.setDescription("Channel description");
        mNM.createNotificationChannel(channel);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default")
                .setContentTitle(text)
                .setContentText("Click to open H2Grow")
                .setSmallIcon(R.drawable.baseline_opacity_white_18dp)
                .setVibrate(null);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        mNM.notify(NOTIFICATION, builder.build());
    }
}