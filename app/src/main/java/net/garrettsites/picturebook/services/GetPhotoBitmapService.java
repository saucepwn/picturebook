package net.garrettsites.picturebook.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.cache.PhotoDiskCache;
import net.garrettsites.picturebook.model.Photo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Garrett on 11/28/2015.
 */
public class GetPhotoBitmapService extends IntentService {
    private static final String TAG = GetPhotoBitmapService.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    // Calling arguments
    public static final String ARG_CODE = "code";
    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_PHOTO_OBJ = "photo";

    // Return arguments
    public static final String ARG_IMAGE_PATH = "cache_image_path";

    private PhotoDiskCache mCache;
    private int invocationCode;

    public GetPhotoBitmapService() {
        super(GetPhotoBitmapService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        invocationCode = intent.getIntExtra(ARG_CODE, -1);
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);

        // Check if the photo exists in the app's disk cache. If not, get from network.
        mCache = new PhotoDiskCache(getApplicationContext());

        Photo photo = intent.getParcelableExtra(ARG_PHOTO_OBJ);
        File imageLocation = null;

        if (mCache.doesPhotoExist(photo)) {
            // FacebookPhoto exists in cache. Serve from cache.
            imageLocation = mCache.getReadablePhotoFile(photo);
            Log.v(TAG, "Getting photo from cache.");
        } else {
            // FacebookPhoto does not exist in cache. Get from network, save to cache, then serve from cache.
            URL fbImageUrl = photo.getImageUrl();
            Bitmap photoBitmap = getBitmapFromInternet(fbImageUrl);
            Log.v(TAG, "Getting photo from internet. Saving to cache.");

            imageLocation = mCache.savePhotoToCache(photo, photoBitmap);
        }

        Bundle retVal = new Bundle();
        retVal.putInt(ARG_CODE, invocationCode);

        if (imageLocation == null) {
            Log.e(TAG, "imageLocation is null. Photo was not successfully retrieved.");
            receiver.send(Activity.RESULT_CANCELED, retVal);
        } else {
            retVal.putString(ARG_IMAGE_PATH, imageLocation.getPath());
            receiver.send(Activity.RESULT_OK, retVal);
        }
    }

    /**
     * Given a URL, retrieves the photo from the internet synchronously.
     * @param photoUrl The URL of the photo to retrieve.
     * @return A Bitmap object representing that photo.
     */
    private Bitmap getBitmapFromInternet(URL photoUrl) {
        try {
            long start = System.currentTimeMillis();
            InputStream in = (InputStream) photoUrl.getContent();
            long end = System.currentTimeMillis();

            HashMap<String, String> properties = new HashMap<>();
            properties.put("Path", photoUrl.getPath());
            mLogger.trackMetric("GetPhotoQuery", (double) (end - start), properties);

            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            mLogger.trackHandledException(e);
            e.printStackTrace();
        }

        return null;
    }
}
