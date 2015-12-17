package net.garrettsites.picturebook.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import net.garrettsites.picturebook.receivers.BootReceiver;

/**
 * Created by Garrett on 11/28/2015.
 */
public class UserPreferences {
    private static final String TAG = UserPreferences.class.getName();

    private static final String SHARED_PREFS_NAME = "picturebook.preferences";

    private static final String KEY_PHOTO_DELAY = "photo_delay_sec";
    private static final String KEY_RANDOMIZE_PHOTO_ORDER = "randomize_photo_order";
    private static final String KEY_WAKE_TIME_HOUR = "wake_time_hour";
    private static final String KEY_WAKE_TIME_MINUTE = "wake_time_minute";
    private static final String KEY_SLEEP_TIME_HOUR = "sleep_time_hour";
    private static final String KEY_SLEEP_TIME_MINUTE = "sleep_time_minute";
    private static final String KEY_ENABLE_SLEEPER_WAKER = "enable_sleeper_waker";

    private Context mAppContext;

    public UserPreferences(Context appContext) {
        mAppContext = appContext;
    }

    public void setPhotoDelaySeconds(int photoDelaySeconds) {
        getEditor().putInt(KEY_PHOTO_DELAY, photoDelaySeconds).apply();
    }

    public int getPhotoDelaySeconds() {
        return getSharedPrefs().getInt(KEY_PHOTO_DELAY, 10);
    }

    public void setRandomizePhotoOrder(boolean randomizePhotoOrder) {
        getEditor().putBoolean(KEY_RANDOMIZE_PHOTO_ORDER, randomizePhotoOrder).apply();
    }

    public boolean getRandomizePhotoOrder() {
        return getSharedPrefs().getBoolean(KEY_RANDOMIZE_PHOTO_ORDER, false);
    }

    public void setWakeTime(int wakeTimeHour, int wakeTimeMinute) {
        SharedPreferences.Editor editor = getEditor();

        editor.putInt(KEY_WAKE_TIME_HOUR, wakeTimeHour);
        editor.putInt(KEY_WAKE_TIME_MINUTE, wakeTimeMinute);

        editor.apply();
    }

    public int getWakeTimeHour() {
        return getSharedPrefs().getInt(KEY_WAKE_TIME_HOUR, 6);
    }

    public int getWakeTimeMinute() {
        return getSharedPrefs().getInt(KEY_WAKE_TIME_MINUTE, 0);
    }

    public void setSleepTime(int sleepTimeHour, int sleepTimeMinute) {
        SharedPreferences.Editor editor = getEditor();

        editor.putInt(KEY_SLEEP_TIME_HOUR, sleepTimeHour);
        editor.putInt(KEY_SLEEP_TIME_MINUTE, sleepTimeMinute);

        editor.apply();
    }

    public int getSleepTimeHour() {
        return getSharedPrefs().getInt(KEY_SLEEP_TIME_HOUR, 23);
    }

    public int getSleepTimeMinute() {
        return getSharedPrefs().getInt(KEY_SLEEP_TIME_MINUTE, 30);
    }

    public void setEnableSleeperWaker(boolean enableSleeperWaker) {
        getEditor().putBoolean(KEY_ENABLE_SLEEPER_WAKER, enableSleeperWaker).apply();

        // Enable or disable the boot receiver congruent to the enableSleeperWaker setting.
        ComponentName receiver = new ComponentName(mAppContext, BootReceiver.class);
        PackageManager pm = mAppContext.getPackageManager();

        if (enableSleeperWaker) {
            // Enable the boot broadcast receiver so we can re-set alarm on device reboot.
            Log.v(TAG, "Enabling boot listener.");
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            // Disable the boot broadcast receiver since we don't need to re-set alarm.
            Log.v(TAG, "Disabling boot listener.");
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    public boolean isSleeperWakerEnabled() {
        return getSharedPrefs().getBoolean(KEY_ENABLE_SLEEPER_WAKER, false);
    }

    /**
     * @return The SharedPreferences.Editor object for the app's preferences.
     */
    private SharedPreferences.Editor getEditor() {
        return getSharedPrefs().edit();
    }

    /**
     * @return The app's SharedPreferences object where configuration is stored.
     */
    private SharedPreferences getSharedPrefs() {
        return mAppContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }
}
