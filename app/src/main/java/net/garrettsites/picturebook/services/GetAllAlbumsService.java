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

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.ErrorCodes;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Garrett on 11/20/2015.
 */
public class GetAllAlbumsService extends IntentService {
    private static final String TAG = GetAllAlbumsService.class.getName();

    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_ALBUM_ARRAY_LIST = "albums";
    private ArrayList<Album> allAlbums = new ArrayList<>();

    public GetAllAlbumsService() {
        super(GetAllAlbumsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);

        if (AccessToken.getCurrentAccessToken() == null) {
            Log.w(TAG, "Tried to start slideshow without logged in Facebook account - aborting.");
            Bundle errorBundle = new Bundle();
            errorBundle.putInt("ErrorCode", ErrorCodes.Error.NO_LOGGED_IN_ACCOUNT.ordinal());

            receiver.send(Activity.RESULT_CANCELED, new Bundle());
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

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ALBUM_ARRAY_LIST, allAlbums);
        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void executeRequestAndAddAlbumsToList(GraphRequest request) {
        GraphResponse response = request.executeAndWait();

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
            e.printStackTrace();
        }

        // If there are additional albums, grab them.
        GraphRequest nextPage = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextPage != null) {
            executeRequestAndAddAlbumsToList(nextPage);
        }
    }
}
