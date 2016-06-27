package net.garrettsites.picturebook.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.garrettsites.picturebook.activities.ViewSlideshowActivity;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;

/**
 * This Receiver will do everything required to acquire a "random" album from Facebook, get all of
 * the photos, and start the ViewSlideshowActivity with the album. All of the below methods are
 * called IN ORDER. Please keep that in mind when editing this file.
 */
public class StartSlideshowService extends IntentService {
    private static final String TAG = StartSlideshowService.class.getName();

    public static final String ARG_ALBUM = "album";

    public StartSlideshowService() {
        super(StartSlideshowService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Incoming intent: desired action is to display slideshow.");
        Intent viewSlideshowActivity = new Intent(getApplicationContext(), ViewSlideshowActivity.class);

        if (intent.getParcelableExtra(ARG_ALBUM) != null) {
            Album album = intent.getParcelableExtra(ARG_ALBUM);
            viewSlideshowActivity.putExtra(ViewSlideshowActivity.ARG_ALBUM, album);
        }

        viewSlideshowActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(viewSlideshowActivity);

        StartSlideshowBroadcastReceiver.completeWakefulIntent(intent);
    }
}
