package net.garrettsites.picturebook.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.services.StartSlideshowService;
import net.garrettsites.picturebook.util.Wakeitizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Garrett on 12/3/2015.
 */
public class StartSlideshowBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = StartSlideshowBroadcastReceiver.class.getName();

    public static final String ARG_ALBUM = "album";

    @Override
    public void onReceive(Context context, Intent intent) {
        TelemetryClient logger = TelemetryClient.getInstance();

        Map<String, String> properties = new HashMap<>();
        properties.put("TriggeredBy", intent.getAction());
        logger.trackEvent("StartSlideshowBroadcastReceiver received broadcast.", properties);
        Log.i(TAG, "Received broadcast, starting StartSlideshowService. Action: " + intent.getAction());

        Intent startSlideshowServiceIntent = new Intent(context, StartSlideshowService.class);

        if (intent.getParcelableExtra(ARG_ALBUM) != null) {
            Album album = intent.getParcelableExtra(ARG_ALBUM);
            startSlideshowServiceIntent.putExtra(StartSlideshowService.ARG_ALBUM, album);
        }

        startWakefulService(context, startSlideshowServiceIntent);

        // Re-set the alarm to wake the device 24 hours from now.
        Log.v(TAG, "Re-setting alarm to wake device tomorrow.");
        UserPreferences prefs = new UserPreferences(context);

        Wakeitizer waker = Wakeitizer.getInstance(context);
        waker.setDailyWakeTime(prefs.getWakeTimeHour(), prefs.getWakeTimeMinute());
    }
}
