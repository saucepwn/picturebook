package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.flaviofaria.kenburnsview.KenBurnsView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.commands.GetPhotoBitmapReceiver;
import net.garrettsites.picturebook.commands.GetPhotoBitmapService;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;

/**
 * Created by Garrett on 11/29/2015.
 */
public class ViewSlideshowActivity extends Activity implements GetPhotoBitmapReceiver.Receiver {

    public static final String ARG_ALBUM = "album";
    private static final String TAG = ViewSlideshowActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slideshow);

        // Get the album we're supposed to display.
        Album album = getIntent().getParcelableExtra(ARG_ALBUM);

        if (album == null) {
            throw new IllegalArgumentException("Need to pass the '" + ARG_ALBUM + "' arg as an Album to this activity.");
        }

        // Display the first picture from the album.
        GetPhotoBitmapReceiver receiver = new GetPhotoBitmapReceiver(new Handler());
        receiver.setReceiver(this);

        // For now, just get the first photo from the album.
        Photo p = album.getPhotos().get(0);

        Intent getBitmapIntent = new Intent(this, GetPhotoBitmapService.class);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_PHOTO_OBJ, p);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_RECEIVER, receiver);
        startService(getBitmapIntent);
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
    public void onReceiveResult(int resultCode, String imageFilePath) {
        // Show the image we've just retrieved.
        KenBurnsView imageViewport = (KenBurnsView) findViewById(R.id.image_viewport);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
        imageViewport.setImageBitmap(imageBitmap);
    }
}
