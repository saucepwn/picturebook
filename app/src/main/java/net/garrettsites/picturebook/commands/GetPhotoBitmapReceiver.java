package net.garrettsites.picturebook.commands;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Garrett on 11/29/2015.
 */
public class GetPhotoBitmapReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public GetPhotoBitmapReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, String imageFilePath);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            String imageFilePath = resultData.getString(GetPhotoBitmapService.ARG_IMAGE_PATH);
            mReceiver.onReceiveResult(resultCode, imageFilePath);
        }
    }
}
