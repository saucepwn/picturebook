package net.garrettsites.picturebook.util;

/**
 * Created by Garrett on 12/1/2015.
 */
public class SequentialPhotoOrder extends PhotoOrder {

    private int mCurrentPhotoIdx;

    public SequentialPhotoOrder(int numPhotos) {
        super(numPhotos);
    }

    @Override
    public int getNextPhotoIdx() {
        if (mCurrentPhotoIdx == mNumPhotos) {
            mCurrentPhotoIdx = 0;
        }

        return mCurrentPhotoIdx++;
    }
}
