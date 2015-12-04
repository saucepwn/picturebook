package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.receivers.GetAllPhotoMetadataReceiver;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;
import net.garrettsites.picturebook.util.ChooseRandomAlbum;
import net.garrettsites.picturebook.receivers.GetAllAlbumsReceiver;
import net.garrettsites.picturebook.services.GetAllAlbumsService;
import net.garrettsites.picturebook.services.GetAllPhotoMetadataService;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;

import java.util.ArrayList;

public class MainActivity extends PictureBookActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((TextView) findViewById(R.id.status_textview)).setText("Status goes here");
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
        ((TextView) findViewById(R.id.status_textview)).setText("Triggered StartSlideshowBroadcastReceiver.");
        sendBroadcast(new Intent(this, StartSlideshowBroadcastReceiver.class));
    }
}
