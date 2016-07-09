package net.garrettsites.picturebook.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.fragments.ManageAccountsFragment;
import net.garrettsites.picturebook.receivers.StartSlideshowBroadcastReceiver;

public class MainActivity extends PictureBookActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fm = getFragmentManager();
        ManageAccountsFragment userInfoFragment  = (ManageAccountsFragment) fm.findFragmentById(R.id.accounts_profile_fragment_container);

        // The layout needs to update each time onResume is called.
        userInfoFragment.updateAllRows(findViewById(R.id.accounts_profile_fragment_container));
    }

    public void startSlideshow(View view) {
        Intent i = new Intent(this, StartSlideshowBroadcastReceiver.class);
        i.setAction("START_SLIDESHOW_MANUAL");
        sendBroadcast(i);
    }

    public void chooseAlbum(View view) {
        startActivity(new Intent(this, ChooseAlbumActivity.class));
    }
}
