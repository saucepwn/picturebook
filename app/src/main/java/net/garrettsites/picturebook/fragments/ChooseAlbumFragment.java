package net.garrettsites.picturebook.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.model.AlbumDateComparator;
import net.garrettsites.picturebook.model.ErrorCodes;
import net.garrettsites.picturebook.receivers.GetAllAlbumsReceiver;
import net.garrettsites.picturebook.services.GetAllAlbumsService;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChooseAlbumFragment extends Fragment implements GetAllAlbumsReceiver.Receiver {
    public static final String TAG = ChooseAlbumFragment.class.getName();

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChooseAlbumFragment() {
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        beginNonUiOperations((Context) activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        beginNonUiOperations(context);
    }

    /**
     * Performs operations that are required for this fragment that do not interact with the UI.
     * This method should be called onAttach(...).
     * @param context The parent activity's context.
     */
    private void beginNonUiOperations(Context context) {
        GetAllAlbumsReceiver getAllAlbumsReceiver = new GetAllAlbumsReceiver(new Handler());
        getAllAlbumsReceiver.setReceiver(this);

        Intent getAllAlbumsIntent = new Intent(context, GetAllAlbumsService.class);
        getAllAlbumsIntent.putExtra(GetAllAlbumsService.ARG_RECEIVER, getAllAlbumsReceiver);

        Log.v(TAG, "Calling GetAllAlbumsService");
        context.startService(getAllAlbumsIntent);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_album_list, container, false);

        // Set the adapter
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReceiveAllAlbums(int resultCode, int errorCode, int invocationCode, ArrayList<Album> albums) {
        if (resultCode == Activity.RESULT_OK) {
            Log.v(TAG, "Got results from GetAllAlbumsService");

            // Sort the albums in descending order by date.
            Collections.sort(albums, new AlbumDateComparator());
            Collections.reverse(albums);

            mRecyclerView.setAdapter(new ChooseAlbumRecyclerViewAdapter(albums, mListener));
        } else {
            // Show the user an error if we received an error code.
            new AlertDialog.Builder(getActivity()).setTitle(R.string.error)
                    .setMessage(ErrorCodes.getLocalizedErrorStringResource(errorCode))
                    .setIcon(android.R.drawable.stat_notify_error)
                    .setNeutralButton(R.string.ok, null)
                    .show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Album item);
    }
}
