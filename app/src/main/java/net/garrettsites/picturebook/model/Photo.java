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
    private int mId;
    private String mUploadedBy;
    private int mUploadedById;
    private URL mImageUrl;
    private URL mPostUrl;
    private DateTime mCreatedTime;

    public Photo(int id, String uploadedBy, int uploadedById, URL imageUrl, URL postUrl, DateTime createdTime) {
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
        mId = Integer.parseInt(data[0]);
        mUploadedBy = data[1];
        mUploadedById = Integer.parseInt(data[2]);

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

    public int getId() {
        return mId;
    }

    public String getUploadedBy() {
        return mUploadedBy;
    }

    public int getUploadedById() {
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
           Integer.toString(mId), mUploadedBy, Integer.toString(mUploadedById), mImageUrl.toString(), mPostUrl.toString(), mCreatedTime.toString()
        });
    }
}
