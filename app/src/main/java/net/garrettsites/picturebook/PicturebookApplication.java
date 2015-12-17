package net.garrettsites.picturebook;

import android.app.Application;

import net.garrettsites.picturebook.model.UserPreferences;

/**
 * Created by Garrett on 12/16/2015.
 */
public class PicturebookApplication extends Application {
    public UserPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        // Load all preferences into the UserPreferences object.
        preferences = new UserPreferences(getApplicationContext());
    }
}
