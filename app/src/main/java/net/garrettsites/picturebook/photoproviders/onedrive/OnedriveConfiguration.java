package net.garrettsites.picturebook.photoproviders.onedrive;

import net.garrettsites.picturebook.R;
import net.garrettsites.picturebook.activities.OnedriveLoginActivity;
import net.garrettsites.picturebook.photoproviders.ProviderConfiguration;

/**
 * Created by Garrett on 6/29/2016.
 */
public class OnedriveConfiguration implements ProviderConfiguration {
    @Override
    public int getNameResource() {
        return R.string.provider_onedrive;
    }

    @Override
    public int getColorResource() {
        return R.color.onedrive;
    }

    @Override
    public int getIconResource() {
        return R.drawable.onedrive_icon;
    }

    @Override
    public String getShortName() {
        return "1D";
    }

    @Override
    public Class<?> getManageAccountsActivity() {
        return OnedriveLoginActivity.class;
    }
}
