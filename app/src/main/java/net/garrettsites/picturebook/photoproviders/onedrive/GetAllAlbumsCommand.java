package net.garrettsites.picturebook.photoproviders.onedrive;

import com.microsoft.applicationinsights.library.TelemetryClient;
import com.onedrive.sdk.extensions.IItemCollectionPage;
import com.onedrive.sdk.extensions.IItemCollectionRequestBuilder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;

import net.garrettsites.picturebook.model.Album;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Garrett on 6/29/2016.
 */
public class GetAllAlbumsCommand implements Callable<ArrayList<Album>> {
    private static String TAG = GetAllAlbumsCommand.class.getName();

    private TelemetryClient mLogger;
    private IOneDriveClient mOnedriveClient;

    public GetAllAlbumsCommand(TelemetryClient logger, IOneDriveClient onedriveClient) {
        mLogger = logger;
        mOnedriveClient = onedriveClient;
    }

    @Override
    public ArrayList<Album> call() throws Exception {
        ArrayList<Album> allAlbums = new ArrayList<>();

        IItemCollectionRequestBuilder requestBuilder = mOnedriveClient.getDrive().getSpecial("photos").getChildren();
        IItemCollectionPage response;

        while (requestBuilder != null) {
            response = requestBuilder.buildRequest().get();

            for (Item item : response.getCurrentPage()) {
                // Skip non-folders or folders that do not contain any items.
                if (item.folder == null || item.folder.childCount == 0) continue;

                String id = item.id;
                String description = item.description;
                String albumName = item.name;
                int numPhotos = item.folder.childCount; // TODO: Plumb this through to the UI and also for Facebook.

                DateTime lastModified = new DateTime(item.lastModifiedDateTime.getTimeInMillis());
                DateTime created = new DateTime(item.createdDateTime.getTimeInMillis());

                allAlbums.add(new OnedriveAlbum(albumName, description, created, lastModified, id));
            }

            requestBuilder = response.getNextPage();
        }

        return allAlbums;
    }
}
