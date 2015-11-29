package net.garrettsites.picturebook.commands;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import net.garrettsites.picturebook.model.Photo;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Garrett on 11/28/2015.
 */
public class GetAllPhotoMetadata extends IntentService {

    public static final String ARG_RECEIVER = "receiverTag";
    public static final String ARG_ALBUM_ID = "albumId";
    public static final String ARG_PHOTOS_METADATA = "photos_metadata";
    private ArrayList<Photo> mAllPhotos = new ArrayList<>();

    public GetAllPhotoMetadata() {
        super(GetAllPhotoMetadata.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(ARG_RECEIVER);
        String albumId = intent.getStringExtra(ARG_ALBUM_ID);

        if (albumId == null)
            throw new IllegalArgumentException("Must pass album ID to GetAllPhotosMetadata.");

        Bundle parameters = new Bundle();
        parameters.putString("fields", "created_time,id,from,images,link,name");

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                String.format(Locale.ENGLISH, "/%s/photos", albumId),
                parameters,
                HttpMethod.GET);

        executeRequestAndAddPhotosToList(request);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_PHOTOS_METADATA, mAllPhotos);
        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void executeRequestAndAddPhotosToList(GraphRequest request) {
        GraphResponse response = request.executeAndWait();

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
                    e.printStackTrace();
                }

                URL postUrl = null;
                try {
                    postUrl = new URL(postUrlStr);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Photo photo = new Photo(id, name, uploadedBy, uploadedById, imageUrl, postUrl, createdTime);

                mAllPhotos.add(photo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If there are additional photos, grab them.
        GraphRequest nextPage = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextPage != null) {
            executeRequestAndAddPhotosToList(nextPage);
        }
    }
}
