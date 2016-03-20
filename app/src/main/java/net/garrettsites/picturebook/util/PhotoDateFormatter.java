package net.garrettsites.picturebook.util;

import android.content.res.Resources;

import net.garrettsites.picturebook.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Period;

/**
 * Created by Garrett on 3/20/2016.
 */
public class PhotoDateFormatter {
    private Resources mResources;

    /**
     * Creates a new instance of the PhotoDateFormatter class.
     * @param resources The Activity's resources object. Call activity.getResources();
     */
    public PhotoDateFormatter(Resources resources) {
        mResources = resources;
    }

    /**
     * Given a Period representing the time between when a photo was taken and now, this method
     * formats a string to show the user the amount of time that's elapsed during the Period.
     * @param from The beginning of the time span.
     * @param to The end of the time span.
     * @return A formatted string for the UI.
     */
    public String formatTimeSincePhotoCreated(DateTime from, DateTime to) {
        int years = Math.abs(to.get(DateTimeFieldType.year()) - from.get(DateTimeFieldType.year()));
        if (years > 0) {
            return mResources.getQuantityString(R.plurals.var_years_ago, years, years);
        }

        Period period = new Period(from, to);
        if (period.getMonths() != 0) {
            int months = period.getMonths();
            return mResources.getQuantityString(R.plurals.var_months_ago, months, months);

        } else if (period.getWeeks() != 0) {
            int weeks = period.getWeeks();
            return mResources.getQuantityString(R.plurals.var_weeks_ago, weeks, weeks);

        } else if (period.getDays() != 0) {
            int days = period.getDays();
            return mResources.getQuantityString(R.plurals.var_days_ago, days, days);

        } else {
            return mResources.getString(R.string.just_now);
        }
    }
}
