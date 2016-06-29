package net.garrettsites.picturebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import net.garrettsites.picturebook.photoproviders.PhotoProvider;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Garrett on 3/20/2016.
 */
public abstract class Album implements Parcelable {

    protected String mName;
    protected String mDescription;
    protected DateTime mCreatedTime;
    protected DateTime mUpdatedTime;
    protected String mId;

    protected ArrayList<Photo> mPhotos;

    public Album(String name, String description, DateTime createdTime, DateTime updatedTime, String id) {
        mName = name;
        mDescription = description;
        mCreatedTime = createdTime;
        mUpdatedTime = updatedTime;
        mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mCreatedTime.toString());
        dest.writeString(mUpdatedTime.toString());
        dest.writeString(mId);
        dest.writeList(mPhotos);

        doWriteToParcel(dest);
    }

    /**
     * Offers subclasses the opportunity to add their custom properties to the parcel. The base
     * Album class's data is added to the parcel first.
     * @param dest The parcel to write data to.
     */
    protected abstract void doWriteToParcel(Parcel dest);

    protected Album(Parcel in) {
        mName = in.readString();
        mDescription = in.readString();
        mCreatedTime = new DateTime(in.readString());
        mUpdatedTime = new DateTime(in.readString());
        mId = in.readString();

        mPhotos = in.readArrayList(getClass().getClassLoader());

        doReadFromParcel(in);
    }

    /**
     * Offers subclasses the opportunity to read their custom properties from the parcel. The base
     * Album class's data is read from the parcel first.
     * @param in The parcel to read from.
     */
    protected abstract void doReadFromParcel(Parcel in);

    /**
     * @return The PhotoProvider that retrieved this album.
     */
    public abstract PhotoProvider getPhotoProvider();

    /**
     * @return The album's name.
     */
    public String getName() {
        return mName;
    }

    /**
     * @return A short description of the album.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return The date the album was created.
     */
    public DateTime getCreatedTime() {
        return mCreatedTime;
    }

    /**
     * @return The date the album was last updated.
     */
    public DateTime getUpdatedTime() {
        return mUpdatedTime;
    }

    /**
     * @return A unique ID referencing this album.
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets this album's photos.
     * @param photos The photos to add to this album.
     */
    public void setPhotos(ArrayList<Photo> photos) {
        mPhotos = photos;
    }

    /**
     * @return The photos in this album.
     */
    public ArrayList<Photo> getPhotos() {
        return mPhotos;
    }
}
