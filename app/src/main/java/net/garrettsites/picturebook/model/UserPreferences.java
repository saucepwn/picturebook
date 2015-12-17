package net.garrettsites.picturebook.model;

/**
 * Created by Garrett on 11/28/2015.
 */
public class UserPreferences {
    // TODO: Hook all these up to SharedPreferences
    private static int mPhotoDelaySeconds = 10;
    private static boolean mRandomizePhotoOrder = false;

    // Default wake time: 6:30am
    private static int mWakeTimeHour = 6;
    private static int mWakeTimeMinute = 0;

    // Default sleep time: 11:30pm
    private static int mSleepTimeHour = 23;
    private static int mSleepTimeMinute = 30;

    private static boolean mEnableSleeperWaker = false;

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

    public static void setWakeTime(int wakeTimeHour, int wakeTimeMinute) {
        mWakeTimeHour = wakeTimeHour;
        mWakeTimeMinute = wakeTimeMinute;
    }

    public static int getWakeTimeHour() {
        return mWakeTimeHour;
    }

    public static int getWakeTimeMinute() {
        return mWakeTimeMinute;
    }

    public static void setSleepTime(int sleepTimeHour, int sleepTimeMinute) {
        mSleepTimeHour = sleepTimeHour;
        mSleepTimeMinute = sleepTimeMinute;
    }

    public static int getSleepTimeHour() {
        return mSleepTimeHour;
    }

    public static int getSleepTimeMinute() {
        return mSleepTimeMinute;
    }

    public static void setEnableSleeperWaker(boolean enableSleeperWaker) {
        mEnableSleeperWaker = enableSleeperWaker;
    }

    public static boolean isSleeperWakerEnabled() {
        return mEnableSleeperWaker;
    }
}
