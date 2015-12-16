package net.garrettsites.picturebook.model;

import org.joda.time.DateTime;

/**
 * Created by Garrett on 11/28/2015.
 */
public class UserPreferences {
    // TODO: Hook all these up to SharedPreferences
    private static int mPhotoDelaySeconds = 10;
    private static boolean mRandomizePhotoOrder = false;
    private static DateTime mWakeTime;
    private static DateTime mSleepTime;

    public static void setPhotoDelaySeconds(int photoDelaySeconds) {
        mPhotoDelaySeconds = photoDelaySeconds;
    }

    public static int getPhotoDelaySeconds() {
        return mPhotoDelaySeconds;
    }

    public static void setRandomizePhotoOrder(boolean randomizePhotoOrder) {
        mRandomizePhotoOrder = randomizePhotoOrder;
    }

    public static boolean getRandomizePhotoOrder() {
        return mRandomizePhotoOrder;
    }

    public static void setWakeTime(DateTime wakeTime) {
        mWakeTime = wakeTime;
    }

    public static DateTime getWakeTime() {
        return mWakeTime;
    }

    public static void setSleepTime(DateTime sleepTime) {
        mSleepTime = sleepTime;
    }

    public static DateTime getSleepTime() {
        return mSleepTime;
    }
}
