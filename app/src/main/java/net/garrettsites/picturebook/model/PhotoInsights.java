package net.garrettsites.picturebook.model;

import android.content.res.Resources;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Created by Garrett on 3/20/2016.
 */
public class PhotoInsights {
    private TelemetryClient mLogger = TelemetryClient.getInstance();
    private TreeMap<InsightKey, String> insights = new TreeMap<>();

    public enum InsightKey {
        // A list of people in the photo.
        PEOPLE,

        // A short description of the photo.
        COMMENT,

        // Where the photo was taken.
        PLACE,

        // The photo's date.
        DATE,

        // The time the photo was taken.
        TIME,

        // Where this photo was from. Ex) Facebook, Tumblr
        SOURCE,

        // The photo's original width before any resizing the app may perform.
        ORIG_WIDTH,

        // The photo's original height before any resizing the app may perform.
        ORIG_HEIGHT,

        // The photo's resized width.
        WIDTH,

        // The photo's resized height.
        HEIGHT,

        CAMERA,
        EXPOSURE,
        F_NUMBER,
        FOCAL_LENGTH,
        ISO,
        TAKEN_TIME
    }

    /**
     * Adds an insight to this photo.
     * @param key The key of the insight. This must be an enum so it can be properly localized at
     *            display time.
     * @param insight The insight's value.
     */
    public void addInsight(InsightKey key, String insight) {
        if (insight != null && insight.length() > 0)
            insights.put(key, insight);
    }

    /**
     * Gets a localized and sorted key value pair of this photo's insights. The insights are sorted
     * in the order the enum is defined.
     * @param resources The application's resources object to get localized strings.
     * @return A localized map of key value pairs that can be displayed to the user.
     */
    public LinkedHashMap<String, String> getLocalizedInsights(Resources resources) {
        LinkedHashMap<String, String> localizedInsights = new LinkedHashMap<>();

        for (InsightKey key : InsightKey.values()) {
            if (insights.containsKey(key)) {
                localizedInsights.put(getLocalizedKeyName(resources, key), insights.get(key));
            }
        }

        return localizedInsights;
    }

    /**
     * Formats a DateTime object into a string that can be displayed to the user as a photo insight.
     * @param dateTime The DateTime object to format.
     * @return A string of the formatted photo date.
     */
    public static String formatDate(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("EEEE MMMM dd, yyyy");
        return formatter.print(dateTime);
    }

    /**
     * Formats a DateTime object into a string that can be displayed to the user as a photo insight.
     * @param dateTime The DateTime object to format.
     * @return A string of the formatted photo time.
     */
    public static String formatTime(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm aa zzz");
        return formatter.print(dateTime);
    }

    /**
     * Gets a localized insight key string.
     * @param resources The application's Resources object. Retrieve using activity.getResources()
     * @param key The key name to be localized.
     * @return A localized version of the key name that can be displayed in the UI.
     */
    private String getLocalizedKeyName(Resources resources, InsightKey key) {
        switch (key) {
            case ORIG_WIDTH:
                return resources.getString(R.string.orig_width);
            case ORIG_HEIGHT:
                return resources.getString(R.string.orig_height);
            case WIDTH:
                return resources.getString(R.string.width);
            case HEIGHT:
                return resources.getString(R.string.height);
            case DATE:
                return resources.getString(R.string.date);
            case TIME:
                return resources.getString(R.string.time);
            case COMMENT:
                return resources.getString(R.string.comment);
            case PEOPLE:
                return resources.getString(R.string.people);
            case PLACE:
                return resources.getString(R.string.place);
            case SOURCE:
                return resources.getString(R.string.source);
            case CAMERA:
                return resources.getString(R.string.camera);
            case EXPOSURE:
                return resources.getString(R.string.exposure);
            case F_NUMBER:
                return resources.getString(R.string.f_number);
            case FOCAL_LENGTH:
                return resources.getString(R.string.focal_length);
            case ISO:
                return resources.getString(R.string.iso);
            case TAKEN_TIME:
                return resources.getString(R.string.taken_time);
            default:
                mLogger.trackManagedException(
                        "LocalizedKeyNotFound",
                        "No localized key name for insight: " + key.toString(),
                        "",
                        true);

                return key.toString();
        }
    }

}
