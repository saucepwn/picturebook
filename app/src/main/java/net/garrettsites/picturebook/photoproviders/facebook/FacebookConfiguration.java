package net.garrettsites.picturebook.photoproviders.facebook;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.activities.FacebookLoginActivity;
import net.garrettsites.picturebook.photoproviders.ProviderConfiguration;

/**
 * Created by Garrett on 6/28/2016.
 */
public class FacebookConfiguration implements ProviderConfiguration {

    @Override
    public int getNameResource() {
        return R.string.provider_facebook;
    }

    @Override
    public int getColorResource() {
        return R.color.facebook;
    }

    @Override
    public int getIconResource() {
        return R.drawable.facebook_icon;
    }

    @Override
    public Class<?> getManageAccountsActivity() {
        return FacebookLoginActivity.class;
    }
}
