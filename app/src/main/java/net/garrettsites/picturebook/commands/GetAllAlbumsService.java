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

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllAlbumsService extends IntentService {

    public static final String ARG_RECEIVER = "receiverTag";

    private ArrayList<Album> allAlbums = new ArrayList<>();

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
        parameters.putString("limit", "50");

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                parameters,
                HttpMethod.GET);

        executeRequestAndAddAlbumsToList(request);

        receiver.send(Activity.RESULT_OK, new Bundle());
    }

    private void executeRequestAndAddAlbumsToList(GraphRequest request) {
        GraphResponse response = request.executeAndWait();

        try {
            JSONArray albumListJson = response.getJSONObject().getJSONArray("data");
            for (int i = 0; i < albumListJson.length(); i++) {
                JSONObject thisAlbum = albumListJson.getJSONObject(i);

                String type = thisAlbum.getString("type");
                String name = thisAlbum.getString("name");
                String createdTimeStr = thisAlbum.getString("created_time");
                String updatedTimeStr = thisAlbum.getString("updated_time");
                int id = thisAlbum.getInt("id");

                DateTime createdTime = new DateTime(createdTimeStr);
                DateTime updatedTime = new DateTime(updatedTimeStr);

                Album album = new Album(type, name, createdTime, updatedTime, id);

                allAlbums.add(album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If there are additional albums, grab them.
        GraphRequest nextPage = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextPage != null) {
            executeRequestAndAddAlbumsToList(nextPage);
        }
    }

    private ArrayList<Album> getAllAlbums() {
        return new ArrayList<Album>();
    }
}
