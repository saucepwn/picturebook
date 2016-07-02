package net.garrettsites.picturebook.photoproviders.onedrive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.microsoft.applicationinsights.library.TelemetryClient;
import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.OneDriveClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.ProviderConfiguration;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/29/2016.
 */
public class OnedrivePhotoProvider implements PhotoProvider {
    private static String TAG = OnedrivePhotoProvider.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    private Context mAppContext;
    private IOneDriveClient mOnedriveClient;

    private final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
        @Override
        public String getClientId() {
            return "bf33ee00-affe-4c8d-912f-8aab0abdb7a1";
        }

        @Override
        public String[] getScopes() {
            return new String[] { "onedrive.readonly", "offline_access" };
        }
    };

    private final IClientConfig onedriveConfig =
            DefaultClientConfig.createWithAuthenticator(msaAuthenticator);

    @Override
    public void initialize(Context appContext) {
        mAppContext = appContext;
    }

    @Override
    public Callable<ArrayList<Album>> getAllAlbumsCommand() {
        return new GetAllAlbumsCommand(mLogger, mOnedriveClient);
    }

    @Override
    public Album getAlbumPhotoData(Album album) throws Exception {
        if (!(album instanceof OnedriveAlbum)) {
            throw new Exception("Album not an instance of OnedriveAlbum");
        }

        GetAlbumPhotoDataCommand command = new GetAlbumPhotoDataCommand(mLogger, mOnedriveClient);
        return command.execute((OnedriveAlbum) album);
    }

    @Override
    public Bitmap getPhotoBitmap(Photo photo) {
        return new GetPhotoBitmapCommand(mLogger, mOnedriveClient).execute((OnedrivePhoto) photo);
    }

    @Override
    public boolean isUserLoggedIn() {
        return !(mOnedriveClient == null
                || mOnedriveClient.getAuthenticator().getAccountInfo() == null
                || mOnedriveClient.getAuthenticator().getAccountInfo().isExpired());
    }

    @Override
    public String getUserName() {
        // TODO: Is there a way to get the actual user name or e-mail address?
        if (isUserLoggedIn())
            return "Logged in";
        else
            return null;
    }

    @Override
    public ProviderConfiguration getConfiguration() {
        return new OnedriveConfiguration();
    }

    /**
     * Begin the OneDrive login flow.
     * @param activity The current Android Activity, used to spawn a webview for authentication.
     */
    public void logIn(Activity activity) {
        mOnedriveClient = new OneDriveClient.Builder().fromConfig(onedriveConfig).loginAndBuildClient(activity);
    }

    /**
     * Logs out the current OneDrive user.
     */
    public void logOut() {
        mOnedriveClient.getAuthenticator().logout();
        mOnedriveClient = null;
    }
}
