package net.garrettsites.picturebook.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.microsoft.applicationinsights.library.TelemetryClient;

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

    public boolean doesPhotoExist(String photoId) {
        return getReadablePhotoFile(photoId).exists();
    }

    /**
     * Saves the provided photo to the cache.
     * @param photoId The Facebook ID of the photo to save. Used when determining the filename.
     * @param photo The photo to save.
     * @return A File reference to the image that was just saved.
     */
    public File savePhotoToCache(String photoId, Bitmap photo) {
        if (photo == null) {
            return null;
        }

        File writeLocation = getWritablePhotoFile(photoId);

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

        boolean saved = photo.compress(Bitmap.CompressFormat.JPEG, JpegQualityLevel, outputStream);

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
     * @param photoId Facebook's ID for the photo to retrieve.
     * @return A File handle pointing to the given photo. It may or may not exist.
     */
    public File getReadablePhotoFile(String photoId) {
        return new File(getPreferredCacheForRead().getPath() + '/' + getPhotoFilename(photoId));
    }

    /**
     * Gets a reference to a File in the cache for a given photo ID.
     * @param photoId Facebook's ID for the photo to retrieve.
     * @return A File handle pointing to the given photo. It may or may not exist.
     */
    private File getWritablePhotoFile(String photoId) {
        return new File(getPreferredCacheForWrite().getPath() + '/' + getPhotoFilename(photoId));
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
