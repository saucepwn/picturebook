package net.garrettsites.picturebook.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;

import net.garrettsites.picturebook.R;

public class UserFacebookProfile extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFacebookProfile.
     */
    public static UserFacebookProfile newInstance() {
        return new UserFacebookProfile();
    }

    public UserFacebookProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_facebook_profile, container, false);

        updateUserInformation(view);

        return view;
    }

    public void updateUserInformation(View rootView) {
        // Check if there is a logged in user. If there is, display their profile information.
        Profile profile = Profile.getCurrentProfile();

        if (profile == null) {
            // User is not logged in.
            rootView.findViewById(R.id.facebook_account_details_layout).setVisibility(View.GONE);
            rootView.findViewById(R.id.facebook_no_account_layout).setVisibility(View.VISIBLE);
        } else {
            // User is logged in.
            rootView.findViewById(R.id.facebook_account_details_layout).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.facebook_no_account_layout).setVisibility(View.GONE);

            TextView name = (TextView) rootView.findViewById(R.id.facebook_user_name);
            name.setText(profile.getName());
        }
    }
}
