package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class FacebookAlbum implements Album {
    private String mType;
    private String mName;
    private String mDescription;
    private DateTime mCreatedTime;
    private DateTime mUpdatedTime;
    private String mId;

    private ArrayList<Photo> mPhotos;

    public FacebookAlbum(String type, String name, String description, DateTime createdTime, DateTime updatedTime, String id) {
        mType = type;
        mName = name;
        mDescription = description;
        mCreatedTime = createdTime;
        mUpdatedTime = updatedTime;
        mId = id;
    }

    private FacebookAlbum(Parcel in) {
        mType = in.readString();
        mName = in.readString();
        mDescription = in.readString();
        mCreatedTime = new DateTime(in.readString());
        mUpdatedTime = new DateTime(in.readString());
        mId = in.readString();

        mPhotos = in.readArrayList(getClass().getClassLoader());
    }

    public String getType() {
        return mType;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    @Override
    public DateTime getUpdatedTime() {
        return mUpdatedTime;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setPhotos(ArrayList<Photo> photos) {
        mPhotos = photos;
    }

    @Override
    public ArrayList<Photo> getPhotos() {
        return mPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mCreatedTime.toString());
        dest.writeString(mUpdatedTime.toString());
        dest.writeString(mId);
        dest.writeList(mPhotos);
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new FacebookAlbum(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
