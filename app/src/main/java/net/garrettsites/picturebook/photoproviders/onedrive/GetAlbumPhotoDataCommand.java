package net.garrettsites.picturebook.photoproviders.onedrive;

import com.microsoft.applicationinsights.library.TelemetryClient;
import com.onedrive.sdk.extensions.IItemCollectionPage;
import com.onedrive.sdk.extensions.IItemCollectionRequestBuilder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;

import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Garrett on 6/30/2016.
 */
public class GetAlbumPhotoDataCommand {
    private static final String TAG = GetAlbumPhotoDataCommand.class.getName();

    private TelemetryClient mLogger;
    private IOneDriveClient mOnedriveClient;

    public GetAlbumPhotoDataCommand(TelemetryClient logger, IOneDriveClient onedriveClient) {
        mLogger = logger;
        mOnedriveClient = onedriveClient;
    }

    /**
     * Retrieves photo data for a given album.
     * @param album The album to retrieve photo data for.
     * @return An Album populated with the metadata of all its photos. Retrieve using .getPhotos()
     */
    public Album execute(OnedriveAlbum album) {
        ArrayList<Photo> allPhotos = new ArrayList<>();

        IItemCollectionRequestBuilder requestBuilder = mOnedriveClient.getDrive().getItems(album.getId()).getChildren();
        IItemCollectionPage response;

        while (requestBuilder != null) {
            response = requestBuilder.buildRequest().get();

            for (Item photo : response.getCurrentPage()) {
                // Pass on any items that are not photos.
                if (photo.image == null) continue;

                String name = photo.name;

                String[] nameTokenized = name.split("\\.");
                String fileExtensionLower = nameTokenized[nameTokenized.length - 1].toLowerCase();
                if (!fileExtensionLower.equals("jpg") && !fileExtensionLower.equals("jpeg"))
                    continue;

                // Basic photo information
                String id = photo.id;
                int width = photo.image.width;
                int height = photo.image.height;
                DateTime createdTime = new DateTime(photo.createdDateTime.getTimeInMillis());

                URL imageUrl;
                try {
                    imageUrl = new URL(photo.webUrl);
                } catch (MalformedURLException e) {
                    // Skip this image if it has a malformed URL.
                    mLogger.trackHandledException(e);
                    continue;
                }

                OnedrivePhoto finishedPhoto = new OnedrivePhoto(
                        id,
                        allPhotos.size() + 1,
                        null,
                        width,
                        height,
                        imageUrl,
                        createdTime);

                // Extended photo information
                if (photo.photo != null) {
                    finishedPhoto.setCameraMake(photo.photo.cameraMake);
                    finishedPhoto.setCameraModel(photo.photo.cameraModel);

                    if (photo.photo.exposureDenominator != null) {
                        finishedPhoto.setExposureDenominator(photo.photo.exposureDenominator);
                    }

                    if (photo.photo.exposureNumerator != null) {
                        finishedPhoto.setExposureNumerator(photo.photo.exposureNumerator);
                    }

                    if (photo.photo.fNumber != null) {
                        finishedPhoto.setFNumber(photo.photo.fNumber);
                    }

                    if (photo.photo.focalLength != null) {
                        finishedPhoto.setFocalLength(photo.photo.focalLength);
                    }

                    if (photo.photo.iso != null) {
                        finishedPhoto.setIso(photo.photo.iso);
                    }

                    if (photo.photo.takenDateTime != null) {
                        finishedPhoto.setTakenTime(new DateTime(photo.photo.takenDateTime.getTimeInMillis()));
                    }
                }

                allPhotos.add(finishedPhoto);
            }

            requestBuilder = response.getNextPage();
        }

        album.setPhotos(allPhotos);
        return album;
    }
}
