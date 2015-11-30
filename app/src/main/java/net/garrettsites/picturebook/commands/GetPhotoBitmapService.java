package net.garrettsites.picturebook.commands;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Garrett on 11/28/2015.
 */
public class GetPhotoBitmapService extends IntentService {

    public static final String ARG_RECEIVER = "receiverTag";

    public GetPhotoBitmapService() {
        super (GetPhotoBitmapService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO: Check if the photo exists in the app's disk cache. If not, get from network.

        // Super-TODO: Get the most appropriate resolution for the device screen size.
    }
}
