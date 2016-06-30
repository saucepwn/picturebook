package net.garrettsites.picturebook.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.fragments.ChooseAlbumFragment.OnListFragmentInteractionListener;
import net.garrettsites.picturebook.model.Album;
import net.garrettsites.picturebook.photoproviders.ProviderConfiguration;

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
        Album album = mAlbums.get(position);
        ProviderConfiguration providerConfig = album.getPhotoProvider().getConfiguration();
        holder.album = album;

        holder.providerIcon.setImageResource(providerConfig.getIconResource());

        int tintColor = holder.providerIcon.getResources().getColor(providerConfig.getColorResource());
        holder.providerIcon.setColorFilter(tintColor);

        holder.lastUpdatedView.setText(album.getAlbumDate().toString("MMMM d, yyyy"));
        holder.albumNameView.setText(album.getName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.album);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView providerIcon;
        public final TextView lastUpdatedView;
        public final TextView albumNameView;
        public Album album;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            providerIcon = (ImageView) view.findViewById(R.id.choose_album_provider_icon);
            lastUpdatedView = (TextView) view.findViewById(R.id.choose_album_last_updated);
            albumNameView = (TextView) view.findViewById(R.id.choose_album_album_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + albumNameView.getText() + "'";
        }
    }
}
