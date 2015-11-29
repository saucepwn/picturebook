package net.garrettsites.picturebook.model;

/**
 * Created by Garrett on 11/28/2015.
 */
public class UserPreferences {
    // TODO: Hook all these up to SharedPreferences
    private static int mPhotoDelaySeconds = 10;

    public static void setPhotoDelaySeconds(int photoDelaySeconds) {
        mPhotoDelaySeconds = photoDelaySeconds;
    }

    public static int getPhotoDelaySeconds() {
        return mPhotoDelaySeconds;
    }
}
