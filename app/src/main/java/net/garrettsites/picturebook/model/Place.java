package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Garrett on 12/4/2015.
 */
public class Place implements Parcelable {
    private String mId;
    private String mName;
    private float mOverallRating;
    private Location mLocation;

    public Place(String id, String name, float overallRating, Location location) {
        mId = id;
        mName = name;
        mOverallRating = overallRating;
        mLocation = location;
    }

    private Place(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mOverallRating = in.readFloat();
        mLocation = in.readParcelable(getClass().getClassLoader());
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public float getOverallRating() {
        return mOverallRating;
    }

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeFloat(mOverallRating);
        dest.writeParcelable(mLocation, flags);
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
