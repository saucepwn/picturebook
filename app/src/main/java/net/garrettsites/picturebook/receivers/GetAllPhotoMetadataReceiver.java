package net.garrettsites.picturebook.receivers;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.services.GetAllFacebookPhotoMetadataService;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllPhotoMetadataReceiver extends ResultReceiver {
    private static String TAG = GetAllPhotoMetadataReceiver.class.getName();

    private Receiver mReceiver;

    public GetAllPhotoMetadataReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveAllPhotoMetadata(int resultCode, ArrayList<Photo> albums);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            if (resultData == null) {
                Log.w(TAG, "resultData is null. Setting resultCode to RESULT_CANCELED.");
                mReceiver.onReceiveAllPhotoMetadata(Activity.RESULT_CANCELED, null);
            } else {
                // Deserialize the parceled version of our album array.
                ArrayList<Photo> photos = resultData.getParcelableArrayList(GetAllFacebookPhotoMetadataService.ARG_PHOTOS_METADATA);
                mReceiver.onReceiveAllPhotoMetadata(resultCode, photos);
            }
        }
    }
}
