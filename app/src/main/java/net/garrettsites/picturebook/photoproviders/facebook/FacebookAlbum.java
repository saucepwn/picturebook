package net.garrettsites.picturebook.photoproviders.facebook;

import android.os.Parcel;
import android.os.Parcelable;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;

import org.joda.time.DateTime;

/**
 * Created by Garrett on 11/20/2015.
 */
public class FacebookAlbum extends Album {
    private String mType;

    public FacebookAlbum(String type, String name, String description, DateTime createdTime, DateTime updatedTime, String id) {
        super(name, description, createdTime, updatedTime, id);
        mType = type;
    }

    private FacebookAlbum(Parcel in) {
        super(in);
    }

    /**
     * Gets the type of the album.
     * @return Either: app, cover, profile, mobile, wall, normal, album
     */
    public String getType() {
        return mType;
    }

    @Override
    public DateTime getAlbumDate() {
        // Facebook's most accurate representation of when an album occured is the updated date.
        return mUpdatedTime;
    }

    @Override
    protected void doWriteToParcel(Parcel dest) {
        dest.writeString(mType);
    }

    @Override
    protected void doReadFromParcel(Parcel in) {
        mType = in.readString();
    }

    @Override
    public PhotoProvider getPhotoProvider() {
        return PhotoProviders.getFacebookPhotoProvider();
    }

    public static final Parcelable.Creator<FacebookAlbum> CREATOR = new Parcelable.Creator<FacebookAlbum>() {
        public FacebookAlbum createFromParcel(Parcel in) {
            return new FacebookAlbum(in);
        }

        public FacebookAlbum[] newArray(int size) {
            return new FacebookAlbum[size];
        }
    };
}
