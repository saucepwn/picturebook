package net.garrettsites.picturebook.util;

import com.microsoft.applicationinsights.library.TelemetryClient;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Garrett on 12/17/2015.
 */
public class Sleepitizer {
    private static final String TAG = Sleepitizer.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    private DateTime mSleepTime;

    public void setDailySleepTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the time to sleep has occurred in the past, increment our mSleepTime timer to the next day.
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        HashMap<String, String> properties = new HashMap<>();
        properties.put("SleepTime", calendar.toString());
        mLogger.trackEvent("Setting sleep time", properties);

        mSleepTime = new DateTime(calendar.getTimeInMillis());
    }

    /**
     * Poll this method to determine if it's time for the device to go to mSleepTime.
     * @return True if the device should go to mSleepTime, false otherwise.
     */
    public boolean timeToSleep() {
        return mSleepTime != null && System.currentTimeMillis() >= mSleepTime.getMillis();
    }
}
