package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Garrett on 3/20/2016.
 */
public abstract class Photo implements Parcelable {
    protected String mId;
    protected int mOrder = 0;
    protected String mName;
    protected int mWidth;
    protected int mHeight;
    protected URL mImageUrl;
    protected String mPlaceName;
    protected DateTime mCreatedTime;

    public Photo(String id, String name, int width, int height, URL imageUrl, DateTime createdTime) {
        mId = id;
        mName = name;
        mWidth = width;
        mHeight = height;
        mImageUrl = imageUrl;
        mCreatedTime = createdTime;
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
        dest.writeInt(getWidth());
        dest.writeInt(getHeight());
        dest.writeString(getPlaceName());
        dest.writeString(getCreatedTime().toString());
        dest.writeString(getImageUrl().toString());

        doWriteToParcel(dest);
    }

    /**
     * Offers subclasses the opportunity to add their custom properties to the parcel. The base
     * Album class's data is added to the parcel first.
     * @param dest The parcel to write data to.
     */
    protected abstract void doWriteToParcel(Parcel dest);

    protected Photo(Parcel in) {
        mId = in.readString();
        mOrder = in.readInt();
        mName = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
        mPlaceName = in.readString();
        mCreatedTime = new DateTime(in.readString());

        try {
            mImageUrl = new URL(in.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        doReadFromParcel(in);
    }

    /**
     * Offers subclasses the opportunity to read their custom properties from the parcel. The base
     * Album class's data is read from the parcel first.
     * @param in The parcel to read from.
     */
    protected abstract void doReadFromParcel(Parcel in);

    /**
     * @return A unique ID for the photo provided by the service hosting the photo.
     */
    public String getId() {
        return mId;
    }

    /**
     * @return This photo's position in the album it belongs to.
     */
    public int getOrder() {
        return mOrder;
    }

    /**
     * @return The name or short description of this photo.
     */
    public String getName() {
        return mName;
    }

    /**
     * @return The name of the place where this photo was taken.
     */
    public String getPlaceName() {
        return mPlaceName;
    }

    /**
     * Set the name of the place this photo was taken.
     * @param placeName The name of the place this photo was taken.
     */
    public void setPlaceName(String placeName) {
        mPlaceName = placeName;
    }

    /**
     * @return A URL where this image can be downloaded. The app will use this URL to retrieve the
     * actual photo, so it must return an image and not HTML.
     */
    public URL getImageUrl() {
        return mImageUrl;
    }

    /**
     * @return The width of the image in pixels.
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * @return The height of the image in pixels.
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * @return When this photo was taken. This will be displayed to the user, so ideally the date
     * would reflect when the photo was taken, but most online galleries will only let us retrieve
     * the date the photo was uploaded.
     */
    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    /**
     * @return The number of people in this photograph. Use 0 if the number of people is unknown.
     */
    public int getNumPeopleInPhoto() {
        return 0;
    }

    /**
     * @return A key value pair of extra information about the photo. This extra information is
     * displayed if the user taps the photo during a slideshow.
     */
    public abstract PhotoInsights getPhotoInsights();

    /**
     * @return The service that provides this photo. Ex) "facebook", "onedrive"
     */
    public abstract String getProvider();
}
