package net.garrettsites.picturebook.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.services.GetAllAlbumsService;
import net.garrettsites.picturebook.model.Album;

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
        void onReceiveAllAlbums(int resultCode, ArrayList<Album> albums);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            // Deserialize the parceled version of our album array.
            ArrayList<Album> albums = resultData.getParcelableArrayList(GetAllAlbumsService.ARG_ALBUM_ARRAY_LIST);
            mReceiver.onReceiveAllAlbums(resultCode, albums);
        }
    }
}
