package net.garrettsites.picturebook.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import net.garrettsites.picturebook.services.GetAlbumPhotoDataService;
import net.garrettsites.picturebook.services.GetAllAlbumsService;
import net.garrettsites.picturebook.services.GetPhotoBitmapService;
import net.garrettsites.picturebook.util.ChooseRandomAlbum;
import net.garrettsites.picturebook.util.OverlayLayoutHelper;
import net.garrettsites.picturebook.util.PhotoDateFormatter;
import net.garrettsites.picturebook.util.PhotoOrder;
import net.garrettsites.picturebook.util.PhotoTagsTransitionGenerator;
import net.garrettsites.picturebook.util.RandomPhotoOrder;
import net.garrettsites.picturebook.util.SequentialPhotoOrder;
import net.garrettsites.picturebook.util.Sleepitizer;

import org.joda.time.DateTime;

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

    private boolean mIsPaused = false;
    private long mCurrentPhotoDisplayedTimeMillis; // When the current photo was first displayed.
    private long mPausedPhotoDisplayedDurationMillis; // How long the current photo was displayed before it was paused.

    private int serviceInvocationCode;

    private PhotoDateFormatter mPhotoDateFormatter;
    private OverlayLayoutHelper overlayHelper;

    private KenBurnsView mActiveKenBurnsView;
    private KenBurnsView mBackgroundKenBurnsView;

    private Sleepitizer mSleeper = new Sleepitizer();
    private Handler mHandler;
    private GetPhotoBitmapReceiver mPhotoBitmapReceiver;

    private UserPreferences mUserPreferences;
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    public static final String ARG_ALBUM = "album";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        // Sometimes we can be viewing a slideshow when a StartSlideshowBroadcastReceiver receives
        // an intent to start another slideshow. Usually, we want to start another slideshow.
        // However, if the user has paused the current slideshow, don't start a new one.
        if (mIsPaused) {
            mLogger.trackEvent("Entered ViewSlideshowActivity.onCreate while current slideshow was paused.");
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slideshow);

        // Wake the device up when this activity starts & prevent sleep.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActiveKenBurnsView = (KenBurnsView) findViewById(R.id.image_viewport_1);
        mBackgroundKenBurnsView = (KenBurnsView) findViewById(R.id.image_viewport_2);

        mUserPreferences = ((PicturebookApplication) getApplication()).preferences;

        mPhotoDateFormatter = new PhotoDateFormatter(getResources());

        // Show overlay UI when the user taps the screen during the slideshow.
        View overlayRootLayout = findViewById(R.id.view_slideshow_overlay_root_layout);
        overlayHelper = new OverlayLayoutHelper(this, overlayRootLayout);
        findViewById(R.id.view_slideshow_root_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayHelper.showOverlay();
            }
        });

        // If an album was passed to this activity, display it. Otherwise, choose a random album
        // to show.
        mAlbum = null;
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
        Log.d(TAG, "onResume called");
        super.onResume();

        mHandler = new Handler();

        // Hook up the receiver from the GetPhotoBitmap service.
        mPhotoBitmapReceiver = new GetPhotoBitmapReceiver(mHandler);
        mPhotoBitmapReceiver.setReceiver(this);

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
        Log.d(TAG, "onPause called");
        super.onPause();
        mHandler.removeCallbacks(this);
        mHandler = null;
    }

    @Override
    public void run() {
        // Check if we should put the device to sleep (by finishing the activity).
        if (mUserPreferences.isSleeperWakerEnabled() && mSleeper.timeToSleep()) {
            mLogger.trackEvent("Sleeper finishing ViewSlideshowActivity");
            finishSlideshow();
        } else {
            beginLoadNewPhoto();
        }
    }

    /**
     * Finishes the activity and removes all handler callbacks.
     */
    public void finishSlideshow() {
        mHandler.removeCallbacks(this);
        finish();
    }

    /**
     * Hides the splash screen if it's visible.
     */
    private void hideSplashScreenIfVisible() {
        final View splashScreen = findViewById(R.id.photo_splash_screen);

        if (splashScreen.getVisibility() == View.VISIBLE) {
            // Allow the UI overlay once the splash screen disappears.
            overlayHelper.setOverlayAllowed();

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
     * From here until the below comment, the following methods should be called IN ORDER to
     * retrieve the photo album.
     */
    private void beginRetrieveAlbumSequence() {
        // Step 1: Get the metadata for all of this user's photo albums.
        Log.v(TAG, "Starting retrieve album sequence");
        callGetAllAlbumsService();
    }

    private void callGetAllAlbumsService() {
        ((TextView) findViewById(R.id.splash_screen_loading_details)).setText(R.string.getting_all_albums);
        GetAllAlbumsReceiver getAllAlbumsReceiver = new GetAllAlbumsReceiver(mHandler);
        getAllAlbumsReceiver.setReceiver(this);

        Intent getAllAlbumsIntent = new Intent(this, GetAllAlbumsService.class);
        getAllAlbumsIntent.putExtra(GetAllAlbumsService.ARG_RECEIVER, getAllAlbumsReceiver);

        Log.v(TAG, "Calling GetAllAlbumsService" + serviceInvocationCode);
        startServiceInternal(getAllAlbumsIntent);
    }

    @Override
    public void onReceiveAllAlbums(int resultCode, int errorCode, int invocationCode, ArrayList<Album> albums) {
        if (!validateInvocationCode(invocationCode)) return;

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

        Intent getAllPhotoMetadataIntent = new Intent(this, GetAlbumPhotoDataService.class);
        getAllPhotoMetadataIntent.putExtra(GetAlbumPhotoDataService.ARG_RECEIVER, allPhotosReceiver);
        getAllPhotoMetadataIntent.putExtra(GetAlbumPhotoDataService.ARG_ALBUM, mAlbum);

        Log.v(TAG, "Calling GetAlbumPhotoDataService");
        startServiceInternal(getAllPhotoMetadataIntent);
    }

    @Override
    public void onReceiveAllPhotoMetadata(
            int resultCode, int invocationCode, ArrayList<Photo> photos) {
        if (!validateInvocationCode(invocationCode)) return;

        Log.v(TAG, "Got results from GetAlbumPhotoDataService");

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
     * either acquire the photo from the internet or locally from cache. The act of retrieving the
     * photo happens on a background thread.
     */
    private void beginLoadNewPhoto() {
        mNextPhoto = mAlbum.getPhotos().get(mPhotoOrder.getNextPhotoIdx());
        Log.v(TAG, "Begin load new photo ID: " + mNextPhoto.getId());

        Intent getBitmapIntent = new Intent(this, GetPhotoBitmapService.class);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_PHOTO_OBJ, mNextPhoto);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_RECEIVER, mPhotoBitmapReceiver);
        startServiceInternal(getBitmapIntent);
        Log.v(TAG, "End loading photo: " + mNextPhoto.getId());
    }

    @Override
    public void onReceivePhotoBitmap(int resultCode, int invocationCode, String imageFilePath) {
        if (!validateInvocationCode(invocationCode)) return;

        // Only update the photo if the last operation completed successfully. If it didn't, try it
        // again.
        if (resultCode == Activity.RESULT_OK) {
            mThisPhoto = mNextPhoto;
            mNextPhoto = null;

            hideSplashScreenIfVisible();

            // TODO: Sometimes imageBitmap is null. Handle that situation gracefully.
            // Show the image we've just retrieved.
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
            if (imageBitmap != null) {
                mThisPhoto.setWidth(imageBitmap.getWidth());
                mThisPhoto.setHeight(imageBitmap.getHeight());
            }

            setupKenBurnsTransition(imageBitmap);

            // Populate the UI with additional photo information.
            populateUiWithPhotoInfo(mThisPhoto);
        } else {
            String errorStr = "Did not get RESULT_OK from GetPhotoBitmapService, photo ID: " +
                    mNextPhoto.getId();

            mLogger.trackManagedException("ReceivePhotoBitmapError", errorStr, "", true);
            Log.e(TAG, errorStr);
        }

        // Queue up another photo.
        if (!mIsPaused)
            mHandler.postDelayed(this, mUserPreferences.getPhotoDelaySeconds() * 1000);
    }

    private void setupKenBurnsTransition(Bitmap imageBitmap) {
        mBackgroundKenBurnsView.setImageBitmap(imageBitmap);

        PhotoTagsTransitionGenerator generator = new PhotoTagsTransitionGenerator(
                mUserPreferences.getPhotoDelaySeconds() * 1000, null);

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

        mCurrentPhotoDisplayedTimeMillis = System.currentTimeMillis();
    }

    private void populateUiWithPhotoInfo(Photo photo) {
        TextView photoDescription = (TextView) findViewById(R.id.photo_description);
        TextView photoTimeAgo = (TextView) findViewById(R.id.photo_time_ago);
        TextView photoPlaceName = (TextView) findViewById(R.id.photo_place_name);
        TextView photoOrder = (TextView) findViewById(R.id.photo_album_photo_count);
        View numPeopleInPhotoLayout = findViewById(R.id.photo_num_people_layout);
        TextView numPeopleInPhoto = (TextView) findViewById(R.id.photo_num_people);

        // photo {num} of {num}.
        String numPhotosStr = getString(R.string.photo_var_of_var, photo.getOrder(), mAlbum.getPhotos().size());
        photoOrder.setText(numPhotosStr);

        // Number of people tagged in the photo.
        if (photo.getNumPeopleInPhoto() > 0) {
            numPeopleInPhotoLayout.setVisibility(View.VISIBLE);
            numPeopleInPhoto.setText(String.format("%d", photo.getNumPeopleInPhoto()));
        } else {
            numPeopleInPhotoLayout.setVisibility(View.GONE);
        }

        // The name of the place where the photo was taken.
        if (photo.getPlaceName() == null || photo.getPlaceName().length() == 0) {
            photoPlaceName.setVisibility(View.GONE);
        } else {
            photoPlaceName.setText(getString(R.string.at_var, photo.getPlaceName()));
            photoPlaceName.setVisibility(View.VISIBLE);
        }

        // {age} days/months/years ago.
        photoTimeAgo.setText(mPhotoDateFormatter.formatTimeSincePhotoCreated(
                photo.getCreatedTime(), DateTime.now()));

        // Uploader's comment of this photo.
        if (photo.getName() == null || photo.getName().length() == 0) {
            photoDescription.setVisibility(View.INVISIBLE);
        } else {
            photoDescription.setText(photo.getName());
            photoDescription.setVisibility(View.VISIBLE);
        }
    }
    /**
     * END sequence
     */

    /**
     * @return The photo currently on screen.
     */
    public Photo getCurrentPhoto() {
        return mThisPhoto;
    }

    /**
     * Pauses the slideshow. Stops the Ken Burns animation and stops the photo advance timer.
     */
    public void pauseSlideshow() {
        if (mIsPaused) return;

        Log.i(TAG, "Pausing slideshow");
        mIsPaused = true;
        mActiveKenBurnsView.pause();
        mHandler.removeCallbacks(this);

        mPausedPhotoDisplayedDurationMillis = System.currentTimeMillis() -
                mCurrentPhotoDisplayedTimeMillis;
    }

    /**
     * Resumes a slideshow that has been paused.
     */
    public void resumeSlideshow() {
        if (!mIsPaused) return;

        mIsPaused = false;
        mActiveKenBurnsView.resume();

        int photoDelayMillis = mUserPreferences.getPhotoDelaySeconds() * 1000;
        long remainingTime = photoDelayMillis - mPausedPhotoDisplayedDurationMillis;

        // If less than 3 seconds remain on the current photo, show it for at least 3 more seconds
        // so that it doesn't seem like the slideshow is jumpy.
        if (remainingTime < 3000) {
            remainingTime = 3000;
        }

        Log.i(TAG, "Resuming slideshow, next photo in " + (remainingTime / 1000) + " seconds");
        mHandler.postDelayed(this, remainingTime);
    }

    /**
     * Validates that the received invocation code matches the invocation code we started the service
     * with. This prevents duplicate service calls from being made.
     * @param receivedCode The code to validate.
     * @return True if this is the response we are expecting, false if it is not.
     */
    private boolean validateInvocationCode(int receivedCode) {
        if (receivedCode != serviceInvocationCode) {
            Log.w(TAG, "Received message with incorrect invocation code. Dropping message.");
            return false;
        }

        return true;
    }

    /**
     * Adds an invocation code to the intent and starts the service described by it.
     * @param intent The intent to start.
     */
    private void startServiceInternal(Intent intent) {
        serviceInvocationCode = (int) (Math.random() * Integer.MAX_VALUE);
        intent.putExtra("code", serviceInvocationCode);

        startService(intent);
    }
}
