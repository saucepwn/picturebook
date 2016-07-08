package net.garrettsites.picturebook.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;

/**
 * Created by Garrett on 11/28/2015.
 */
public class GetAlbumPhotoDataService extends IntentService {
    private static final String TAG = GetAlbumPhotoDataService.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();
    private int invocationCode;

    public static final String ARG_CODE = "code";
    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_ALBUM = "album";
    public static final String ARG_PHOTOS_METADATA = "photos_metadata";

    public GetAlbumPhotoDataService() {
        super(GetAlbumPhotoDataService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        invocationCode = intent.getIntExtra(ARG_CODE, -1);
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);
        Album album = intent.getParcelableExtra(ARG_ALBUM);

        Bundle resultBundle = new Bundle();
        resultBundle.putInt(ARG_CODE, invocationCode);

        try
        {
            album = album.getPhotoProvider().getAlbumPhotoData(album);
        } catch (Exception e) {
            // TODO: Instead of just logging exceptions here, pass the exception text to the UI.
            e.printStackTrace();
            mLogger.trackHandledException(e);
            receiver.send(Activity.RESULT_CANCELED, resultBundle);
            return;
        }

        // This album has no photos, return failure.
        if (album.getPhotos().size() == 0) {
            Log.w(TAG, "Album id " + album.getId() + " has no photos. Failing.");
            receiver.send(Activity.RESULT_CANCELED, resultBundle);
        } else {
            resultBundle.putParcelableArrayList(ARG_PHOTOS_METADATA, album.getPhotos());
            receiver.send(Activity.RESULT_OK, resultBundle);
        }
    }
}
