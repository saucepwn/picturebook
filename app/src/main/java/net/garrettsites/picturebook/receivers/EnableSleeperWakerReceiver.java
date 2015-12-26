package net.garrettsites.picturebook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.util.Wakeitizer;

public class EnableSleeperWakerReceiver extends BroadcastReceiver {
    private static final String TAG = EnableSleeperWakerReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // We need this to re-set the alarm which displays albums at the user's configured time.
        Log.v(TAG, "Received intent, action: " + intent.getAction());

        UserPreferences prefs = new UserPreferences(context);

        if (prefs.isSleeperWakerEnabled()) {
            Log.i(TAG, "Enabling sleeper/waker due to broadcast: " + intent.getAction());

            int wakeHour = prefs.getWakeTimeHour();
            int wakeMinute = prefs.getWakeTimeMinute();

            Wakeitizer waker = Wakeitizer.getInstance(context);
            waker.setDailyWakeTime(wakeHour, wakeMinute);
        }
    }
}
