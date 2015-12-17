package net.garrettsites.picturebook.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.util.Wakeitizer;

public class SleepScheduleConfig extends Fragment {

    private static final String TAG = SleepScheduleConfig.class.getName();

    private TimePicker mWakeTimePicker;
    private TimePicker mSleepTimePicker;
    private ToggleButton mSleepWakeEnable;

    /**
     * Required empty public constructor.
     */
    public SleepScheduleConfig() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_schedule_config, container, false);

        mWakeTimePicker = (TimePicker) view.findViewById(R.id.wake_time_picker);
        mSleepTimePicker = (TimePicker) view.findViewById(R.id.sleep_time_picker);
        mSleepWakeEnable = (ToggleButton) view.findViewById(R.id.sleep_wake_enable);

        // Set UI controls to their values from config.
        mSleepWakeEnable.setChecked(UserPreferences.isSleeperWakerEnabled());

        mWakeTimePicker.setCurrentHour(UserPreferences.getWakeTimeHour());
        mWakeTimePicker.setCurrentMinute(UserPreferences.getWakeTimeMinute());

        mSleepTimePicker.setCurrentHour(UserPreferences.getSleepTimeHour());
        mSleepTimePicker.setCurrentMinute(UserPreferences.getSleepTimeMinute());

        // Add listeners to the UI controls.
        mSleepWakeEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserPreferences.setEnableSleeperWaker(isChecked);
                Wakeitizer waker = Wakeitizer.getInstance(buttonView.getContext().getApplicationContext());

                if (isChecked) {
                    // Enable the sleeper & waker.
                    int wakeHour = UserPreferences.getWakeTimeHour();
                    int wakeMinute = UserPreferences.getWakeTimeMinute();

                    waker.setDailyWakeTime(wakeHour, wakeMinute);
                } else {
                    // Disable the sleeper & waker.
                    waker.cancelWaker();
                }
            }
        });

        // Update the app's alarm to wake the device at a given time each day.
        mWakeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                UserPreferences.setWakeTime(hourOfDay, minute);

                if (mSleepWakeEnable.isChecked()) {
                    Wakeitizer waker =
                            Wakeitizer.getInstance(inflater.getContext().getApplicationContext());
                    waker.setDailyWakeTime(hourOfDay, minute);
                }
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
