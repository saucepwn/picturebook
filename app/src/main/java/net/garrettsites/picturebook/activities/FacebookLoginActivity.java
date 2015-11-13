package net.garrettsites.picturebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import net.garrettsites.picturebook.R;

import java.util.Arrays;

public class FacebookLoginActivity extends PictureBookActivity {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();

        if (AccessToken.getCurrentAccessToken() == null) {
            // Log in a user.
            loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY);
            loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    finish();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
            loginManager.logInWithReadPermissions(this, Arrays.asList("user_photos", "public_profile"));
        } else {
            // Log out the user.
            loginManager.logOut();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
