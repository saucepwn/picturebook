package net.garrettsites.picturebook.photoproviders.facebook;

import com.facebook.AccessToken;
import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/28/2016.
 */
public class FacebookPhotoProvider implements PhotoProvider {
    private static String TAG = FacebookPhotoProvider.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    @Override
    public Callable<ArrayList<Album>> GetAllAlbumsCommand() {
        return new FacebookGetAllAlbumsCommand(mLogger, AccessToken.getCurrentAccessToken());
    }
}
