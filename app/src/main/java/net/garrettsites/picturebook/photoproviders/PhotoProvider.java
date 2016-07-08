package net.garrettsites.picturebook.photoproviders;

import android.content.Context;
import android.graphics.Bitmap;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/28/2016.
 */
public interface PhotoProvider {
    /**
     * Initializes the provider with the application's context. This is called by the application
     * when it launches.
     * @param appContext The application's context.
     */
    void initialize(Context appContext);

    /**
     * Makes a network call to get all albums for a given provider.
     * @return A list of albums hosted by this provider.
     */
    Callable<ArrayList<Album>> getAllAlbumsCommand();

    /**
     * Makes a network call to get the photo data for a given album.
     * @param album The album to retrieve photo metadata for. For a Facebook album, this object only
     *              needs to be populated with the album's ID, and should be a FacebookAlbum object.
     * @return An Album object with a populated photos array. This array will contain matadata about
     * the photos in the album, but not the bitmaps themselves.
     */
    Album getAlbumPhotoData(Album album) throws Exception;

    /**
     * Gets a photo bitmap from the photo's provider.
     * @param photo The photo metadata to retrieve a bitmap for.
     * @return A bitmap of the requested Photo.
     */
    Bitmap getPhotoBitmap(Photo photo);

    /**
     * Returns whether or not a user is currently logged in to the photo provider.
     * @return True if a user is logged in, false otherwise
     */
    boolean isUserLoggedIn();

    /**
     * If a user is logged in, this returns their name. If a user is not logged in, this shall
     * return null.
     * @return The name of the user who is logged in.
     */
    String getUserName();

    /**
     * @return Statically configured information about the provider, such as the provider's name and
     * primary color. These are values that are set at compile time and never changed.
     */
    ProviderConfiguration getConfiguration();
}
