package net.garrettsites.picturebook.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import net.garrettsites.picturebook.services.GetAlbumAndStartSlideshowService;

/**
 * Created by Garrett on 12/3/2015.
 */
public class StartSlideshowBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = StartSlideshowBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "*** Received broadcast, starting GetAlbumAndStartSlideshowService ***");

        startWakefulService(context, new Intent(context, GetAlbumAndStartSlideshowService.class));
    }
}
