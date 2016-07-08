package net.garrettsites.picturebook.photoproviders.onedrive;

import android.os.Parcel;
import android.os.Parcelable;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;

import org.joda.time.DateTime;

/**
 * Created by Garrett on 6/29/2016.
 */
public class OnedriveAlbum extends Album {
    public OnedriveAlbum(String name, String description, DateTime createdTime, DateTime updatedTime, String id) {
        super(name, description, createdTime, updatedTime, id);
    }

    @Override
    public DateTime getAlbumDate() {
        // Onedrive's most accurate representation of when an album occurred is the created date.
        return mCreatedTime;
    }

    @Override
    protected void doWriteToParcel(Parcel dest) {
        // No onedrive specific properties.
    }

    @Override
    protected void doReadFromParcel(Parcel in) {
        // No onedrive specific properties.
    }

    @Override
    public PhotoProvider getPhotoProvider() {
        return PhotoProviders.getOnedrivePhotoProvider();
    }

    private OnedriveAlbum(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<OnedriveAlbum> CREATOR = new Parcelable.Creator<OnedriveAlbum>() {
        public OnedriveAlbum createFromParcel(Parcel in) {
            return new OnedriveAlbum(in);
        }

        public OnedriveAlbum[] newArray(int size) {
            return new OnedriveAlbum[size];
        }
    };
}
