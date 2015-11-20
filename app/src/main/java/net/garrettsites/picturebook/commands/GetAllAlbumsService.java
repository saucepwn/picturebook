package net.garrettsites.picturebook.commands;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import net.garrettsites.picturebook.model.Album;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllAlbumsService extends IntentService {

    public static final String ARG_RECEIVER = "receiverTag";

    public GetAllAlbumsService() {
        super(GetAllAlbumsService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "type,name,created_time,updated_time");
        parameters.putString("limit", "999");

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                parameters,
                HttpMethod.GET);

        GraphResponse response = request.executeAndWait();

        // TODO: Convert GraphResponse into an ArrayList of Albums.

        receiver.send(Activity.RESULT_OK, new Bundle());
    }

    private ArrayList<Album> getAllAlbums() {
        return new ArrayList<Album>();
    }
}
