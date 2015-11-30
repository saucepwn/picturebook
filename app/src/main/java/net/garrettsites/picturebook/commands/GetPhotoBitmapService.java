package net.garrettsites.picturebook.commands;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.cache.PhotoDiskCache;
import net.garrettsites.picturebook.model.Photo;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Garrett on 11/28/2015.
 */
public class GetPhotoBitmapService extends IntentService {

    // Calling arguments
    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_PHOTO_OBJ = "photo";

    // Return arguments
    public static final String ARG_IMAGE_PATH = "cache_image_path";

    private PhotoDiskCache mCache;

    public GetPhotoBitmapService() {
        super (GetPhotoBitmapService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);

        // Check if the photo exists in the app's disk cache. If not, get from network.
        mCache = new PhotoDiskCache(getApplicationContext());

        Photo photo = intent.getParcelableExtra(ARG_PHOTO_OBJ);

        String fbImageId = photo.getId();
        File imageLocation = null;

        if (mCache.doesPhotoExist(fbImageId)) {
            // Photo exists in cache. Serve from cache.
            imageLocation = mCache.getReadablePhotoFile(fbImageId);
        } else {
            // Photo does not exist in cache. Get from network, save to cache, then serve from cache.
            URL fbImageUrl = photo.getImageUrl();
            Bitmap photoBitmap = getBitmapFromInternet(fbImageUrl);

            imageLocation = mCache.savePhotoToCache(fbImageId, photoBitmap);
        }

        Bundle retVal = new Bundle();
        retVal.putString(ARG_IMAGE_PATH, imageLocation.getPath());
        receiver.send(Activity.RESULT_OK, retVal);
    }

    /**
     * Given a URL, retrieves the photo from the internet synchronously.
     * @param photoUrl The URL of the photo to retrieve.
     * @return A Bitmap object representing that photo.
     */
    private Bitmap getBitmapFromInternet(URL photoUrl) {
        try {
            InputStream in = (InputStream) photoUrl.getContent();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
