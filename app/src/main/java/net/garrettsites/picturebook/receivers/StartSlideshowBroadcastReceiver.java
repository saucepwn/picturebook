package net.garrettsites.picturebook.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.services.GetAlbumAndStartSlideshowService;
import net.garrettsites.picturebook.util.Wakeitizer;

/**
 * Created by Garrett on 12/3/2015.
 */
public class StartSlideshowBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = StartSlideshowBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast, starting GetAlbumAndStartSlideshowService");

        startWakefulService(context, new Intent(context, GetAlbumAndStartSlideshowService.class));

        // Re-set the alarm to wake the device 24 hours from now.
        Log.v(TAG, "Re-setting alarm to wake device tomorrow.");
        UserPreferences prefs = new UserPreferences(context);

        Wakeitizer waker = Wakeitizer.getInstance(context);
        waker.setDailyWakeTime(prefs.getWakeTimeHour(), prefs.getWakeTimeMinute());
    }
}
