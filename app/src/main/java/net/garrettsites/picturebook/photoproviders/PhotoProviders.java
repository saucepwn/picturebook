package net.garrettsites.picturebook.photoproviders;

import android.content.Context;

import net.garrettsites.picturebook.photoproviders.facebook.FacebookPhotoProvider;
import net.garrettsites.picturebook.photoproviders.onedrive.OnedrivePhotoProvider;

/**
 * Created by Garrett on 6/28/2016.
 */
public class PhotoProviders {

    private static final FacebookPhotoProvider mFacebookPhotoProvider = new FacebookPhotoProvider();
    private static final OnedrivePhotoProvider mOnedrivePhotoProvider = new OnedrivePhotoProvider();

    /**
     * Initializes the PhotoProviders collection with the application's context.
     * @param applicationContext Application's context
     */
    public static void initWithContext(Context applicationContext) {
        for(PhotoProvider provider : getAllPhotoProviders()) {
            provider.initialize(applicationContext);
        }
    }

    /**
     * Gets a list of active photo providers on the device. New photo providers must be added here.
     * @return Active photo providers.
     */
    public static PhotoProvider[] getAllPhotoProviders() {
        PhotoProvider[] photoProviders = new PhotoProvider[2];

        photoProviders[0] = mFacebookPhotoProvider;
        photoProviders[1] = mOnedrivePhotoProvider;

        return photoProviders;
    }

    /**
     * A method that returns the FacebookPhotoProvider. Use if you need to specifically access the
     * Facebook provider.
     * @return A cached instance of the FacebookPhotoProvider.
     */
    public static FacebookPhotoProvider getFacebookPhotoProvider() {
        return mFacebookPhotoProvider;
    }

    /**
     * A method that returns the OnedrivePhotoProvider. Use if you need to specifically access the
     * Onedrive provider.
     * @return A cached instance of the OnedrivePhotoProvider.
     */
    public static OnedrivePhotoProvider getOnedrivePhotoProvider() {
        return mOnedrivePhotoProvider;
    }
}
