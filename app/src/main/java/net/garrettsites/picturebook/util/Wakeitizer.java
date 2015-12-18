package net.garrettsites.picturebook.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by Garrett on 12/15/2015.
 */
public class Wakeitizer {
    private static final String TAG = Wakeitizer.class.getName();
    private static Wakeitizer sharedInstance;

    private AlarmManager mAlarmManager;
    private PendingIntent mWakerIntent;

    private Context mAppContext;

    public static Wakeitizer getInstance(Context appContext) {
        if (sharedInstance == null) {
            sharedInstance = new Wakeitizer(appContext);
        }

        return sharedInstance;
    }

    private Wakeitizer(Context appContext) {
        mAppContext = appContext;

        mAlarmManager = (AlarmManager) mAppContext.getSystemService(Context.ALARM_SERVICE);

        Intent startSlideshowIntent = new Intent(mAppContext, StartSlideshowBroadcastReceiver.class);
        mWakerIntent = PendingIntent.getBroadcast(mAppContext, 0, startSlideshowIntent, 0);
    }

    public void cancelWaker() {
        if (mAlarmManager != null) {
            mAlarmManager.cancel(mWakerIntent);
            Log.i(TAG, "Cancelling wake alarm.");
        } else {
            Log.w(TAG, "Could not cancel wake alarm, mAlarmManager is null.");
        }
    }

    /**
     * Sets the device's daily wake and slideshow display time to the given hour and minute.
     * @param hourOfDay The hour
     * @param minute The minute
     */
    public void setDailyWakeTime(int hourOfDay, int minute) {
        // Set the alarm to start at the user's specified time.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the alarm is to be set for sometime in the past, set it for tomorrow.
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        // Set an alarm with a 24 hour repeating window.
        if (Build.VERSION.SDK_INT >= 19) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mWakerIntent);
        } else {
            mAlarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    mWakerIntent);
        }

        Log.i(TAG, "Setting wake alarm for " + hourOfDay + ":" + minute + " on " +
                (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));
    }
}
