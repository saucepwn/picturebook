package net.garrettsites.picturebook.photoproviders.onedrive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.microsoft.applicationinsights.library.TelemetryClient;
import com.microsoft.applicationinsights.library.TelemetryContext;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;

import net.garrettsites.picturebook.model.Photo;

import java.io.InputStream;

/**
 * Created by garre on 7/1/2016.
 */
public class GetPhotoBitmapCommand {
    private static final String TAG = GetPhotoBitmapCommand.class.getName();

    private TelemetryClient mLogger;
    private IOneDriveClient mOnedriveClient;

    public GetPhotoBitmapCommand(TelemetryClient logger, IOneDriveClient onedriveClient) {
        mLogger = logger;
        mOnedriveClient = onedriveClient;
    }

    public Bitmap execute(OnedrivePhoto photo) {
        InputStream in = mOnedriveClient.getDrive().getItems(photo.getId()).getContent().buildRequest().get();
        return BitmapFactory.decodeStream(in);
    }
}
