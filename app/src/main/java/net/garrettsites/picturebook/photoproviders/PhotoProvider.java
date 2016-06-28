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
    Callable<ArrayList<Album>> GetAllAlbumsCommand();


}
