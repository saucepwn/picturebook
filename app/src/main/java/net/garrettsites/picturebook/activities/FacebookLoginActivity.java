package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.microsoft.applicationinsights.library.ApplicationInsights;
import com.microsoft.applicationinsights.library.TelemetryClient;

import net.garrettsites.picturebook.R;

import java.util.Arrays;

public class FacebookLoginActivity extends PictureBookActivity {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        mCallbackManager = CallbackManager.Factory.create();
        final LoginManager loginManager = LoginManager.getInstance();
        final Activity self = this;

        if (AccessToken.getCurrentAccessToken() == null) {
            // Log in a user.
            loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY);
            loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // Show the user a thank you screen.
                    ApplicationInsights.getTelemetryContext().setAuthenticatedUserId(loginResult.getAccessToken().getUserId());
                    findViewById(R.id.facebook_login_thank_you_layout).setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException error) {
                    TelemetryClient.getInstance().trackHandledException(error);

                    new AlertDialog.Builder(self)
                            .setTitle("Facebook Error")
                            .setMessage(error.getLocalizedMessage())
                            .setNeutralButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            
            loginManager.logInWithReadPermissions(this, Arrays.asList("user_photos", "public_profile", "user_tagged_places"));
        } else {
            // Log out the user.
            new AlertDialog.Builder(this).setTitle("Confirm Log Out")
                    .setMessage("You must have a Facebook account logged in to view slideshows. Are you sure you want to log out?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            self.finish();
                        }
                    })
                    .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loginManager.logOut();
                            ApplicationInsights.getTelemetryContext().setAuthenticatedUserId(null);
                            self.finish();
                        }
                    })
                    .show();
        }

        // Go back to the previous activity if the user clicks "next".
        findViewById(R.id.facebook_login_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
