package net.garrettsites.picturebook.photoproviders.onedrive;

import android.os.Parcel;
import android.os.Parcelable;

import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.model.PhotoInsights;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;

import org.joda.time.DateTime;

import java.net.URL;

/**
 * Created by Garrett on 6/30/2016.
 */
public class OnedrivePhoto extends Photo {
    private String mCameraMake;
    private String mCameraModel;
    private double mExposureDenominator;
    private double mExposureNumerator;
    private double mFNumber;
    private double mFocalLength;
    private int mIso;
    private DateTime mTakenTime;

    public OnedrivePhoto(String id, int order, String name, int width, int height, URL imageUrl, DateTime createdTime) {
        super(id, order, name, width, height, imageUrl, createdTime);
    }

    private OnedrivePhoto(Parcel in) {
        super(in);
    }

    @Override
    public int getNumPeopleInPhoto() {
        // Onedrive doesn't support tagging.
        return 0;
    }

    @Override
    public void doGetPhotoInsights(PhotoInsights insights) {
        insights.addInsight(PhotoInsights.InsightKey.SOURCE, "OneDrive");

        if (mCameraMake != null && mCameraModel != null)
            insights.addInsight(PhotoInsights.InsightKey.CAMERA, mCameraMake + " " + mCameraModel);
        else if (mCameraMake != null)
            insights.addInsight(PhotoInsights.InsightKey.CAMERA, mCameraMake);
        else if (mCameraModel != null)
            insights.addInsight(PhotoInsights.InsightKey.CAMERA, mCameraModel);

        if (mExposureNumerator != 0 && mExposureDenominator != 0) {
            String exposure = mExposureNumerator+ "/" +  mExposureDenominator;
            insights.addInsight(PhotoInsights.InsightKey.EXPOSURE, exposure);
        }

        if (mFNumber > 0) insights.addInsight(PhotoInsights.InsightKey.F_NUMBER, Double.toString(mFNumber));
        if (mFocalLength > 0) insights.addInsight(PhotoInsights.InsightKey.FOCAL_LENGTH, Double.toString(mFocalLength));
        if (mIso > 0) insights.addInsight(PhotoInsights.InsightKey.ISO, Integer.toString(mIso));

        // TODO: Add taken time to the photo insights. Preferably as one key.
    }

    @Override
    public void doWriteToParcel(Parcel dest) {
        // No onedrive-specific data.
    }

    @Override
    public void doReadFromParcel(Parcel in) {
        // No onedrive-specific data.
    }

    @Override
    public PhotoProvider getProvider() {
        return PhotoProviders.getOnedrivePhotoProvider();
    }

    public static final Parcelable.Creator<OnedrivePhoto> CREATOR = new Parcelable.Creator<OnedrivePhoto>() {
        public OnedrivePhoto createFromParcel(Parcel in) {
            return new OnedrivePhoto(in);
        }

        public OnedrivePhoto[] newArray(int size) {
            return new OnedrivePhoto[size];
        }
    };

    public void setCameraMake(String mCameraMake) {
        this.mCameraMake = mCameraMake;
    }

    public void setCameraModel(String mCameraModel) {
        this.mCameraModel = mCameraModel;
    }

    public void setExposureDenominator(double mExposureDenominator) {
        this.mExposureDenominator = mExposureDenominator;
    }

    public void setExposureNumerator(double mExposureNumerator) {
        this.mExposureNumerator = mExposureNumerator;
    }

    public void setFNumber(double mFNumber) {
        this.mFNumber = mFNumber;
    }

    public void setFocalLength(double mFocalLength) {
        this.mFocalLength = mFocalLength;
    }

    public void setIso(int mIso) {
        this.mIso = mIso;
    }

    public void setTakenTime(DateTime mTakenTime) {
        this.mTakenTime = mTakenTime;
    }
}
