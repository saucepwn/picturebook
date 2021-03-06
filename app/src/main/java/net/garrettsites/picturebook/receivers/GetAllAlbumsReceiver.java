package net.garrettsites.picturebook.receivers;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.ErrorCodes;
import net.garrettsites.picturebook.services.GetAllAlbumsService;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllAlbumsReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public GetAllAlbumsReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveAllAlbums(int invocationCode, ArrayList<Album> albums);
        void onReceiveAllAlbumsError(int invocationCode, int errorCode, Throwable exception);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        int invocationCode = resultData.getInt(GetAllAlbumsService.ARG_CODE);

        if (mReceiver != null) {
            if (resultCode == Activity.RESULT_OK) {
                // Deserialize the parceled version of our album array.
                ArrayList<Album> albums =
                        resultData.getParcelableArrayList(GetAllAlbumsService.ARG_ALBUM_ARRAY_LIST);
                mReceiver.onReceiveAllAlbums(invocationCode, albums);
            } else {
                int errorCode = resultData.getInt(ErrorCodes.BUNDLE_ERROR_CODE_ARG);
                Throwable exception =
                        (Throwable) resultData.getSerializable(ErrorCodes.BUNDLE_EXCEPTION_ARG);

                mReceiver.onReceiveAllAlbumsError(invocationCode, errorCode, exception);
            }
        }
    }
}
