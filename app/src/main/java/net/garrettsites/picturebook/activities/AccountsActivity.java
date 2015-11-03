package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.os.Bundle;

import com.facebook.appevents.AppEventsLogger;

import net.garrettsites.picturebook.R;

public class AccountsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        // Left off here: https://developers.facebook.com/docs/facebook-login/android
    }

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
