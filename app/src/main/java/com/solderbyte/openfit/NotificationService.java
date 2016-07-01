package com.solderbyte.openfit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.solderbyte.openfit.util.OpenFitIntent;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationService extends NotificationListenerService {
    private static final String LOG_TAG = "OpenFit:NotificationService";

    private ArrayList<String> ListPackageNames = new ArrayList<String>();
    private Context context;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Created NotificationService");
        this.registerReceiver(serviceStopReceiver, new IntentFilter(OpenFitIntent.INTENT_SERVICE_STOP));
        this.registerReceiver(applicationsReceiver, new IntentFilter(OpenFitIntent.INTENT_SERVICE_NOTIFICATION_APPLICATIONS));
        context = getApplicationContext();

        Intent msg = new Intent(OpenFitIntent.INTENT_SERVICE_NOTIFICATION);
        context.sendBroadcast(msg);
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(LOG_TAG, "onNotificationPosted");
        String packageName = sbn.getPackageName();

        if(!ListPackageNames.contains(packageName)) {
            return;
        }

        // API v19
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        //String category = notification.category; API v21
        if((notification.flags & Notification.FLAG_ONGOING_EVENT) != 0) {
            Log.d(LOG_TAG, "filtered by flags");
            return;
        }

        String ticker = null;
        String message = null;
        String submessage = null;
        String summary = null;
        String info = null;
        String title = null;
        try {
            ticker = (String) sbn.getNotification().tickerText;
        }
        catch(Exception e) {
            Log.d(LOG_TAG, "Notification does't have tickerText");
        }
        String tag = sbn.getTag();
        long time = sbn.getPostTime();
        //int id = sbn.getId();
        int id = new Random().nextInt();

        if(extras.getCharSequence("android.title") != null) {
            title = extras.getString("android.title");
        }
        if(extras.getCharSequence("android.text") != null) {
            message = extras.getCharSequence("android.text").toString();
        }
        if(extras.getCharSequence("android.subText") != null) {
            submessage = extras.getCharSequence("android.subText").toString();
        }
        if(extras.getCharSequence("android.summaryText") != null) {
            summary = extras.getCharSequence("android.summaryText").toString();
        }
        if(extras.getCharSequence("android.infoText") != null) {
            info = extras.getCharSequence("android.infoText").toString();
        }
        if(extras.getCharSequence("android.infoText") != null) {
            info = extras.getCharSequence("android.infoText").toString();
        }

        Log.d(LOG_TAG, "Captured notification message: " + message + " from source:" + packageName);
        Log.d(LOG_TAG, "ticker: " + ticker);
        Log.d(LOG_TAG, "title: " + title);
        Log.d(LOG_TAG, "message: " + message);
        Log.d(LOG_TAG, "tag: " + tag);
        Log.d(LOG_TAG, "time: " + time);
        Log.d(LOG_TAG, "id: " + id);
        Log.d(LOG_TAG, "submessage: " + submessage);
        Log.d(LOG_TAG, "summary: " + summary);
        Log.d(LOG_TAG, "info: " + info);
        //Log.d(LOG_TAG, "category: " + category);

        Intent msg = new Intent(OpenFitIntent.INTENT_NOTIFICATION);
        msg.putExtra("packageName", packageName);
        msg.putExtra("ticker", ticker);
        msg.putExtra("title", title);
        msg.putExtra("message", message);
        msg.putExtra("time", time);
        msg.putExtra("id", id);
        if(submessage != null) {
            msg.putExtra("submessage", submessage);
        }

        context.sendBroadcast(msg);
        Log.d(LOG_TAG, "Sending notification message: " + message + " from source:" + packageName);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(LOG_TAG, "onNotificationRemoved");
        String packageName = sbn.getPackageName();
        String shortMsg = "";
        try {
            shortMsg = (String) sbn.getNotification().tickerText;
        }
        catch(Exception e) {
            
        }
        Log.d(LOG_TAG, "Removed notification message: " + shortMsg + " from source:" + packageName);
    }

    public void setPackageNames(ArrayList<String> packageNames) {
        ListPackageNames = packageNames;
    }

    private BroadcastReceiver serviceStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Stopping Service");
            unregisterReceiver(applicationsReceiver);
            unregisterReceiver(serviceStopReceiver);
            stopSelf();
        }
    };

    private BroadcastReceiver applicationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> applications = intent.getStringArrayListExtra(OpenFitIntent.INTENT_EXTRA_DATA);
            setPackageNames(applications);
            Log.d(LOG_TAG, "Recieved listeningApps: " + applications.size());
        }
    };
}
