package net.garrettsites.picturebook.model;

/**
 * Created by Garrett on 6/28/2016.
 */
public class PictureBookException extends Exception {
    private ErrorCodes.Error mErrorCode;

    public PictureBookException(ErrorCodes.Error errorCode) {
        mErrorCode = errorCode;
    }

    public ErrorCodes.Error getErrorCode() {
        return mErrorCode;
    }
}
