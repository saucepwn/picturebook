package net.garrettsites.picturebook.services;

import android.app.IntentService;
import android.content.Intent;

import net.garrettsites.picturebook.activities.ViewSlideshowActivity;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;

/**
 * This Receiver will do everything required to acquire a "random" album from Facebook, get all of
 * the photos, and start the ViewSlideshowActivity with the album. All of the below methods are
 * called IN ORDER. Please keep that in mind when editing this file.
 */
public class GetAlbumAndStartSlideshowService extends IntentService {
    private static final String TAG = GetAlbumAndStartSlideshowService.class.getName();

    public GetAlbumAndStartSlideshowService() {
        super(GetAlbumAndStartSlideshowService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent viewSlideshowActivity = new Intent(getApplicationContext(), ViewSlideshowActivity.class);
        viewSlideshowActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(viewSlideshowActivity);

        StartSlideshowBroadcastReceiver.completeWakefulIntent(intent);
    }
}
