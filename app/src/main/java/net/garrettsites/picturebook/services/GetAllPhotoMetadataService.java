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

import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.model.Tag;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Garrett on 11/28/2015.
 */
public class GetAllPhotoMetadataService extends IntentService {
    private static final String TAG = GetAllPhotoMetadataService.class.getName();
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_ALBUM_ID = "albumId";
    public static final String ARG_PHOTOS_METADATA = "photos_metadata";
    private ArrayList<Photo> mAllPhotos = new ArrayList<>();

    public GetAllPhotoMetadataService() {
        super(GetAllPhotoMetadataService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);
        String albumId = intent.getStringExtra(ARG_ALBUM_ID);

        if (albumId == null)
            throw new IllegalArgumentException("Must pass album ID to GetAllPhotosMetadata.");

        Bundle parameters = new Bundle();
        parameters.putString("fields", "created_time,id,from,images,link,name,place,tags");

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                String.format(Locale.ENGLISH, "/%s/photos", albumId),
                parameters,
                HttpMethod.GET);

        executeRequestAndAddPhotosToList(request);

        // This album has no photos, return failure.
        if (mAllPhotos.size() == 0) {
            Log.w(TAG, "Album id " + albumId + " has no photos. Failing.");
            receiver.send(Activity.RESULT_CANCELED, null);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ARG_PHOTOS_METADATA, mAllPhotos);
            receiver.send(Activity.RESULT_OK, bundle);
        }
    }

    private void executeRequestAndAddPhotosToList(GraphRequest request) {
        long start = System.currentTimeMillis();
        GraphResponse response = request.executeAndWait();
        long end = System.currentTimeMillis();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("Path", request.getGraphPath());
        mLogger.trackMetric("FacebookQuery", (double) (end - start), properties);

        try {
            JSONArray photosListJson = response.getJSONObject().getJSONArray("data");
            for (int i = 0; i < photosListJson.length(); i++) {
                JSONObject thisPhoto = photosListJson.getJSONObject(i);

                String id = thisPhoto.getString("id");

                String name = null;
                if (thisPhoto.has("name")) {
                    name = thisPhoto.getString("name");
                }

                String uploadedBy = thisPhoto.getJSONObject("from").getString("name");
                String uploadedById = thisPhoto.getJSONObject("from").getString("id");
                String imageUrlStr = thisPhoto.getJSONArray("images").getJSONObject(0).getString("source");
                String postUrlStr = thisPhoto.getString("link");
                DateTime createdTime = new DateTime(thisPhoto.getString("created_time"));

                URL imageUrl = null;
                try {
                    imageUrl = new URL(imageUrlStr);
                } catch (MalformedURLException e) {
                    mLogger.trackHandledException(e);
                    e.printStackTrace();
                }

                URL postUrl = null;
                try {
                    postUrl = new URL(postUrlStr);
                } catch (MalformedURLException e) {
                    mLogger.trackHandledException(e);
                    e.printStackTrace();
                }

                Photo photo = new Photo(
                        id,
                        mAllPhotos.size() + 1, // order
                        name,
                        uploadedBy,
                        uploadedById,
                        imageUrl,
                        postUrl,
                        createdTime);

                String placeName = null;
                if (thisPhoto.has("place")) {
                    JSONObject placeJsonObj = thisPhoto.getJSONObject("place");
                    if (placeJsonObj.has("name")) {
                        placeName = placeJsonObj.getString("name");
                    }
                }

                if (placeName != null) {
                    photo.setPlaceName(placeName);
                }

                ArrayList<Tag> tags = new ArrayList<>();
                if (thisPhoto.has("tags")) {
                    JSONArray tagsArray = thisPhoto.getJSONObject("tags").getJSONArray("data");

                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject thisTag = tagsArray.getJSONObject(j);

                        // A tag must contain an id and a name to be useful to us.
                        if (thisTag == null || !thisTag.has("id") || !thisTag.has("name")) {
                            Log.v(TAG, "Skipping tag for photo " + id + ". Tag missing info.");
                            continue;
                        }

                        String taggedUserId = thisTag.getString("id");
                        String taggedUserName = thisTag.getString("name");

                        DateTime tagCreated = null;
                        double tagX = -1;
                        double tagY = -1;

                        if (thisTag.has("created_time"))
                            tagCreated = new DateTime(thisTag.getString("created_time"));

                        if (thisTag.has("x"))
                            tagX = thisTag.getDouble("x");

                        if (thisTag.has("y"))
                            tagY = thisTag.getDouble("y");

                        tags.add(new Tag(taggedUserId, taggedUserName, tagCreated, tagX, tagY));
                    }
                }

                photo.setTags(tags);

                mAllPhotos.add(photo);
            }
        } catch (JSONException e) {
            mLogger.trackHandledException(e);
            e.printStackTrace();
        }

        // If there are additional photos, grab them.
        GraphRequest nextPage = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextPage != null) {
            executeRequestAndAddPhotosToList(nextPage);
        }
    }
}
