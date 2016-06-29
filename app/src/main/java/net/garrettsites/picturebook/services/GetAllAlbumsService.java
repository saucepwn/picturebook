package net.garrettsites.picturebook.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllAlbumsService extends IntentService {
    private static final String TAG = GetAllAlbumsService.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();
    private int invocationCode;

    public static final String ARG_CODE = "code";
    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_ALBUM_ARRAY_LIST = "albums";

    public GetAllAlbumsService() {
        super(GetAllAlbumsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);
        invocationCode = intent.getIntExtra(ARG_CODE, -1);

        // Call all of our services here and get all albums from each logged in service.
        PhotoProvider[] photoProviders = PhotoProviders.getAllPhotoProviders();
        ArrayList<Album> allAlbums = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(photoProviders.length);
        Set<Future<ArrayList<Album>>> set = new HashSet<>();

        for (PhotoProvider provider : photoProviders) {
            // Submit the Callables to the executor service and keep a set to track their results.
            set.add(executor.submit(provider.getAllAlbumsCommand()));
        }

        // Await all callables.
        // TODO: Add a timeout here for each provider.
        for (Future<ArrayList<Album>> future : set) {
            try {
                allAlbums.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // We did not get the albums this time. Log, and continue.
                mLogger.trackHandledException(e);
                e.printStackTrace();
            }
        }

        executor.shutdown();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ALBUM_ARRAY_LIST, allAlbums);
        bundle.putInt(ARG_CODE, invocationCode);
        receiver.send(Activity.RESULT_OK, bundle);
    }
}
