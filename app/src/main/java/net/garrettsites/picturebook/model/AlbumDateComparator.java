package net.garrettsites.picturebook.model;

import java.util.Comparator;

/**
 * Compares two albums based on their dates.
 */
public class AlbumDateComparator implements Comparator<Album> {
    @Override
    public int compare(Album lhs, Album rhs) {
        return lhs.getAlbumDate().compareTo(rhs.getAlbumDate());
    }
}
