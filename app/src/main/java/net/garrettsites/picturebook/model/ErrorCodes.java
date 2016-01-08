package net.garrettsites.picturebook.model;

import net.garrettsites.picturebook.R;

public class ErrorCodes {
    public enum Error {
        NO_LOGGED_IN_ACCOUNT
    }

    /**
     * Gets a localized string resource to show the user for the given error code.
     * @param errorCode The error code to generate a string for.
     * @return A localized string that's safe to show to the user.
     */
    public static int getLocalizedErrorStringResource(Error errorCode) {
        switch (errorCode) {
            case NO_LOGGED_IN_ACCOUNT:
                return R.string.error_no_account;
            default:
                return R.string.generic_error;
        }
    }
}
