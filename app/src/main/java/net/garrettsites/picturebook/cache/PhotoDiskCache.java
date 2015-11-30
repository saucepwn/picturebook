package net.garrettsites.picturebook.cache;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Garrett on 11/29/2015.
 */
public class PhotoDiskCache {

    private static final String DIR_TYPE = Environment.DIRECTORY_PICTURES;

    private Context mAppContext;

    public PhotoDiskCache(Context appContext) {
        mAppContext = appContext;
    }

    public boolean doesPhotoExist(String photoId) {
        return getPhotoFile(photoId).exists();
    }

    /**
     * Gets a reference to a File in the cache for a given photo ID.
     * @param photoId Facebook's ID for the photo to retrieve.
     * @return A File handle pointing to the given photo. It may or may not exist.
     */
    private File getPhotoFile(String photoId) {
        return new File(getPreferredCacheForRead().getPath() + '/' + getPhotoFilename(photoId));
    }

    /**
     * Photos are represented in the cache by using their ID with the .jpg extension.
     * @param photoId The facebook ID of the photo.
     * @return A filename which can be used to access this photo in the app's photo cache.
     */
    private String getPhotoFilename(String photoId) {
        return photoId + ".jpg";
    }

    private File getPreferredCacheForRead() {
        // Use external storage if possible; use internal storage as a fallback.
        if (isExternalStorageReadable()) {
            return mAppContext.getExternalFilesDir(DIR_TYPE);
        } else {
            return mAppContext.getFilesDir();
        }
    }

    private File getPreferredCacheForWrite() {
        // Use external storage if possible; use internal storage as a fallback.
        if (isExternalStorageWritable()) {
            return mAppContext.getExternalFilesDir(DIR_TYPE);
        } else {
            return mAppContext.getFilesDir();
        }
    }

    /**
     * Determines if external storage is writable.
     * @return True if external storage is writable, false otherwise.
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        boolean stateValid = Environment.MEDIA_MOUNTED.equals(state);

        return stateValid && mAppContext.getExternalFilesDir(DIR_TYPE) != null;
    }

    /**
     * Determines if external storage is readable.
     * @return True if external storage is readable, false otherwise.
     */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        boolean stateValid = Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);

        return stateValid && mAppContext.getExternalFilesDir(DIR_TYPE) != null;
    }
}
