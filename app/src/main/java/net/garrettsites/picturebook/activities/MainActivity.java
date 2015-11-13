package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import net.garrettsites.picturebook.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Log 'install' and 'app active' App Events.
        AppEventsLogger.activateApp(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(MainActivity.this);
    }

    /**
     * Launches the AccountsActivity.
     * @param view The view which created this action.
     */
    public void launchAccountsActivity(View view) {
        Intent i = new Intent(this, AccountsActivity.class);
        startActivity(i);
    }
}
