package net.garrettsites.picturebook.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.garrettsites.picturebook.activities.ViewSlideshowActivity;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.receivers.GetAllAlbumsReceiver;
import net.garrettsites.picturebook.receivers.GetAllPhotoMetadataReceiver;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;
import net.garrettsites.picturebook.util.ChooseRandomAlbum;

import java.util.ArrayList;

/**
 * This Receiver will do everything required to acquire a "random" album from Facebook, get all of
 * the photos, and start the ViewSlideshowActivity with the album. All of the below methods are
 * called IN ORDER. Please keep that in mind when editing this file.
 */
public class GetAlbumAndStartSlideshowService extends IntentService implements
        GetAllAlbumsReceiver.Receiver, GetAllPhotoMetadataReceiver.Receiver {
    private static final String TAG = GetAlbumAndStartSlideshowService.class.getName();

    private Intent mCallingIntent;
    private Context mContext;
    private Album mAlbum;

    public GetAlbumAndStartSlideshowService() {
        super(GetAlbumAndStartSlideshowService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Starting service");
        mCallingIntent = intent;
        mContext = getApplicationContext();
        mAlbum = null;

        beginProcess();
    }

    /**
     * Must be called to start this service.
     */
    private void beginProcess() {
        // Set up a looper so this thread doesn't die while it's waiting for its receivers.
        if (Looper.myLooper() == null) {
            Log.v(TAG, "Preparing looper");
            Looper.prepare();
        }

        // Step 1: Get the metadata for all of this user's Facebook albums.
        callGetAllAlbumsService();
    }

    private void callGetAllAlbumsService() {
        GetAllAlbumsReceiver getAllAlbumsReceiver = new GetAllAlbumsReceiver(new Handler());
        getAllAlbumsReceiver.setReceiver(this);

        Intent getAllAlbumsIntent = new Intent(mContext, GetAllAlbumsService.class);
        getAllAlbumsIntent.putExtra(GetAllAlbumsService.ARG_RECEIVER, getAllAlbumsReceiver);

        Log.v(TAG, "Calling GetAllAlbumsService");
        mContext.startService(getAllAlbumsIntent);

        Log.v(TAG, "Starting looper");
        Looper.loop();
    }

    @Override
    public void onReceiveAllAlbums(int resultCode, ArrayList<Album> albums) {
        Log.v(TAG, "Got results from GetAllAlbumsService");
        ChooseRandomAlbum albumRandomizer = new ChooseRandomAlbum(albums);
        mAlbum = albumRandomizer.selectRandomAlbum();

        // Step 2: Get the photo metadata for all of the photos in this album.
        callGetAllPhotoMetadataService();
    }

    private void callGetAllPhotoMetadataService() {
        GetAllPhotoMetadataReceiver allPhotosReceiver = new GetAllPhotoMetadataReceiver(new Handler());
        allPhotosReceiver.setReceiver(this);

        Intent getAllPhotoMetadataIntent = new Intent(mContext, GetAllPhotoMetadataService.class);
        getAllPhotoMetadataIntent.putExtra(GetAllPhotoMetadataService.ARG_RECEIVER, allPhotosReceiver);
        getAllPhotoMetadataIntent.putExtra(GetAllPhotoMetadataService.ARG_ALBUM_ID, mAlbum.getId());

        Log.v(TAG, "Calling GetAllPhotoMetadataService");
        mContext.startService(getAllPhotoMetadataIntent);
    }

    @Override
    public void onReceiveAllPhotoMetadata(int resultCode, ArrayList<Photo> photos) {
        Log.v(TAG, "Got results from GetAllPhotoMetadataService");
        mAlbum.setPhotos(photos);

        // Step 3: Start the ViewSlideshowActivity with the Album object we've just created.
        Intent viewSlideshowActivity = new Intent(mContext, ViewSlideshowActivity.class);
        viewSlideshowActivity.putExtra(ViewSlideshowActivity.ARG_ALBUM, mAlbum);
        viewSlideshowActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.i(TAG, "Starting ViewSlideshowActivity");
        startActivity(viewSlideshowActivity);

        endProcess();
    }

    /**
     * Must be called when this service is completed.
     */
    private void endProcess() {
        Looper.myLooper().quit();
        StartSlideshowBroadcastReceiver.completeWakefulIntent(mCallingIntent);
    }
}
