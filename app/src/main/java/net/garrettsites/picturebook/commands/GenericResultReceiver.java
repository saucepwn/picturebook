package net.garrettsites.picturebook.commands;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ResultReceiver;

import net.garrettsites.picturebook.model.Album;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GenericResultReceiver extends ResultReceiver {
    private Receiver mReceiver;
    private ArrayList<Album> mAlbums;

    public GenericResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, ArrayList<Album> albums);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, mAlbums);
        }
    }
}
