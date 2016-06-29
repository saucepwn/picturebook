package net.garrettsites.picturebook.photoproviders;

import net.garrettsites.picturebook.photoproviders.facebook.FacebookPhotoProvider;

/**
 * Created by Garrett on 6/28/2016.
 */
public class PhotoProviders {

    private static final FacebookPhotoProvider mFacebookPhotoProvider = new FacebookPhotoProvider();

    /**
     * Gets a list of active photo providers on the device. New photo providers must be added here.
     * @return Active photo providers.
     */
    public static PhotoProvider[] getAllPhotoProviders() {
        PhotoProvider[] photoProviders = new PhotoProvider[1];

        photoProviders[0] = mFacebookPhotoProvider;

        return photoProviders;
    }

    /**
     * A method that returns the FacebookPhotoProvider. Use if you need to specifically access the
     * Facebook provider.
     * @return A cached instance of the FacebookPhotoProvider.
     */
    public static PhotoProvider getFacebookPhotoProvider() {
        return mFacebookPhotoProvider;
    }
}
