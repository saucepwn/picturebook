package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import net.garrettsites.picturebook.photoproviders.PhotoProvider;

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
    protected int mOrigWidth;
    protected int mOrigHeight;
    protected int mWidth = -1;
    protected int mHeight = -1;
    protected URL mImageUrl;
    protected String mPlaceName;
    protected DateTime mCreatedTime;

    public Photo(String id, String name, int origWidth, int origHeight, URL imageUrl, DateTime createdTime) {
        mId = id;
        mName = name;
        mOrigWidth = origWidth;
        mOrigHeight = origHeight;
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
        dest.writeInt(getOrigWidth());
        dest.writeInt(getOrigHeight());
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
        mOrigWidth = in.readInt();
        mOrigHeight = in.readInt();
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
     * @return The width of the image in pixels before it was resized by the app.
     */
    public int getOrigWidth() {
        return mOrigWidth;
    }

    /**
     * @return The height of the image in pixels before it was resized by the app.
     */
    public int getOrigHeight() {
        return mOrigHeight;
    }

    /**
     * Sets the actual width of the image.
     * @param width The actual width of the image.
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * @return The actual width of the image.
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Sets the actual height of the image.
     * @param height The actual height of the image.
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * @return The actual height of the image.
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
    public PhotoInsights getPhotoInsights() {
        PhotoInsights insights = new PhotoInsights();
        insights.addInsight(PhotoInsights.InsightKey.ORIG_WIDTH, Integer.toString(getOrigWidth()) + "px");
        insights.addInsight(PhotoInsights.InsightKey.ORIG_HEIGHT, Integer.toString(getOrigHeight()) + "px");

        if (getWidth() != -1 && getHeight() != -1 &&
                getOrigWidth() != getWidth() && getOrigHeight() != getHeight()) {
            insights.addInsight(PhotoInsights.InsightKey.WIDTH, Integer.toString(getWidth()) + "px");
            insights.addInsight(PhotoInsights.InsightKey.HEIGHT, Integer.toString(getHeight()) + "px");
        }

        insights.addInsight(PhotoInsights.InsightKey.COMMENT, getName());
        insights.addInsight(PhotoInsights.InsightKey.PLACE, getPlaceName());

        if (getCreatedTime() != null) {
            insights.addInsight(PhotoInsights.InsightKey.DATE,
                    PhotoInsights.formatDate(getCreatedTime()));

            insights.addInsight(PhotoInsights.InsightKey.TIME,
                    PhotoInsights.formatTime(getCreatedTime()));
        }

        doGetPhotoInsights(insights);
        return insights;
    }

    /**
     * This method presents an opportunity for a subclass to add additional insights to a photo.
     * Insights are a set of key value pairs containing information about a photo.
     */
    public abstract void doGetPhotoInsights(PhotoInsights insights);

    /**
     * @return The provider associated with this photo.
     */
    public abstract PhotoProvider getProvider();
}
