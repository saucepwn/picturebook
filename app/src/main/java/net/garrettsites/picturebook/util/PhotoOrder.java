package net.garrettsites.picturebook.util;

/**
 * Created by Garrett on 12/1/2015.
 */
public abstract class PhotoOrder {

    protected int mNumPhotos;

    public PhotoOrder(int numPhotos) {
        mNumPhotos = numPhotos;
    }

    public abstract int getNextPhotoIdx();
}
