package net.garrettsites.picturebook.model;

import android.os.Parcelable;

import org.joda.time.DateTime;

import java.net.URL;

/**
 * Created by Garrett on 3/20/2016.
 */
public interface Photo extends Parcelable {
    /**
     * @return A unique ID for the photo. Used when generating the photo's cached name. It may be
     * modified by the app, so getId() should not be used when calling the photo hosting service.
     */
    String getId();

    /**
     * @return This photo's position in the album it belongs to.
     */
    int getOrder();

    /**
     * @return The name or short description of this photo.
     */
    String getName();

    /**
     * @return The name of the place where this photo was taken.
     */
    String getPlaceName();

    /**
     * @return A URL where this image can be downloaded. The app will use this URL to retrieve the
     * actual photo, so it must return an image and not HTML.
     */
    URL getImageUrl();

    /**
     * @return When this photo was taken. This will be displayed to the user, so ideally the date
     * would reflect when the photo was taken, but most online galleries will only let us retrieve
     * the date the photo was uploaded.
     */
    DateTime getCreatedTime();

    /**
     * @return A key value pair of extra information about the photo. This extra information is
     * displayed if the user taps the photo during a slideshow.
     */
    PhotoInsights getPhotoInsights();

    /**
     * @return The number of people in this photograph. Use 0 if the number of people is unknown.
     */
    int getNumPeopleInPhoto();

    /**
     * @return The service that provides this photo. Ex) "facebook", "onedrive"
     */
    String getProvider();
}
