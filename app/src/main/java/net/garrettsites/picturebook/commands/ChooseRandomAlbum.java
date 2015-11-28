package net.garrettsites.picturebook.commands;

import net.garrettsites.picturebook.model.Album;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/25/2015.
 */
public class ChooseRandomAlbum {
    private ArrayList<Album> mAlbums;

    public ChooseRandomAlbum(ArrayList<Album> albums) {
        mAlbums = albums;
    }

    /**
     * Chooses an album from the input ArrayList.
     * @return An album chosen from the input array of albums.
     */
    public Album selectRandomAlbum() {
        // TODO: Create an algorithm which more intelligently does this
        int randomIdx = (int) (Math.random() * mAlbums.size());
        return mAlbums.get(randomIdx);
    }
}
