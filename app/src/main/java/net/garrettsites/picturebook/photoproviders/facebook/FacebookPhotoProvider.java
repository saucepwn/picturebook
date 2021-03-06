package net.garrettsites.picturebook.photoproviders.facebook;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.ProviderConfiguration;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/28/2016.
 */
public class FacebookPhotoProvider implements PhotoProvider {
    private static String TAG = FacebookPhotoProvider.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    @Override
    public void initialize(Context appContext) {
        FacebookSdk.sdkInitialize(appContext);
    }

    @Override
    public Callable<ArrayList<Album>> getAllAlbumsCommand() {
        return new GetAllAlbumsCommand(mLogger, getCurrentAccessToken());
    }

    @Override
    public Album getAlbumPhotoData(Album album) throws Exception {
        if (!(album instanceof FacebookAlbum)) {
            throw new Exception("Album not an instance of FacebookAlbum");
        }

        return new GetAlbumPhotoDataCommand(mLogger, getCurrentAccessToken())
                .execute((FacebookAlbum) album);
    }

    @Override
    public Bitmap getPhotoBitmap(Photo photo) {
        return new GetPhotoBitmapCommand(mLogger).execute((FacebookPhoto) photo);
    }

    @Override
    public boolean isUserLoggedIn() {
        return !(Profile.getCurrentProfile() == null);
    }

    @Override
    public String getUserName() {
        if (isUserLoggedIn()) {
            return Profile.getCurrentProfile().getName();
        } else {
            return null;
        }
    }

    @Override
    public ProviderConfiguration getConfiguration() {
        return new FacebookConfiguration();
    }

    private AccessToken getCurrentAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }
}
