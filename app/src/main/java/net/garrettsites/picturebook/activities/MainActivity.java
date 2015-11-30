package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.commands.AllPhotosMetadataResultReceiver;
import net.garrettsites.picturebook.commands.ChooseRandomAlbum;
import net.garrettsites.picturebook.commands.AllAlbumsResultReceiver;
import net.garrettsites.picturebook.commands.GetAllAlbumsService;
import net.garrettsites.picturebook.commands.GetAllPhotoMetadataService;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.Photo;

import java.util.ArrayList;

public class MainActivity extends PictureBookActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    /**
     * Launches the AccountsActivity.
     * @param view The view which created this action.
     */
    public void launchAccountsActivity(View view) {
        Intent i = new Intent(this, AccountsActivity.class);
        startActivity(i);
    }

    public void startSlideshow(View view) {
        Intent i = new Intent(this, ViewSlideshowActivity.class);
        startActivity(i);
    }

    /**
     * Used as a hook for debugging. Delete all this shit before it's time to release.
     * @param view The view which created this action.
     */
    public void launchDebugActivity(View view) {
        final TextView albumNameView = (TextView) findViewById(R.id.random_album_name);
        final TextView albumPhotoCount = (TextView) findViewById(R.id.album_photo_count);

        final Activity self = this;

        AllAlbumsResultReceiver mReceiver = new AllAlbumsResultReceiver(new Handler());
        mReceiver.setReceiver(new AllAlbumsResultReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, ArrayList<Album> albums) {
                // We've gotten the albums.
                ChooseRandomAlbum albumRandomizer = new ChooseRandomAlbum(albums);
                Album randomAlbum = albumRandomizer.selectRandomAlbum();
                albumNameView.setText("Your album is: " + randomAlbum.getName() + "\nDescription: " + randomAlbum.getDescription() + "\nType: " + randomAlbum.getType() + "\nCreated: " + randomAlbum.getCreatedTime() + "\nLast updated: " + randomAlbum.getUpdatedTime());

                // Get all of the photos in our target album.
                AllPhotosMetadataResultReceiver allPhotosReceiver = new AllPhotosMetadataResultReceiver(new Handler());
                allPhotosReceiver.setReceiver(new AllPhotosMetadataResultReceiver.Receiver() {
                    @Override
                    public void onReceiveResult(int resultCode, ArrayList<Photo> albums) {
                        // We've gotten all of the photos in this album.
                        albumPhotoCount.setText("Album contains " + albums.size() + " photos.");
                    }
                });

                Intent i = new Intent(self, GetAllPhotoMetadataService.class);
                i.putExtra(GetAllPhotoMetadataService.ARG_RECEIVER, allPhotosReceiver);
                i.putExtra(GetAllPhotoMetadataService.ARG_ALBUM_ID, randomAlbum.getId());
                self.startService(i);
            }
        });

        Intent i = new Intent(this, GetAllAlbumsService.class);
        i.putExtra(GetAllAlbumsService.ARG_RECEIVER, mReceiver);
        startService(i);
    }
}
