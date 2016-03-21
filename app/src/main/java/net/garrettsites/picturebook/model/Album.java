package net.garrettsites.picturebook.model;

import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Garrett on 3/20/2016.
 */
public interface Album extends Parcelable {
    /**
     * @return The album's name.
     */
    String getName();

    /**
     * @return A short description of the album.
     */
    String getDescription();

    /**
     * @return The date the album was created.
     */
    DateTime getCreatedTime();

    /**
     * @return The date the album was last updated.
     */
    DateTime getUpdatedTime();

    /**
     * @return A unique ID referencing this album.
     */
    String getId();

    /**
     * Sets this album's photos.
     * @param photos The photos to add to this album.
     */
    void setPhotos(ArrayList<Photo> photos);

    /**
     * @return The photos in this album.
     */
    ArrayList<Photo> getPhotos();
}
