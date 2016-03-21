package net.garrettsites.picturebook.receivers;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.services.GetAllFacebookAlbumsService;

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
        void onReceiveAllAlbums(int resultCode, int errorCode, ArrayList<Album> albums);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            if (resultCode == Activity.RESULT_OK) {
                // Deserialize the parceled version of our album array.
                ArrayList<Album> albums =
                        resultData.getParcelableArrayList(GetAllFacebookAlbumsService.ARG_ALBUM_ARRAY_LIST);
                mReceiver.onReceiveAllAlbums(resultCode, -1, albums);
            } else {
                int errorCode = resultData.getInt("ErrorCode");
                mReceiver.onReceiveAllAlbums(resultCode, errorCode, null);
            }
        }
    }
}
