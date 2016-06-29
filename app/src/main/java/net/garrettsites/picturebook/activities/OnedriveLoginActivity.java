package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.os.Bundle;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;

/**
 * Created by Garrett on 6/29/2016.
 */
public class OnedriveLoginActivity extends PictureBookActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onedrive_login);

        final Activity self = this;

        if (!PhotoProviders.getOnedrivePhotoProvider().isUserLoggedIn()) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    PhotoProviders.getOnedrivePhotoProvider().logIn(self);
                }});
            t.start();
        }
    }
}
