package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class Album implements Parcelable {
    private String mType;
    private String mName;
    private String mDescription;
    private DateTime mCreatedTime;
    private DateTime mUpdatedTime;
    private String mId;

    // NOTE: This field is not parceled.
    private ArrayList<Photo> mPhotos;

    public Album(String type, String name, String description, DateTime createdTime, DateTime updatedTime, String id) {
        mType = type;
        mName = name;
        mDescription = description;
        mCreatedTime = createdTime;
        mUpdatedTime = updatedTime;
        mId = id;
    }

    public Album(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        mType = data[0];
        mName = data[1];
        mDescription = data[2];
        mCreatedTime = new DateTime(data[3]);
        mUpdatedTime = new DateTime(data[4]);
        mId = data[5];
    }

    public String getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    public DateTime getUpdatedTime() {
        return mUpdatedTime;
    }

    public String getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                mType, mName, mDescription, mCreatedTime.toString(), mUpdatedTime.toString(), mId
        });
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
