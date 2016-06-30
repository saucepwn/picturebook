package net.garrettsites.picturebook.photoproviders.facebook;

import android.os.Parcel;
import android.os.Parcelable;

import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.model.PhotoInsights;
import net.garrettsites.picturebook.model.Tag;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Garrett on 11/28/2015.
 */
public class FacebookPhoto extends Photo {
    private String mUploadedBy;
    private String mUploadedById;
    private URL mPostUrl;

    private ArrayList<Tag> mTags;

    public FacebookPhoto(String id, int order, String name, String uploadedBy, String uploadedById, int width, int height, URL imageUrl, URL postUrl, DateTime createdTime) {
        super(id, name, width, height, imageUrl, createdTime);

        this.mOrder = order;
        this.mUploadedBy = uploadedBy;
        this.mUploadedById = uploadedById;
        this.mPostUrl = postUrl;
    }

    private FacebookPhoto(Parcel in) {
        super(in);
    }

    public String getUploadedBy() {
        return mUploadedBy;
    }

    public String getUploadedById() {
        return mUploadedById;
    }

    public URL getPostUrl() {
        return mPostUrl;
    }

    public ArrayList<Tag> getTags() {
        return mTags;
    }

    public void setTags(ArrayList<Tag> tags) {
        mTags = tags;
    }

    @Override
    public int getNumPeopleInPhoto() {
        if (mTags == null) {
            return 0;
        } else {
            return mTags.size();
        }
    }

    @Override
    public void doGetPhotoInsights(PhotoInsights insights) {
        insights.addInsight(PhotoInsights.InsightKey.SOURCE, "Facebook");

        if (getTags() != null && getTags().size() > 0) {
            StringBuilder people = new StringBuilder();

            for (Tag tag : getTags()) {
                people.append(tag.getName());
                people.append(", ");
            }

            insights.addInsight(PhotoInsights.InsightKey.PEOPLE,
                    people.substring(0, people.length() - 2));
        }
    }

    @Override
    public void doWriteToParcel(Parcel dest) {
        dest.writeString(getUploadedBy());
        dest.writeString(getUploadedById());
        dest.writeString(getPostUrl().toString());

        dest.writeList(mTags);
    }

    @Override
    public void doReadFromParcel(Parcel in) {
        mUploadedBy = in.readString();
        mUploadedById = in.readString();

        try {
            mPostUrl = new URL(in.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mTags = in.readArrayList(getClass().getClassLoader());
    }

    @Override
    public String getProvider() {
        return "FB";
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
