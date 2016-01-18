package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.os.Bundle;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.fragments.ChooseAlbumFragment;
import net.garrettsites.picturebook.model.Album;

/**
 * Created by Garrett on 1/12/2016.
 */
public class ChooseAlbumActivity extends Activity
        implements ChooseAlbumFragment.OnListFragmentInteractionListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_album);
    }

    @Override
    public void onListFragmentInteraction(Album album) {

    }
}
