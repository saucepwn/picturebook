package net.garrettsites.picturebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;

public class MainActivity extends PictureBookActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Launches the AccountsActivity.
     * @param view The view which created this action.
     */
    public void launchAccountsActivity(View view) {
        Intent i = new Intent(this, AccountsActivity.class);
        startActivity(i);
    }

    public void startSlideshow(View view) {
        sendBroadcast(new Intent(this, StartSlideshowBroadcastReceiver.class));
    }
}
