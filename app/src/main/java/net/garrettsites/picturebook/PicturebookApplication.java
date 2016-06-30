package net.garrettsites.picturebook;

import android.app.Application;
import android.util.Log;

import com.facebook.Profile;
import com.microsoft.applicationinsights.library.ApplicationInsights;

import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;

/**
 * Created by Garrett on 12/16/2015.
 */
public class PicturebookApplication extends Application {
    private static final String TAG = PicturebookApplication.class.getName();

    public UserPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Initializing Facebook SDK, UserPreferences, and AppInsights.");

        // Load all preferences into the UserPreferences object.
        preferences = new UserPreferences(getApplicationContext());
        ApplicationInsights.setup(getApplicationContext(), this);
        ApplicationInsights.start();

        PhotoProviders.initWithContext(getApplicationContext());

        // TODO: Right now this only looks at Facebook user information. Maybe have it look at OneDrive information, too.
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            ApplicationInsights.getTelemetryContext().setAuthenticatedUserId(profile.getId());
        }

    }
}
