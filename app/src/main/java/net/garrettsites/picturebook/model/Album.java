package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by Garrett on 11/20/2015.
 */
public class Album implements Parcelable {
    private String mType;
    private String mName;
    private String mDescription;
    private DateTime mCreatedTime;
    private DateTime mUpdatedTime;
    private int mId;

    public Album(String type, String name, String description, DateTime createdTime, DateTime updatedTime, int id) {
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
        mCreatedTime = new DateTime(data[2]);
        mUpdatedTime = new DateTime(data[3]);
        mId = Integer.parseInt(data[4]);
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

    public int getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                mType, mName, mCreatedTime.toString(), mUpdatedTime.toString(), Integer.toString(mId)
        });
    }
}
