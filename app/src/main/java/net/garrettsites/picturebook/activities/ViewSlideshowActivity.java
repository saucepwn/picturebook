package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.commands.GetPhotoBitmapReceiver;
import net.garrettsites.picturebook.commands.GetPhotoBitmapService;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;
import net.garrettsites.picturebook.model.UserPreferences;
import net.garrettsites.picturebook.util.PhotoOrder;
import net.garrettsites.picturebook.util.RandomPhotoOrder;
import net.garrettsites.picturebook.util.SequentialPhotoOrder;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Garrett on 11/29/2015.
 */
public class ViewSlideshowActivity extends Activity
        implements GetPhotoBitmapReceiver.Receiver, Runnable {

    public static final String ARG_ALBUM = "album";
    private static final String TAG = ViewSlideshowActivity.class.getName();

    private Album mAlbum;
    private PhotoOrder mPhotoOrder;
    private Photo mNextPhoto;
    private Photo mThisPhoto;

    private GetPhotoBitmapReceiver mReceiver = new GetPhotoBitmapReceiver(new Handler());
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slideshow);

        // Get the album we're supposed to display.
        mAlbum = getIntent().getParcelableExtra(ARG_ALBUM);
        if (mAlbum == null) {
            throw new IllegalArgumentException("Need to pass the '" + ARG_ALBUM + "' arg as an Album to this activity.");
        }

        // Create the ordering scheme for the photos.
        if (UserPreferences.getRandomizePhotoOrder()) {
            mPhotoOrder = new RandomPhotoOrder(mAlbum.getPhotos().size());
        } else {
            mPhotoOrder = new SequentialPhotoOrder(mAlbum.getPhotos().size());
        }

        // Hook up the receiver from the GetPhotoBitmap service.
        mReceiver = new GetPhotoBitmapReceiver(new Handler());
        mReceiver.setReceiver(this);

        // Populate UI elements with data from the album.
        String numPhotosStr = getString(R.string.num_photos, mAlbum.getPhotos().size());
        ((TextView) findViewById(R.id.photo_album_photo_count)).setText(numPhotosStr);
        ((TextView) findViewById(R.id.photo_album_name)).setText(mAlbum.getName());

        loadNewPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Hide the navigation bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(this);
    }

    @Override
    public void onReceiveResult(int resultCode, String imageFilePath) {
        mThisPhoto = mNextPhoto;
        mNextPhoto = null;

        // Show the image we've just retrieved.
        KenBurnsView imageViewport = (KenBurnsView) findViewById(R.id.image_viewport);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
        imageViewport.setImageBitmap(imageBitmap);

        // Populate the UI with additional photo information.
        TextView photoDate = (TextView) findViewById(R.id.photo_date);
        TextView photoDescription = (TextView) findViewById(R.id.photo_description);

        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MMMM e, YYYY");
        photoDate.setText(dateTimeFormat.print(mThisPhoto.getCreatedTime()));

        photoDescription.setText(mThisPhoto.getName());

        // Queue up another photo.
        mHandler.postDelayed(this, UserPreferences.getPhotoDelaySeconds() * 1000);
    }

    @Override
    public void run() {
        loadNewPhoto();
    }

    /**
     * Begin the process of displaying a new photo. This calls GetPhotoBitmapService which will
     * either acquire the photo from Facebook (using the internet) or locally from cache. The act
     * of retrieving the photo happens on a background thread.
     */
    private void loadNewPhoto() {
        mNextPhoto = mAlbum.getPhotos().get(mPhotoOrder.getNextPhotoIdx());

        Intent getBitmapIntent = new Intent(this, GetPhotoBitmapService.class);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_PHOTO_OBJ, mNextPhoto);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_RECEIVER, mReceiver);
        startService(getBitmapIntent);
    }
}
