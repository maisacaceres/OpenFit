package com.solderbyte.openfit;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Set;

public class Alarm {
    private static final String LOG_TAG = "OpenFit.Alarm";

    private final static Uri mAlarmDBUri = Uri.parse("content://com.samsung.sec.android.clockpackage/alarm");
    private static String alarmManufacturer = "Google";

    public static IntentFilter getIntentFilter() {
        IntentFilter alarm = new IntentFilter();
        alarm.addAction("com.android.deskclock.ALARM_ALERT");
        alarm.addAction("com.android.deskclock.ALARM_SNOOZE");
        alarm.addAction("com.android.deskclock.ALARM_DISMISS");
        alarm.addAction("com.android.deskclock.ALARM_DONE");
        alarm.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_STARTED_IN_ALERT");
        alarm.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_STOP");
        alarm.addAction("com.samsung.sec.android.clockpackage.alarm.ALARM_STOPPED_IN_ALERT");
        return alarm;
    }

    public static String getAction(Intent intent, Context context, int alarmId) {
        String action = intent.getAction();
        boolean isSnooze = false;

        if (action.equals("com.samsung.sec.android.clockpackage.alarm.ALARM_STARTED_IN_ALERT")) {
            alarmManufacturer = "Samsung";

            Cursor cursor = context.getContentResolver(). query(mAlarmDBUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getInt(0) == alarmId) {
                            isSnooze = (cursor.getInt(1) == 2);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            if (isSnooze) {
                action = "STARTSNOOZE";
            } else {
                action = "START";
            }
        }
        else if (action.equals("com.samsung.sec.android.clockpackage.alarm.ALARM_STOP")) {
            alarmManufacturer = "Samsung";

            action = "STOP";
        }
        else if(action.equals("com.samsung.sec.android.clockpackage.alarm.ALARM_STOPPED_IN_ALERT")) {
            alarmManufacturer = "Samsung";

            action = "STOP";
        }
        else if(action.equals("com.android.deskclock.ALARM_ALERT")) {
            Log.d(LOG_TAG, "Google alarmId"+ alarmId);
            alarmManufacturer = "Google";

            action = "STARTSNOOZE";
        }
        else if(action.equals("com.android.deskclock.ALARM_SNOOZE")) {
            alarmManufacturer = "Google";
            action = "SNOOZE";
        }
        else if(action.equals("com.android.deskclock.ALARM_DISMISS")) {
            alarmManufacturer = "Google";
            action = "STOP";
        }
        else if(action.equals("com.android.deskclock.ALARM_DONE")) {
            alarmManufacturer = "Google";
            action = "STOP";
        }
        else {
            action = "STOP";
        }

        return action;
    }

    public static Intent snoozeAlarm() {
        Intent intent = new Intent();

        if (alarmManufacturer.equals("Google")) {
            intent.setAction("com.android.deskclock.ALARM_SNOOZE");
        }
        else {
            intent.setAction("com.samsung.sec.android.clockpackage.alarm.ALARM_STOP");
            intent.putExtra("bDismiss", false);
        }
        return intent;
    }

    public static Intent dismissAlarm() {
        Intent intent = new Intent();

        if (alarmManufacturer.equals("Google")) {
            intent.setAction("com.android.deskclock.ALARM_DISMISS");
        }
        else {
            intent.setAction("com.samsung.sec.android.clockpackage.alarm.ALARM_STOP");
            intent.putExtra("bDismiss", true);
        }
        return intent;
    }
}
