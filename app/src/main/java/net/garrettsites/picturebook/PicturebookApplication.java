package net.garrettsites.picturebook;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.microsoft.applicationinsights.library.ApplicationInsights;

import net.garrettsites.picturebook.model.UserPreferences;

/**
 * Created by Garrett on 12/16/2015.
 */
public class PicturebookApplication extends Application {
    private static final String TAG = PicturebookApplication.class.getName();

    public UserPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        // Load all preferences into the UserPreferences object.
        preferences = new UserPreferences(getApplicationContext());

        Log.v(TAG, "Initializing Facebook SDK, UserPreferences, and AppInsights.");
        FacebookSdk.sdkInitialize(getApplicationContext());

        ApplicationInsights.setup(getApplicationContext(), this);

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            ApplicationInsights.getTelemetryContext().setAuthenticatedUserId(profile.getId());
        }

        ApplicationInsights.start();
    }
}
