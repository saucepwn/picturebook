package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by Garrett on 12/7/2015.
 */
public class Tag implements Parcelable {
    private String mId;
    private String mName;
    private DateTime mTaggedTime;
    private double mX;
    private double mY;

    public Tag(String id, String name, DateTime taggedTime, double x, double y) {
        mId = id;
        mName = name;
        mTaggedTime = taggedTime;
        mX = x;
        mY = y;
    }

    private Tag(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mTaggedTime = new DateTime(in.readString());
        mX = in.readDouble();
        mY = in.readDouble();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public DateTime getTaggedTime() {
        return mTaggedTime;
    }

    public double getX() {
        return mX;
    }

    public double getY() {
        return mY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mTaggedTime.toString());
        dest.writeDouble(mX);
        dest.writeDouble(mY);
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}
