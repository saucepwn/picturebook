package net.garrettsites.picturebook.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookActivity;
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

        // Check if there is a logged in user. If there is, display their profile information.
        if (AccessToken.getCurrentAccessToken() == null) {
            // User is not logged in.
            view.findViewById(R.id.facebook_account_details_layout).setVisibility(View.GONE);
        } else {
            // User is logged in.
            view.findViewById(R.id.facebook_no_account_layout).setVisibility(View.GONE);

            Profile profile = Profile.getCurrentProfile();

            TextView name = (TextView) view.findViewById(R.id.facebook_user_name);
            name.setText(profile.getName());
        }

        return view;
    }
}
