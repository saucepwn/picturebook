package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
            // Log in a user.
            Thread t = new Thread(new Runnable() {
                public void run() {
                    PhotoProviders.getOnedrivePhotoProvider().logIn(self);
                }});
            t.start();
        } else {
            // Log out the current user.
            // Log out the user.
            new AlertDialog.Builder(this).setTitle("Confirm Log Out")
                    .setMessage("Are you sure you want to log out of your OneDrive account?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            self.finish();
                        }
                    })
                    .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PhotoProviders.getOnedrivePhotoProvider().logOut();
                            self.finish();
                        }
                    })
                    .show();
        }
    }
}
