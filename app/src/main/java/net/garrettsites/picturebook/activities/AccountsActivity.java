package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.fragments.UserFacebookProfile;

public class AccountsActivity extends PictureBookActivity {

    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        final Activity self = this;

        Button addAccountButton = (Button) findViewById(R.id.accounts_add_account_button);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(self, FacebookLoginActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFragmentManager().beginTransaction().replace(R.id.accounts_profile_fragment_container, new UserFacebookProfile()).commit();
    }
}
