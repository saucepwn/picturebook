package net.garrettsites.picturebook.photoproviders.facebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by garre on 7/1/2016.
 */
public class GetPhotoBitmapCommand {
    private static final String TAG = GetPhotoBitmapCommand.class.getName();

    private TelemetryClient mLogger;

    public GetPhotoBitmapCommand(TelemetryClient logger) {
        mLogger = logger;
    }

    /**
     * Gets a Facebook photo from the internet.
     * @param photo The metadata object representing the photo to retrieve.
     * @return The photo bitmap.
     */
    public Bitmap execute(FacebookPhoto photo) {
        URL photoUrl = photo.getImageUrl();

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
