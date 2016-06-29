package net.garrettsites.picturebook.photoproviders;

import net.garrettsites.picturebook.model.Album;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/28/2016.
 */
public interface PhotoProvider {
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
}
