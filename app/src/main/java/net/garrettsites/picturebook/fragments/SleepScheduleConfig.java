package net.garrettsites.picturebook.fragments;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;

import java.util.Calendar;

public class SleepScheduleConfig extends Fragment {

    private static final String TAG = SleepScheduleConfig.class.getName();

    private TimePicker mWakeTimePicker;
    private TimePicker mSleepTimePicker;

    /**
     * Required empty public constructor.
     */
    public SleepScheduleConfig() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_schedule_config, container, false);
        final Context context = inflater.getContext();

        mWakeTimePicker = (TimePicker) view.findViewById(R.id.wake_time_picker);
        mSleepTimePicker = (TimePicker) view.findViewById(R.id.sleep_time_picker);

        // Update the app's alarm to wake the device at a given time each day.
        mWakeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // RTC_WAKEUP
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                PendingIntent alarmIntent;

                Intent startSlideshowIntent = new Intent(context, StartSlideshowBroadcastReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, 0, startSlideshowIntent, 0);

                // Set the alarm to start at the user's specified time.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // Set an alarm with a 24 hour repeating window.
                alarmMgr.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent);

                Log.i(TAG, "Setting wake alarm for " + hourOfDay + ":" + minute);
            }
        });

        // Update the app's alarm to sleep the device at at given time each day.
        mSleepTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

            }
        });

        return view;
    }
}
