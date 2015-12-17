package net.garrettsites.picturebook;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;

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

        Log.v(TAG, "Initializing Facebook SDK and UserPreferences.");
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
