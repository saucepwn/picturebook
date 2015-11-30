package net.garrettsites.picturebook.commands;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.model.Photo;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class AllPhotosMetadataResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public AllPhotosMetadataResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, ArrayList<Photo> albums);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            // Deserialize the parceled version of our album array.
            ArrayList<Photo> photos = resultData.getParcelableArrayList(GetAllPhotoMetadataService.ARG_PHOTOS_METADATA);
            mReceiver.onReceiveResult(resultCode, photos);
        }
    }
}
