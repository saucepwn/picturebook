package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import com.flaviofaria.kenburnsview.KenBurnsView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.commands.GetPhotoBitmapReceiver;
import net.garrettsites.picturebook.commands.GetPhotoBitmapService;
import net.garrettsites.picturebook.model.Photo;

import org.joda.time.DateTime;

import java.net.URL;

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
        /*
        Album album = getIntent().getParcelableExtra(ARG_ALBUM);

        if (album == null) {
            throw new IllegalArgumentException("Need to pass the '" + ARG_ALBUM + "' arg as an Album to this activity.");
        }
*/
        // Display the first picture from the album.


        GetPhotoBitmapReceiver receiver = new GetPhotoBitmapReceiver(new Handler());
        receiver.setReceiver(this);

        // TEST: sample image
        Photo p = null;
        try {
            p = new Photo("10152908351577443",
                    "Michelle Curran Waterskis",
                    "Garrett Padera",
                    "10152219716832443",
                    new URL("https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/11752049_10152908351577443_1533813850328563646_n.jpg?oh=7d96fa4ae76eb9eb3572ed93b0d44e70&oe=56AC9A14"),
                    new URL("https://www.facebook.com/photo.php?fbid=10152908351577443&set=a.10152908356347443&type=3"),
                    new DateTime("2015-07-20T06:31:43+0000"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent getBitmapIntent = new Intent(this, GetPhotoBitmapService.class);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_PHOTO_OBJ, p);
        getBitmapIntent.putExtra(GetPhotoBitmapService.ARG_RECEIVER, receiver);
        startService(getBitmapIntent);
    }

    @Override
    public void onReceiveResult(int resultCode, String imageFilePath) {
        // Show the image we've just retrieved.
        KenBurnsView imageViewport = (KenBurnsView) findViewById(R.id.image_viewport);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
        imageViewport.setImageBitmap(imageBitmap);
    }
}
