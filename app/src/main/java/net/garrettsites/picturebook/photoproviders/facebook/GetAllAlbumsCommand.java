package net.garrettsites.picturebook.photoproviders.facebook;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.ErrorCodes;
import net.garrettsites.picturebook.model.FacebookAlbum;
import net.garrettsites.picturebook.model.PictureBookException;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/28/2016.
 */
public class GetAllAlbumsCommand implements Callable<ArrayList<Album>> {
    private static String TAG = GetAllAlbumsCommand.class.getName();

    private ArrayList<Album> mAllAlbums = new ArrayList<>();

    private AccessToken mAccessToken;
    private TelemetryClient mLogger;

    /**
     * Creates a new instance of a command to get all photo albums from Facebook and injects
     * depencendies.
     * @param logger Logger object.
     * @param accessToken Facebook access token to authenticate Facebook calls.
     */
    public GetAllAlbumsCommand(TelemetryClient logger, AccessToken accessToken) {
        mLogger = logger;
        mAccessToken = accessToken;
    }

    @Override
    public ArrayList<Album> call() throws PictureBookException {
        // Fail if the user is not logged in.
        if (mAccessToken == null) {
            Log.w(TAG, "Tried to start slideshow without logged in Facebook account - aborting.");
            mLogger.trackEvent("WARN: GetAllAlbumsService called without a Facebook account.");
            throw new PictureBookException(ErrorCodes.Error.NO_LOGGED_IN_ACCOUNT);
        }

        Bundle parameters = new Bundle();
        parameters.putString("fields", "type,name,created_time,updated_time,description");
        parameters.putString("limit", "50");

        GraphRequest request = new GraphRequest(
                mAccessToken,
                "/me/albums",
                parameters,
                HttpMethod.GET);

        executeRequestAndAddAlbumsToList(request);
        mLogger.trackMetric("NumAlbums", mAllAlbums.size(), new HashMap<String, String>());

        // Fail with an error code if the user has no albums.
        if (mAllAlbums.size() == 0) {
            Log.w(TAG, "User has no Facebook albums, so we have nothing to display.");
            throw new PictureBookException(ErrorCodes.Error.NO_ALBUMS);
        }

        return mAllAlbums;
    }

    private void executeRequestAndAddAlbumsToList(GraphRequest request) {
        long start = System.currentTimeMillis();
        GraphResponse response = request.executeAndWait();
        long end = System.currentTimeMillis();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("Path", request.getGraphPath());
        mLogger.trackMetric("FacebookQuery", (double) (end - start), properties);

        // Throw any network errors up to the caller.
        if (response.getError() != null) {
            throw response.getError().getException();
        }

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

                FacebookAlbum album =
                        new FacebookAlbum(type, name, description, createdTime, updatedTime, id);

                mAllAlbums.add(album);
            }
        } catch (JSONException e) {
            mLogger.trackHandledException(e);
            Log.d(TAG, "Response object: " + response.toString());
            e.printStackTrace();
        }

        // If there are additional albums, grab them.
        GraphRequest nextPage = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextPage != null) {
            executeRequestAndAddAlbumsToList(nextPage);
        }
    }
}
