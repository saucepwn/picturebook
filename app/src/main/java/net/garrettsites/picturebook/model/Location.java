package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Garrett on 12/4/2015.
 */
public class Location implements Parcelable {
    private String mId;
    private String mCity;
    private String mCountry;
    private float mLatitude;
    private float mLongitude;
    private String mName;
    private String mRegion;
    private String mState;
    private String mStreet;
    private String mZip;

    public Location(String mId, String mCity, String mCountry, float mLatitude, float mLongitude, String mName, String mRegion, String mState, String mStreet, String mZip) {
        this.mId = mId;
        this.mCity = mCity;
        this.mCountry = mCountry;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mName = mName;
        this.mRegion = mRegion;
        this.mState = mState;
        this.mStreet = mStreet;
        this.mZip = mZip;
    }

    private Location(Parcel in) {
        mId = in.readString();
        mCity = in.readString();
        mCountry = in.readString();
        mLatitude = in.readFloat();
        mLongitude = in.readFloat();
        mName = in.readString();
        mRegion = in.readString();
        mState = in.readString();
        mStreet = in.readString();
        mZip = in.readString();
    }

    public String getId() {
        return mId;
    }

    public String getCity() {
        return mCity;
    }

    public String getCountry() {
        return mCountry;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public String getName() {
        return mName;
    }

    public String getRegion() {
        return mRegion;
    }

    public String getState() {
        return mState;
    }

    public String getStreet() {
        return mStreet;
    }

    public String getZip() {
        return mZip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getCity());
        dest.writeString(getCountry());
        dest.writeFloat(getLatitude());
        dest.writeFloat(getLongitude());
        dest.writeString(getName());
        dest.writeString(getRegion());
        dest.writeString(getState());
        dest.writeString(getStreet());
        dest.writeString(getZip());
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
