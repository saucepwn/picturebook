package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Garrett on 11/28/2015.
 */
public class FacebookPhoto implements IPhoto {
    private String mId;
    private int mOrder = 0;
    private String mName;
    private String mUploadedBy;
    private String mUploadedById;
    private String mPlaceName;
    private int mWidth;
    private int mHeight;
    private URL mImageUrl;
    private URL mPostUrl;
    private DateTime mCreatedTime;

    private ArrayList<Tag> mTags;

    public FacebookPhoto(String id, int order, String name, String uploadedBy, String uploadedById, int width, int height, URL imageUrl, URL postUrl, DateTime createdTime) {
        this.mId = id;
        this.mOrder = order;
        this.mName = name;
        this.mUploadedBy = uploadedBy;
        this.mUploadedById = uploadedById;
        this.mWidth = width;
        this.mHeight = height;
        this.mImageUrl = imageUrl;
        this.mPostUrl = postUrl;
        this.mCreatedTime = createdTime;
    }

    private FacebookPhoto(Parcel in) {
        mId = in.readString();
        mOrder = in.readInt();
        mName = in.readString();
        mUploadedBy = in.readString();
        mUploadedById = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
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
        mTags = in.readArrayList(getClass().getClassLoader());
    }

    public String getId() {
        return "FB_" + mId;
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

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
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

    public ArrayList<Tag> getTags() {
        return mTags;
    }

    public void setTags(ArrayList<Tag> tags) {
        mTags = tags;
    }

    public int getNumPeopleInPhoto() {
        if (mTags != null) {
            return mTags.size();
        } else {
            return 0;
        }
    }

    public PhotoInsights getPhotoInsights() {
        PhotoInsights insights = new PhotoInsights();
        insights.addInsight(PhotoInsights.InsightKey.WIDTH, Integer.toString(getWidth()) + "px");
        insights.addInsight(PhotoInsights.InsightKey.HEIGHT, Integer.toString(getHeight()) + "px");
        insights.addInsight(PhotoInsights.InsightKey.COMMENT, getName());
        insights.addInsight(PhotoInsights.InsightKey.PLACE, getPlaceName());

        if (getCreatedTime() != null) {
            insights.addInsight(PhotoInsights.InsightKey.DATE,
                    PhotoInsights.formatDate(getCreatedTime()));

            insights.addInsight(PhotoInsights.InsightKey.TIME,
                    PhotoInsights.formatTime(getCreatedTime()));
        }

        if (getTags() != null && getTags().size() > 0) {
            StringBuilder people = new StringBuilder();

            for (Tag tag : getTags()) {
                people.append(tag.getName());
                people.append(", ");
            }

            insights.addInsight(PhotoInsights.InsightKey.PEOPLE,
                    people.substring(0, people.length() - 2));
        }

        return insights;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeInt(getOrder());
        dest.writeString(getName());
        dest.writeString(getUploadedBy());
        dest.writeString(getUploadedById());
        dest.writeInt(getWidth());
        dest.writeInt(getHeight());
        dest.writeString(getPlaceName());
        dest.writeString(getImageUrl().toString());
        dest.writeString(getPostUrl().toString());
        dest.writeString(getCreatedTime().toString());

        dest.writeList(mTags);
    }

    public static final Parcelable.Creator<FacebookPhoto> CREATOR = new Parcelable.Creator<FacebookPhoto>() {
        public FacebookPhoto createFromParcel(Parcel in) {
            return new FacebookPhoto(in);
        }

        public FacebookPhoto[] newArray(int size) {
            return new FacebookPhoto[size];
        }
    };
}
