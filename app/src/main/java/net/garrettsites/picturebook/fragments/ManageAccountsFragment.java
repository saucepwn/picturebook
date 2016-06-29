package net.garrettsites.picturebook.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.photoproviders.PhotoProvider;
import net.garrettsites.picturebook.photoproviders.PhotoProviders;
import net.garrettsites.picturebook.photoproviders.ProviderConfiguration;

public class ManageAccountsFragment extends Fragment {

    public ManageAccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_accounts, container, false);

        // Each photo provider gets its own row in the table.
        for (PhotoProvider provider : PhotoProviders.getAllPhotoProviders()) {
            View table = inflater.inflate(
                    R.layout.fragment_user_accounts_row,
                    (ViewGroup) view.findViewById(R.id.user_accounts_table_layout));

            TableRow row = (TableRow) table.findViewById(R.id.user_accounts_table_row);
            initialRowSetup(row, provider);
            updateRowLayout(row, provider);
        }

        return view;
    }

    /**
     * Updates UI elements in the row that will change over the lifetime of the fragment, for
     * example, elements that change if a user account is logged in or logged out.
     * @param row The row to configure.
     * @param provider The provider to configure for.
     */
    public void updateRowLayout(TableRow row, PhotoProvider provider) {
        TextView userName = (TextView) row.findViewById(R.id.photo_provider_user_name);
        Button manageAcctButton = (Button) row.findViewById(R.id.manage_acct_button);

        if (provider.isUserLoggedIn()) {
            // User is logged in.
            userName.setVisibility(View.VISIBLE);
            userName.setText(provider.getUserName());
            manageAcctButton.setText(R.string.unlink_account);
        } else {
            // User is not logged in.
            userName.setVisibility(View.GONE);
            manageAcctButton.setText(R.string.link_account);
        }
    }

    /**
     * Creates UI elements in the row that will not change over the lifetime of the fragment.
     * @param row The row to configure.
     * @param provider The provider to configure for.
     */
    private void initialRowSetup(TableRow row, PhotoProvider provider) {
        ImageView providerIcon = (ImageView) row.findViewById(R.id.provider_icon);
        TextView photoProviderName = (TextView) row.findViewById(R.id.photo_provider_name);
        Button manageAcctButton = (Button) row.findViewById(R.id.manage_acct_button);

        final ProviderConfiguration configuration = provider.getConfiguration();

        row.setBackgroundResource(configuration.getColorResource());
        photoProviderName.setText(configuration.getNameResource());
        providerIcon.setImageResource(configuration.getIconResource());

        // Hook up link/unlink account button.
        manageAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), configuration.getManageAccountsActivity()));
            }
        });
    }
}
