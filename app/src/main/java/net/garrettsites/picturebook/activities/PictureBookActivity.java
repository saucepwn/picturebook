package net.garrettsites.picturebook.activities;

import android.app.Activity;

import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Garrett on 11/12/2015.
 */
public class PictureBookActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();

        // Log 'install' and 'app active' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
