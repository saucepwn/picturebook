package net.garrettsites.picturebook.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.fragments.ChooseAlbumFragment.OnListFragmentInteractionListener;
import net.garrettsites.picturebook.model.Album;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Album} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ChooseAlbumRecyclerViewAdapter extends RecyclerView.Adapter<ChooseAlbumRecyclerViewAdapter.ViewHolder> {

    private final List<Album> mAlbums;
    private final OnListFragmentInteractionListener mListener;

    public ChooseAlbumRecyclerViewAdapter(List<Album> albums, OnListFragmentInteractionListener listener) {
        mAlbums = albums;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_choose_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mAlbum = mAlbums.get(position);
        holder.mLastUpdatedView.setText(mAlbums.get(position).getAlbumDate().toString("MMMM d, yyyy"));
        holder.mAlbumNameView.setText(mAlbums.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mAlbum);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLastUpdatedView;
        public final TextView mAlbumNameView;
        public Album mAlbum;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLastUpdatedView = (TextView) view.findViewById(R.id.choose_album_last_updated);
            mAlbumNameView = (TextView) view.findViewById(R.id.choose_album_album_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAlbumNameView.getText() + "'";
        }
    }
}
