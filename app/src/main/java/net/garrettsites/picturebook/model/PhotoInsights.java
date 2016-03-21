package net.garrettsites.picturebook.model;

import android.content.res.Resources;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Garrett on 3/20/2016.
 */
public class PhotoInsights {
    private TelemetryClient mLogger = TelemetryClient.getInstance();
    private HashMap<InsightKey, String> insights = new HashMap<>();

    public enum InsightKey {
        // The photo's width.
        WIDTH,

        // The photo's height.
        HEIGHT,

        // The photo's date.
        DATE,

        // A short description of the photo.
        COMMENT,

        // A list of people in the photo.
        PEOPLE,

        // Where the photo was taken.
        PLACE,

        // Where this photo was from. Ex) Facebook, Tumblr
        SOURCE
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
     * Gets a localized key value pair of this photo's insights.
     * @param resources The application's resources object to get localized strings.
     * @return A localized map of key value pairs that can be displayed to the user.
     */
    public HashMap<String, String> getLocalizedInsights(Resources resources) {
        HashMap<String, String> localizedInsights = new HashMap<>(insights.size());

        for (Map.Entry<InsightKey, String> entry : insights.entrySet()) {
            localizedInsights.put(getLocalizedKeyName(resources, entry.getKey()), entry.getValue());
        }

        return localizedInsights;
    }

    /**
     * Formats a DateTime object into a string that can be displayed to the user as a photo insight.
     * @param dateTime The DateTime object to format.
     * @return A string of the formatted DateTime.
     */
    public static String formatDateTime(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("EEEE MMMM dd, yyyy");
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
            case WIDTH:
                return resources.getString(R.string.width);
            case HEIGHT:
                return resources.getString(R.string.height);
            case DATE:
                return resources.getString(R.string.date);
            case COMMENT:
                return resources.getString(R.string.comment);
            case PEOPLE:
                return resources.getString(R.string.people);
            case SOURCE:
                return resources.getString(R.string.soruce);
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
