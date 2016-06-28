package net.garrettsites.picturebook.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Garrett on 11/29/2015.
 */
public class PhotoDiskCache {

    private static final String TAG = PhotoDiskCache.class.getName();
    private static final String DIR_TYPE = Environment.DIRECTORY_PICTURES;
    private final int JpegQualityLevel = 75;

    private Context mAppContext;
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    public PhotoDiskCache(Context appContext) {
        mAppContext = appContext;
    }

    /**
     * Checks to see if a photo exists in the cache.
     * @param photo The photo to check.
     * @return True if the photo is cached, false otherwise.
     */
    public boolean doesPhotoExist(Photo photo) {
        return getReadablePhotoFile(photo).exists();
    }

    /**
     * Saves the provided photo to the cache.
     * @param photo The photo object containing metadata about the photo.
     * @param bitmap The photo bitmap to save.
     * @return A File reference to the image that was just saved.
     */
    public File savePhotoToCache(Photo photo, Bitmap bitmap) {
        if (photo == null) {
            return null;
        }

        File writeLocation = getWritablePhotoFile(photo);

        // Remove the file if it currently exists.
        if (writeLocation.exists()) {
            writeLocation.delete();
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(writeLocation);
        } catch (FileNotFoundException e) {
            mLogger.trackHandledException(e);
            e.printStackTrace();
        }

        boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, JpegQualityLevel, outputStream);

        try {
            outputStream.close();
        } catch (IOException e) {
            mLogger.trackHandledException(e);
            e.printStackTrace();
        }

        if (!saved) {
            Log.e(TAG, "photo.compress returned 'false', could not save photo to cache.");
        }

        return writeLocation;
    }

    /**
     * Gets a reference to a File in the cache for a given photo ID.
     * @param photo The photo object to retrieve. The photo ID and provider are read to determine
     *              the photo's filename in cache.
     * @return A File handle pointing to the given photo. It may or may not exist.
     */
    public File getReadablePhotoFile(Photo photo) {
        return new File(getPreferredCacheForRead().getPath() + '/' + getPhotoFilename(photo));
    }

    /**
     * Gets a reference to a File in the cache for a given photo ID.
     * @param photo The photo object to retrieve. The photo ID and provider are read to determine
     *              the photo's filename in cache.
     * @return A File handle pointing to the given photo. It may or may not exist.
     */
    private File getWritablePhotoFile(Photo photo) {
        return new File(getPreferredCacheForWrite().getPath() + '/' + getPhotoFilename(photo));
    }

    /**
     * Photos are represented in the cache by using their ID with the .jpg extension.
     * @param photo The photo object to get the cached filename for.
     * @return A filename which can be used to access this photo in the app's photo cache.
     */
    private String getPhotoFilename(Photo photo) {
        return photo.getProvider() + "_" + photo.getId() + ".jpg";
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
