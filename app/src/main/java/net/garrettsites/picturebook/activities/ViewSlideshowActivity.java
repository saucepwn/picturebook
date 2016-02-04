package net.garrettsites.picturebook.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.PicturebookApplication;
import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.ErrorCodes;
import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.receivers.GetAllAlbumsReceiver;
import net.garrettsites.picturebook.receivers.GetAllPhotoMetadataReceiver;
import net.garrettsites.picturebook.receivers.GetPhotoBitmapReceiver;
import net.garrettsites.picturebook.services.GetAllAlbumsService;
import net.garrettsites.picturebook.services.GetAllPhotoMetadataService;
import net.garrettsites.picturebook.services.GetPhotoBitmapService;
import net.garrettsites.picturebook.util.ChooseRandomAlbum;
import net.garrettsites.picturebook.util.PhotoOrder;
import net.garrettsites.picturebook.util.PhotoTagsTransitionGenerator;
import net.garrettsites.picturebook.util.RandomPhotoOrder;
import net.garrettsites.picturebook.util.SequentialPhotoOrder;
import net.garrettsites.picturebook.util.Sleepitizer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Garrett on 11/29/2015.
 */
public class ViewSlideshowActivity extends Activity implements
        Runnable,
        GetPhotoBitmapReceiver.Receiver,
        GetAllAlbumsReceiver.Receiver,
        GetAllPhotoMetadataReceiver.Receiver {

    private static final int ALBUM_TITLE_MAX_CHARS = 40;
    private static final int SPLASH_SCREEN_FADE_OUT_MS = 300;
    private static final int PHOTO_TRANSITION_MS = 750;
    private static final String TAG = ViewSlideshowActivity.class.getName();

    private Album mAlbum = null;
    private PhotoOrder mPhotoOrder;
    private Photo mNextPhoto;
    private Photo mThisPhoto;

    private KenBurnsView mActiveKenBurnsView;
    private KenBurnsView mBackgroundKenBurnsView;

    private Sleepitizer mSleeper = new Sleepitizer();
    private Handler mHandler = new Handler();
    private GetPhotoBitmapReceiver mPhotoBitmapReceiver = new GetPhotoBitmapReceiver(mHandler);

    private UserPreferences mUserPreferences;
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    public static final String ARG_ALBUM = "album";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slideshow);

        // Wake the device up when this activity starts & prevent sleep.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Hook up the receiver from the GetPhotoBitmap service.
        mPhotoBitmapReceiver = new GetPhotoBitmapReceiver(mHandler);
        mPhotoBitmapReceiver.setReceiver(this);

        mActiveKenBurnsView = (KenBurnsView) findViewById(R.id.image_viewport_1);
        mBackgroundKenBurnsView = (KenBurnsView) findViewById(R.id.image_viewport_2);

        mUserPreferences = ((PicturebookApplication) getApplication()).preferences;

        // If an album was passed to this activity, display it. Otherwise, choose a random album
        // to show.
        Album albumToShow = getIntent().getParcelableExtra(ARG_ALBUM);
        if (albumToShow != null) {
            // Display the album that we've been passed.
            mAlbum = albumToShow;

            HashMap<String, String> properties = new HashMap<>();
            properties.put("AlbumId", mAlbum.getId());
            properties.put("AlbumName", mAlbum.getName());
            mLogger.trackTrace("Displaying specific album.", properties);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Hide the navigation bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        if (mAlbum == null) {
            // If we have no album at all, get a random album then populate its photos.
            beginRetrieveAlbumSequence();
        } else if (mAlbum.getPhotos() == null) {
            // If we have an Album object but no photos, call getAllPhotoMetadataService to populate
            // the photos.
            callGetAllPhotoMetadataService();
        } else {
            // If we have an Album object with photos, begin displaying photos.
            run();
        }

        if (mUserPreferences.isSleeperWakerEnabled()) {
            mSleeper.setDailySleepTime(
                    mUserPreferences.getSleepTimeHour(), mUserPreferences.getSleepTimeMinute());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(this);
    }

    @Override
    public void run() {
        // Check if we should put the device to sleep (by finishing the activity).
        if (mUserPreferences.isSleeperWakerEnabled() && mSleeper.timeToSleep()) {
            mLogger.trackEvent("Sleeper finishing ViewSlideshowActivity");
            mHandler.removeCallbacks(this);
            finish();
        } else {
            beginLoadNewPhoto();
        }
    }

    /**
     * Hides the splash screen if it's visible.
     */
    private void hideSplashScreenIfVisible() {
        final View splashScreen = findViewById(R.id.photo_splash_screen);

        if (splashScreen.getVisibility() == View.VISIBLE) {
            splashScreen.animate()
                    .alpha(0f)
                    .setDuration(SPLASH_SCREEN_FADE_OUT_MS)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            splashScreen.setVisibility(View.GONE);
                        }
            });
        }
    }

    /**
     * Given a Period representing the time between when a photo was taken and now, this method
     * formats a string to show the user the amount of time that's elapsed during the Period.
     * @param from The beginning of the time span.
     * @param to The end of the time span.
     * @return A formatted string for the UI.
     */
    private String formatTimeSincePhotoCreated(DateTime from, DateTime to) {
        Resources r = getResources();

        int years = Math.abs(to.get(DateTimeFieldType.year()) - from.get(DateTimeFieldType.year()));
        if (years > 0) {
            return r.getQuantityString(R.plurals.var_years_ago, years, years);
        }

        Period period = new Period(from, to);
        if (period.getMonths() != 0) {
            int months = period.getMonths();
            return r.getQuantityString(R.plurals.var_months_ago, months, months);

        } else if (period.getWeeks() != 0) {
            int weeks = period.getWeeks();
            return r.getQuantityString(R.plurals.var_weeks_ago, weeks, weeks);

        } else if (period.getDays() != 0) {
            int days = period.getDays();
            return r.getQuantityString(R.plurals.var_days_ago, days, days);

        } else {
            return getString(R.string.just_now);
        }
    }

    /**
     * From here until the below comment, the following methods should be called IN ORDER to
     * retrieve the photo album.
     */
    protected void beginRetrieveAlbumSequence() {
        // Step 1: Get the metadata for all of this user's Facebook albums.
        Log.v(TAG, "Starting retrieve album sequence");
        callGetAllAlbumsService();
    }

    private void callGetAllAlbumsService() {
        ((TextView) findViewById(R.id.splash_screen_loading_details)).setText(R.string.getting_all_albums);
        GetAllAlbumsReceiver getAllAlbumsReceiver = new GetAllAlbumsReceiver(mHandler);
        getAllAlbumsReceiver.setReceiver(this);

        Intent getAllAlbumsIntent = new Intent(this, GetAllAlbumsService.class);
        getAllAlbumsIntent.putExtra(GetAllAlbumsService.ARG_RECEIVER, getAllAlbumsReceiver);

        Log.v(TAG, "Calling GetAllAlbumsService");
        startService(getAllAlbumsIntent);
    }

    @Override
    public void onReceiveAllAlbums(int resultCode, int errorCode, ArrayList<Album> albums) {
        if (resultCode == Activity.RESULT_OK) {
            Log.v(TAG, "Got results from GetAllAlbumsService");
            ChooseRandomAlbum albumRandomizer = new ChooseRandomAlbum(albums);

            mAlbum = albumRandomizer.selectRandomAlbum();

            HashMap<String, String> properties = new HashMap<>();
            properties.put("AlbumId", mAlbum.getId());
            properties.put("AlbumName", mAlbum.getName());
            mLogger.trackTrace("Displaying random album.", properties);

            // Get the photo metadata for all of the photos in this album.
            callGetAllPhotoMetadataService();
        } else {
            // Show the user an error if we received an error code.
            int errorStringResourceId = ErrorCodes.getLocalizedErrorStringResource(errorCode);
            String errorString = getResources().getString(errorStringResourceId);
            mLogger.trackManagedException("ReceiveAllAlbumsError", errorString, "", false);

            final Activity self = this;
            new AlertDialog.Builder(this).setTitle(R.string.error)
                    .setMessage(errorString)
                    .setIcon(android.R.drawable.stat_notify_error)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            self.finish();
                        }
                    }).show();
        }
    }

    private void callGetAllPhotoMetadataService() {
        String albumName = getResources().getString(R.string.getting_photo_details, mAlbum.getName());
        ((TextView) findViewById(R.id.splash_screen_loading_details)).setText(albumName);

        GetAllPhotoMetadataReceiver allPhotosReceiver = new GetAllPhotoMetadataReceiver(mHandler);
        allPhotosReceiver.setReceiver(this);

        Intent getAllPhotoMetadataIntent = new Intent(this, GetAllPhotoMetadataService.class);
        getAllPhotoMetadataIntent.putExtra(GetAllPhotoMetadataService.ARG_RECEIVER, allPhotosReceiver);
        getAllPhotoMetadataIntent.putExtra(GetAllPhotoMetadataService.ARG_ALBUM_ID, mAlbum.getId());

        Log.v(TAG, "Calling GetAllPhotoMetadataService");
        startService(getAllPhotoMetadataIntent);
    }

    @Override
    public void onReceiveAllPhotoMetadata(int resultCode, ArrayList<Photo> photos) {
        Log.v(TAG, "Got results from GetAllPhotoMetadataService");

        if (resultCode != Activity.RESULT_OK) {
            String errorStr = "Error retrieving photo metadata for album: " + mAlbum.getName() +
                    ". Randomly choosing another album.";

            mLogger.trackManagedException("ReceiveAllPhotoMetadataError", errorStr, "", true);
            Log.e(TAG, errorStr);

            beginRetrieveAlbumSequence();
            return;
        }

        mAlbum.setPhotos(photos);

        // Create the ordering scheme for the photos.
        if (mUserPreferences.getRandomizePhotoOrder()) {
            mPhotoOrder = new RandomPhotoOrder(mAlbum.getPhotos().size());
        } else {
            mPhotoOrder = new SequentialPhotoOrder(mAlbum.getPhotos().size());
        }

        Log.i(TAG, "Retrieve Album sequence complete.");
        writeAlbumDataToUi();
    }

    private void writeAlbumDataToUi() {
        // Populate UI elements with data from the album.
        CharSequence albumName = mAlbum.getName();
        if (albumName.length() >= ALBUM_TITLE_MAX_CHARS) {
            albumName = albumName.subSequence(0, ALBUM_TITLE_MAX_CHARS - 1) + "…";
        }
        ((TextView) findViewById(R.id.photo_album_name)).setText(albumName);

        beginLoadNewPhoto();
    }

    /**
     * Begin the process of displaying a new photo. This calls GetPhotoBitmapService which will
     * either acquire the photo from Facebook (using the internet) or locally from cache. The act
     * of retrieving the photo happens on a background thread.
     */
    private void beginLoadNewPhoto() {
        mNextPhoto = mAlbum.getPhotos().get(mPhotoOrder.getNextPhotoIdx());
        Log.v(TAG, "Begin load new photo ID: " + mNextPhoto.getId());

        Intent getBitmapIntent = new Intent(this, GetPhotoBitmapService.class);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_PHOTO_OBJ, mNextPhoto);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_RECEIVER, mPhotoBitmapReceiver);
        startService(getBitmapIntent);
        Log.v(TAG, "End loading photo: " + mNextPhoto.getId());
    }

    @Override
    public void onReceivePhotoBitmap(int resultCode, String imageFilePath) {
        // Only update the photo if the last operation completed successfully. If it didn't, try it
        // again.
        if (resultCode == Activity.RESULT_OK) {
            mThisPhoto = mNextPhoto;
            mNextPhoto = null;

            hideSplashScreenIfVisible();

            // Show the image we've just retrieved.
            setupKenBurnsTransition(imageFilePath);

            // Populate the UI with additional photo information.
            populateUiWithPhotoInfo(mThisPhoto);
        } else {
            String errorStr = "Did not get RESULT_OK from GetPhotoBitmapService, photo ID: " +
                    mNextPhoto.getId();

            mLogger.trackManagedException("ReceivePhotoBitmapError", errorStr, "", true);
            Log.e(TAG, errorStr);
        }

        // Queue up another photo.
        mHandler.postDelayed(this, mUserPreferences.getPhotoDelaySeconds() * 1000);
    }

    private void setupKenBurnsTransition(String imageFilePath) {

        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
        mBackgroundKenBurnsView.setImageBitmap(imageBitmap);

        PhotoTagsTransitionGenerator generator = new PhotoTagsTransitionGenerator(
                mUserPreferences.getPhotoDelaySeconds() * 1000, mThisPhoto.getTags());

        mBackgroundKenBurnsView.setTransitionGenerator(generator);

        // Fade the background ken burns view into the foreground, and the foreground into the bkgd.
        mBackgroundKenBurnsView.setVisibility(View.VISIBLE);
        mBackgroundKenBurnsView.animate()
                .alpha(1f)
                .setDuration(PHOTO_TRANSITION_MS);

        mActiveKenBurnsView.animate()
                .alpha(0f)
                .setDuration(PHOTO_TRANSITION_MS)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBackgroundKenBurnsView.setVisibility(View.INVISIBLE);
                    }
                });

        // Swap the KenBurnsViews in memory.
        KenBurnsView temp = mActiveKenBurnsView;
        mActiveKenBurnsView = mBackgroundKenBurnsView;
        mBackgroundKenBurnsView = temp;
    }

    private void populateUiWithPhotoInfo(Photo photo) {
        TextView photoDescription = (TextView) findViewById(R.id.photo_description);
        TextView photoTimeAgo = (TextView) findViewById(R.id.photo_time_ago);
        TextView photoPlaceName = (TextView) findViewById(R.id.photo_place_name);
        TextView photoOrder = (TextView) findViewById(R.id.photo_album_photo_count);

        // photo {num} of {num}.
        String numPhotosStr = getString(R.string.photo_var_of_var, photo.getOrder(), mAlbum.getPhotos().size());
        photoOrder.setText(numPhotosStr);

        // Uploader's comment of this photo.
        if (photo.getName() == null || photo.getName().length() == 0) {
            photoDescription.setVisibility(View.INVISIBLE);
        } else {
            photoDescription.setText(photo.getName());
            photoDescription.setVisibility(View.VISIBLE);
        }

        // {age} days/months/years ago.
        photoTimeAgo.setText(formatTimeSincePhotoCreated(photo.getCreatedTime(), DateTime.now()));

        // The name of the place where the photo was taken.
        if (photo.getPlaceName() == null || photo.getPlaceName().length() == 0) {
            photoPlaceName.setVisibility(View.GONE);
        } else {
            photoPlaceName.setText(getString(R.string.at_var, photo.getPlaceName()));
            photoPlaceName.setVisibility(View.VISIBLE);
        }
    }
    /**
     * END sequence
     */
}
