package net.garrettsites.picturebook.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.ErrorCodes;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllFacebookAlbumsService extends IntentService {
    private static final String TAG = GetAllFacebookAlbumsService.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_ALBUM_ARRAY_LIST = "albums";
    private ArrayList<Album> allAlbums = new ArrayList<>();

    public GetAllFacebookAlbumsService() {
        super(GetAllFacebookAlbumsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);

        // Fail with an error code if the user is not logged in.
        if (AccessToken.getCurrentAccessToken() == null) {
            Log.w(TAG, "Tried to start slideshow without logged in Facebook account - aborting.");
            mLogger.trackEvent("WARN: GetAllFacebookAlbumsService called without a Facebook account.");

            Bundle errorBundle = new Bundle();
            errorBundle.putInt("ErrorCode", ErrorCodes.Error.NO_LOGGED_IN_ACCOUNT.ordinal());

            receiver.send(Activity.RESULT_CANCELED, errorBundle);
            return;
        }

        Bundle parameters = new Bundle();
        parameters.putString("fields", "type,name,created_time,updated_time,description");
        parameters.putString("limit", "50");

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                parameters,
                HttpMethod.GET);

        executeRequestAndAddAlbumsToList(request);
        mLogger.trackMetric("NumAlbums", allAlbums.size(), new HashMap<String, String>());

        // Fail with an error code if the user has no albums.
        if (allAlbums.size() == 0) {
            Log.w(TAG, "User has no albums, so we have nothing to display. Aborting.");

            Bundle errorBundle = new Bundle();
            errorBundle.putInt("ErrorCode", ErrorCodes.Error.NO_ALBUMS.ordinal());

            receiver.send(Activity.RESULT_CANCELED, errorBundle);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ALBUM_ARRAY_LIST, allAlbums);
        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void executeRequestAndAddAlbumsToList(GraphRequest request) {
        long start = System.currentTimeMillis();
        GraphResponse response = request.executeAndWait();
        long end = System.currentTimeMillis();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("Path", request.getGraphPath());
        mLogger.trackMetric("FacebookQuery", (double) (end - start), properties);

        try {
            JSONArray albumListJson = response.getJSONObject().getJSONArray("data");
            for (int i = 0; i < albumListJson.length(); i++) {
                JSONObject thisAlbum = albumListJson.getJSONObject(i);

                String type = thisAlbum.getString("type");
                String name = thisAlbum.getString("name");
                DateTime createdTime = new DateTime(thisAlbum.getString("created_time"));
                DateTime updatedTime = new DateTime(thisAlbum.getString("updated_time"));
                String id = thisAlbum.getString("id");

                String description = null;
                if (thisAlbum.has("description")) {
                    description = thisAlbum.getString("description");
                }

                Album album = new Album(type, name, description, createdTime, updatedTime, id);

                allAlbums.add(album);
            }
        } catch (JSONException e) {
            mLogger.trackHandledException(e);
            e.printStackTrace();
        }

        // If there are additional albums, grab them.
        GraphRequest nextPage = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextPage != null) {
            executeRequestAndAddAlbumsToList(nextPage);
        }
    }
}
