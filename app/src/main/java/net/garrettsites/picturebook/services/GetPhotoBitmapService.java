package net.garrettsites.picturebook.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.cache.PhotoDiskCache;
import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.util.ImageResizer;

import java.io.File;

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
        File imageLocation;

        if (mCache.doesPhotoExist(photo)) {
            // Photo exists in cache. Serve from cache.
            imageLocation = mCache.getReadablePhotoFile(photo);
            Log.v(TAG, "Getting photo from cache.");
        } else {
            // Photo is not cached. Get from network, save to cache, then serve from cache.
            Bitmap photoBitmap = photo.getProvider().getPhotoBitmap(photo);

            Log.v(TAG, "Getting photo from internet. Saving to cache.");

            ImageResizer resizer = new ImageResizer(getApplicationContext(), photoBitmap);

            if (resizer.doesImageNeedToBeResized()) {
                Log.v(TAG, "Dimensions: " + photoBitmap.getWidth() + " x " +
                        photoBitmap.getHeight() + ". Scaling down image.");

                photoBitmap = resizer.shrinkBitmap();

                Log.v(TAG, "Image shrunk to " + photoBitmap.getWidth() + " x " +
                        photoBitmap.getHeight() + ".");
            }

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
}
