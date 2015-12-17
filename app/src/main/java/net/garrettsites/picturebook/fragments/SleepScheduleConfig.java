package net.garrettsites.picturebook.fragments;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import net.garrettsites.picturebook.PicturebookApplication;
import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.util.Wakeitizer;

public class SleepScheduleConfig extends Fragment implements View.OnClickListener {

    private static final String TAG = SleepScheduleConfig.class.getName();

    private ToggleButton mSleepWakeEnable;

    private TextView mWakeTimeLabel;
    private TextView mSleepTimeLabel;

    /**
     * Required empty public constructor.
     */
    public SleepScheduleConfig() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sleep_schedule_config, container, false);

        // Get user preferences
        final UserPreferences userPreferences =
                ((PicturebookApplication) getActivity().getApplication()).preferences;

        // Set UI controls to their values from config.
        mSleepWakeEnable = (ToggleButton) view.findViewById(R.id.sleep_wake_enable);
        mSleepWakeEnable.setChecked(userPreferences.isSleeperWakerEnabled());

        int wakeHour = userPreferences.getWakeTimeHour();
        int wakeMinute = userPreferences.getWakeTimeMinute();
        mWakeTimeLabel = ((TextView) view.findViewById(R.id.wake_time_label));
        mWakeTimeLabel.setText(formatTime(wakeHour, wakeMinute));

        int sleepHour = userPreferences.getSleepTimeHour();
        int sleepMinute = userPreferences.getSleepTimeMinute();
        mSleepTimeLabel = ((TextView) view.findViewById(R.id.sleep_time_label));
        mSleepTimeLabel.setText(formatTime(sleepHour, sleepMinute));

        if (mSleepWakeEnable.isChecked()) {
            view.findViewById(R.id.edit_sleep_wake_time_container).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.edit_sleep_wake_time_container).setVisibility(View.INVISIBLE);
        }

        // Add listeners to the UI controls.
        view.findViewById(R.id.edit_wake_time).setOnClickListener(this);
        view.findViewById(R.id.edit_sleep_time).setOnClickListener(this);

        mSleepWakeEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPreferences.setEnableSleeperWaker(isChecked);
                Wakeitizer waker = Wakeitizer.getInstance(buttonView.getContext().getApplicationContext());

                if (isChecked) {
                    // Enable the Waker.
                    int wakeHour = userPreferences.getWakeTimeHour();
                    int wakeMinute = userPreferences.getWakeTimeMinute();

                    waker.setDailyWakeTime(wakeHour, wakeMinute);

                    // TODO: Enable the sleeper.

                    view.findViewById(R.id.edit_sleep_wake_time_container).setVisibility(View.VISIBLE);
                } else {
                    waker.cancelWaker();

                    // TODO: Disable the sleeper.

                    view.findViewById(R.id.edit_sleep_wake_time_container).setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        final UserPreferences userPreferences =
                ((PicturebookApplication) getActivity().getApplication()).preferences;

        switch(v.getId()) {
            case R.id.edit_wake_time:
                int wakeHour = userPreferences.getWakeTimeHour();
                int wakeMinute = userPreferences.getWakeTimeMinute();

                new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                userPreferences.setWakeTime(hourOfDay, minute);

                                if (mSleepWakeEnable.isChecked()) {
                                    Wakeitizer waker =
                                            Wakeitizer.getInstance(getActivity().getApplicationContext());
                                    waker.setDailyWakeTime(hourOfDay, minute);
                                }

                                mWakeTimeLabel.setText(formatTime(hourOfDay, minute));
                            }},
                        wakeHour,
                        wakeMinute,
                        DateFormat.is24HourFormat(getActivity())).show();
                break;

            case R.id.edit_sleep_time:
                int sleepHour = userPreferences.getSleepTimeHour();
                int sleepMinute = userPreferences.getSleepTimeMinute();

                new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                userPreferences.setSleepTime(hourOfDay, minute);

                                if (!mSleepWakeEnable.isChecked()) {
                                    // TODO: Schedule the sleeper to run at the specified time.
                                }

                                mSleepTimeLabel.setText(formatTime(hourOfDay, minute));
                            }},
                        sleepHour,
                        sleepMinute,
                        DateFormat.is24HourFormat(getActivity())).show();
                break;
        }
    }

    /**
     * Formats a 24 hour time into a human readable 12 hour time string. ex) 22:30 -> 10:30 pm
     * @param hour The hour [0-23]
     * @param minute The minute [0-59]
     * @return A formatted 12 hour time string in the format: hh:mm am/pm
     */
    private String formatTime(int hour, int minute) {
        boolean am = true;

        if (hour == 0) {
            hour = 12;
        } else if (hour > 12) {
            am = false;
            hour = hour - 12;
        }

        StringBuilder str = new StringBuilder();
        str.append(hour);
        str.append(":");

        if (minute < 10)
            str.append("0");

        str.append(minute);
        str.append(" ");

        if (am)
            str.append("am");
        else
            str.append("pm");

        return str.toString();
    }
}
