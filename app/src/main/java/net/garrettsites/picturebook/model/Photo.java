package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Garrett on 11/28/2015.
 */
public class Photo implements Parcelable {
    private String mId;
    private int mOrder = 0;
    private String mName;
    private String mUploadedBy;
    private String mUploadedById;
    private String mPlaceName;
    private URL mImageUrl;
    private URL mPostUrl;
    private DateTime mCreatedTime;

    public Photo(String id, int order, String name, String uploadedBy, String uploadedById, URL imageUrl, URL postUrl, DateTime createdTime) {
        this.mId = id;
        this.mOrder= order;
        this.mName = name;
        this.mUploadedBy = uploadedBy;
        this.mUploadedById = uploadedById;
        this.mImageUrl = imageUrl;
        this.mPostUrl = postUrl;
        this.mCreatedTime = createdTime;
    }

    private Photo(Parcel in) {
        mId = in.readString();
        mOrder = in.readInt();
        mName = in.readString();
        mUploadedBy = in.readString();
        mUploadedById = in.readString();
        mPlaceName = in.readString();

        try {
            mImageUrl = new URL(in.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            mPostUrl = new URL(in.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mCreatedTime = new DateTime(in.readString());
    }

    public String getId() {
        return mId;
    }

    public int getOrder() {
        return mOrder;
    }

    public String getName() {
        return mName;
    }

    public String getUploadedBy() {
        return mUploadedBy;
    }

    public String getUploadedById() {
        return mUploadedById;
    }

    /**
     * Set the name of the place this photo was taken.
     * @param placeName The name of the place this photo was taken.
     */
    public void setPlaceName(String placeName) {
        mPlaceName = placeName;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public URL getImageUrl() {
        return mImageUrl;
    }

    public URL getPostUrl() {
        return mPostUrl;
    }

    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    /**
     * @return The duration between when the photo was uploaded, and now.
     */
    public Period getTimeElapsedSinceCreated() {
        return new Period(getCreatedTime(), DateTime.now());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(mOrder);
        dest.writeString(mName);
        dest.writeString(mUploadedBy);
        dest.writeString(mUploadedById);
        dest.writeString(mPlaceName);
        dest.writeString(mImageUrl.toString());
        dest.writeString(mPostUrl.toString());
        dest.writeString(mCreatedTime.toString());
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
