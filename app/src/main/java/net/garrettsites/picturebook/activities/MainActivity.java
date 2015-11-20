package net.garrettsites.picturebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.facebook.FacebookSdk;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.commands.GenericResultReceiver;
import net.garrettsites.picturebook.commands.GetAllAlbumsService;
import net.garrettsites.picturebook.model.Album;

import java.util.ArrayList;

public class MainActivity extends PictureBookActivity {

    GenericResultReceiver mReceiver;

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

    /**
     * Used as a hook for debugging.
     * @param view The view which created this action.
     */
    public void launchDebugActivity(View view) {
        mReceiver = new GenericResultReceiver(new Handler());
        mReceiver.setReceiver(new GenericResultReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, ArrayList<Album> albums) {
                // We've gotten the albums.
            }
        });

        Intent i = new Intent(this, GetAllAlbumsService.class);
        i.putExtra(GetAllAlbumsService.ARG_RECEIVER, mReceiver);
        startService(i);
    }
}
