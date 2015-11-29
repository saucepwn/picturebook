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
    private String mUploadedBy;
    private String mUploadedById;
    private URL mImageUrl;
    private URL mPostUrl;
    private DateTime mCreatedTime;

    public Photo(String id, String uploadedBy, String uploadedById, URL imageUrl, URL postUrl, DateTime createdTime) {
        this.mId = id;
        this.mUploadedBy = uploadedBy;
        this.mUploadedById = uploadedById;
        this.mImageUrl = imageUrl;
        this.mPostUrl = postUrl;
        this.mCreatedTime = createdTime;
    }

    public Photo(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        mId = data[0];
        mUploadedBy = data[1];
        mUploadedById = data[2];

        try {
            mImageUrl = new URL(data[3]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            mPostUrl = new URL(data[4]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mCreatedTime = new DateTime(data[5]);
    }

    public String getId() {
        return mId;
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
           mId, mUploadedBy, mUploadedById, mImageUrl.toString(), mPostUrl.toString(), mCreatedTime.toString()
        });
    }
}
