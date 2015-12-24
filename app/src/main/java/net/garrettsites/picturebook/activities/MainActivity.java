package net.garrettsites.picturebook.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.fragments.UserFacebookProfile;
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
        UserFacebookProfile userInfoFragment  = (UserFacebookProfile) fm.findFragmentById(R.id.accounts_profile_fragment_container);
        userInfoFragment.updateUserInformation(findViewById(R.id.accounts_profile_fragment_container));
    }

    public void startSlideshow(View view) {
        sendBroadcast(new Intent(this, StartSlideshowBroadcastReceiver.class));
    }
}
