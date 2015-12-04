package net.garrettsites.picturebook.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.services.GetPhotoBitmapService;

/**
 * Created by Garrett on 11/29/2015.
 */
public class GetPhotoBitmapReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public GetPhotoBitmapReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceivePhotoBitmap(int resultCode, String imageFilePath);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            String imageFilePath = resultData.getString(GetPhotoBitmapService.ARG_IMAGE_PATH);
            mReceiver.onReceivePhotoBitmap(resultCode, imageFilePath);
        }
    }
}
