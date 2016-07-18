package net.garrettsites.picturebook.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.applicationinsights.library.TelemetryClient;

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
    private TelemetryClient mLogger = TelemetryClient.getInstance();

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private boolean instantiated = false;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayout mLoadingLayout;
    private LinearLayout mErrorLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChooseAlbumFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!instantiated) {
            instantiated = true;
            beginNonUiOperations(activity);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!instantiated) {
            instantiated = true;
            beginNonUiOperations(context);
        }

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.album_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mLoadingLayout = (LinearLayout) view.findViewById(R.id.fragment_choose_album_loading);
        mErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_choose_album_error);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReceiveAllAlbums(int invocationCode, ArrayList<Album> albums) {
        Log.v(TAG, "Got results from GetAllAlbumsService");

        // Sort the albums in descending order by date.
        Collections.sort(albums, new AlbumDateComparator());
        Collections.reverse(albums);

        mLoadingLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(new ChooseAlbumRecyclerViewAdapter(albums, mListener));
    }

    @Override
    public void onReceiveAllAlbumsError(int invocationCode, int errorCode, Throwable exception) {
        // Show the user an error if we received an error code.
        mLogger.trackEvent("Showing error dialog to user");

        String errorMessage = ErrorCodes.getLocalizedErrorStringResource(
                getResources(), errorCode, exception);

        new AlertDialog.Builder(getActivity()).setTitle(R.string.error)
                .setMessage(errorMessage)
                .setIcon(android.R.drawable.stat_notify_error)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }).show();

        ((TextView) mErrorLayout.findViewById(R.id.fragment_choose_album_error_message))
                .setText(errorMessage);

        mLoadingLayout.setVisibility(View.GONE);
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
