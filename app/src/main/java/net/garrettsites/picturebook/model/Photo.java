package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Garrett on 11/28/2015.
 */
public class Photo implements Parcelable {
    private String mId;
    private String mName;
    private String mUploadedBy;
    private String mUploadedById;
    private URL mImageUrl;
    private URL mPostUrl;
    private DateTime mCreatedTime;

    public Photo(String id, String name, String uploadedBy, String uploadedById, URL imageUrl, URL postUrl, DateTime createdTime) {
        this.mId = id;
        this.mName = name;
        this.mUploadedBy = uploadedBy;
        this.mUploadedById = uploadedById;
        this.mImageUrl = imageUrl;
        this.mPostUrl = postUrl;
        this.mCreatedTime = createdTime;
    }

    public Photo(Parcel in) {
        String[] data = new String[7];

        in.readStringArray(data);
        mId = data[0];
        mName = data[1];
        mUploadedBy = data[2];
        mUploadedById = data[3];

        try {
            mImageUrl = new URL(data[4]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            mPostUrl = new URL(data[5]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mCreatedTime = new DateTime(data[6]);
    }

    public String getId() {
        return mId;
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

    public URL getImageUrl() {
        return mImageUrl;
    }

    public URL getPostUrl() {
        return mPostUrl;
    }

    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
           mId, mName, mUploadedBy, mUploadedById, mImageUrl.toString(), mPostUrl.toString(), mCreatedTime.toString()
        });
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
