package net.garrettsites.picturebook.model;

import net.garrettsites.picturebook.R;

public class ErrorCodes {
    public enum Error {
        NO_LOGGED_IN_ACCOUNT,
        NO_ALBUMS
    }

    /**
     * Converts an Error enum to an integer.
     * @param errorEnum The enum to convert to an integer.
     * @return The integer representation of the given enum.
     */
    public static int toInt(Error errorEnum) {
        return errorEnum.ordinal();
    }

    /**
     * Converts an integer to the Error (enum) representation.
     * @param errorCode The integer to convert to an Error enum.
     * @return The Error enum represented by the integer.
     */
    public static Error toEnum(int errorCode) {
        return Error.values()[errorCode];
    }

    /**
     * Gets a localized string resource to show the user for the given error code.
     * @param errorCode The error code to generate a string for.
     * @return A localized string that's safe to show to the user.
     */
    public static int getLocalizedErrorStringResource(int errorCode) {
        return getLocalizedErrorStringResource(ErrorCodes.toEnum(errorCode));
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
            case NO_ALBUMS:
                return R.string.error_no_albums;
            default:
                return R.string.generic_error;
        }
    }
}
